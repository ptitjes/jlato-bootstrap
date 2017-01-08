package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.tree.NodeList;
import org.jlato.tree.Tree;
import org.jlato.tree.TreeCombinators;
import org.jlato.tree.expr.AssignOp;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.stmt.ExpressionStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.jlato.cc.grammar.GExpansion.*;
import static org.jlato.cc.grammar.GProduction.production;
import static org.jlato.pattern.Quotes.expr;
import static org.jlato.pattern.Quotes.stmt;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class LeftRecursionElimination {

	public static final String RESULT_VAR = "__result";
	public static final String RHS_VAR = "__rhs";
	public static final String PRECEDENCE_VAR = "__precedence";

	public GProductions transform(GProductions productions) {
		List<GProduction> newProductions = new ArrayList<>();

		for (GProduction production : productions.getAll()) {
			if (!requiresElimination(production)) {
				newProductions.add(production);
				continue;
			}

			System.out.println("Triggered for left-recursion elimination: " + production.symbol);
			eliminateLeftRecursions(production, newProductions);
		}

		return new GProductions(newProductions);
	}

	private boolean requiresElimination(GProduction production) {
		GExpansion expansion = production.expansion;
		String symbol = production.symbol;
		return expansion.kind == GExpansion.Kind.Choice && requiresElimination(expansion, symbol);
	}

	private boolean requiresElimination(GExpansion choices, String symbol) {
		for (GExpansion choice : choices.children) {
			switch (choice.kind) {
				case Action:
					throw new IllegalStateException();

				case Sequence:
					if (prefixRecursiveCallIndex(choice, symbol) != -1)
						return true;
					break;

				case Choice:
					// Recurse
					requiresElimination(choice, symbol);
					break;

				case NonTerminal:
					if (choice.symbol.equals(symbol)) return true;
				default:
			}
		}
		return false;
	}

	private boolean isRecursiveCall(GExpansion expansion, String symbol) {
		return expansion.kind == GExpansion.Kind.NonTerminal && expansion.symbol.equals(symbol);
	}

	private void eliminateLeftRecursions(GProduction production, List<GProduction> newProductions) {
		GExpansion expansion = production.expansion;
		String symbol = production.symbol;
		Type resultType = production.returnType;

		if (expansion.kind != GExpansion.Kind.Choice) throw new IllegalArgumentException();

		// Collect choices split in (prefix, body, suffix)
		List<SplitProduction> splitProductions = new ArrayList<>();
		splitChoices(expansion, symbol, splitProductions);

		List<GExpansion> primaryOrPrefixChoices = new ArrayList<>();
		List<GExpansion> binaryTernaryOrSuffixChoices = new ArrayList<>();

		// Assign precedence based on choice order
		// and collect choices
		int precedence = splitProductions.size();
		for (SplitProduction splitProduction : splitProductions) {
			splitProduction.precedence = precedence--;

			GExpansion choice = splitProduction.toChoice(symbol);

			if (splitProduction.prefix == null) primaryOrPrefixChoices.add(choice);
			else binaryTernaryOrSuffixChoices.add(choice);
		}

		newProductions.add(production(symbol, resultType,
				production.hintParams,
				production.dataParams,
				listOf(variableDecls(resultType, RESULT_VAR)),
				sequence(
						nonTerminal(RESULT_VAR, symbol + "Rec", listOf(expr("0").build())),
						action("return " + RESULT_VAR + ";")
				)
		));

		// TODO clean declarations
		newProductions.add(production(symbol + "Rec", resultType,
				production.hintParams.append(
						formalParameter(primitiveType(Primitive.Int)).withId(variableDeclaratorId(name(PRECEDENCE_VAR)))
				),
				production.dataParams,
				production.declarations.appendAll(listOf(variableDecls(resultType, RESULT_VAR, RHS_VAR))),
				sequence(
						makeChoicesFor(primaryOrPrefixChoices),
						zeroOrMore(
								makeChoicesFor(binaryTernaryOrSuffixChoices)
						),
						action("return " + RESULT_VAR + ";")
				)
		));
	}

	private GExpansion makeChoicesFor(List<GExpansion> choices) {
		if (choices.size() == 1) return choices.get(0);
		return choice(choices.toArray(new GExpansion[choices.size()]));
	}

	private void splitChoices(GExpansion choices, String symbol, List<SplitProduction> splitProductions) {
		for (GExpansion choice : choices.children) {
			switch (choice.kind) {
				case Action:
					throw new IllegalStateException();

				case Sequence:
					splitProductions.add(splitSequence(choice, symbol));
					break;

				case Choice:
					// Recurse
					splitChoices(choice, symbol, splitProductions);
					break;

				case NonTerminal:
					// Strip away direct recursion
					if (choice.symbol.equals(symbol)) continue;
				default:
					splitProductions.add(splitSequence(sequence(choice), symbol));
			}
		}
	}

	private SplitProduction splitSequence(GExpansion sequence, String symbol) {
		int recursiveCallCounts = countRecursiveCalls(sequence, symbol);
		if (recursiveCallCounts > 3) throw new IllegalArgumentException();

		// Either one of the following:
		// - Primary or other
		// - Prefix or suffix operator
		// - Binary or ternary operator

		int prefixCallIndex = prefixRecursiveCallIndex(sequence, symbol);
		int suffixCallIndex = suffixRecursiveCallIndex(sequence, symbol);

		List<GExpansion> children = sequence.children;

		SplitProduction splitProduction = new SplitProduction();
		splitProduction.prefix = prefixCallIndex == -1 ? null : children.subList(0, prefixCallIndex + 1);
		splitProduction.body = children.subList(
				prefixCallIndex == -1 ? 0 : prefixCallIndex + 1,
				suffixCallIndex == -1 ? children.size() : suffixCallIndex
		);
		splitProduction.suffix = suffixCallIndex == -1 ? null : children.subList(suffixCallIndex, children.size());

		splitProduction.prefixVar = recursiveCallVar(splitProduction.prefix, symbol);
		splitProduction.suffixVar = recursiveCallVar(splitProduction.suffix, symbol);

		return splitProduction;
	}

	private int countRecursiveCalls(GExpansion sequence, String symbol) {
		int count = 0;
		for (GExpansion child : sequence.children) {
			if (isRecursiveCall(child, symbol)) count++;
		}
		return count;
	}

	private int prefixRecursiveCallIndex(GExpansion sequence, String symbol) {
		List<GExpansion> children = sequence.children;
		for (int i = 0; i < children.size(); i++) {
			GExpansion child = children.get(i);
			if (isRecursiveCall(child, symbol)) return i;
			else if (child.kind != GExpansion.Kind.Action) return -1;
		}
		return -1;
	}

	private int suffixRecursiveCallIndex(GExpansion sequence, String symbol) {
		List<GExpansion> children = sequence.children;
		for (int i = children.size() - 1; i >= 0; i--) {
			GExpansion child = children.get(i);
			if (isRecursiveCall(child, symbol)) return i;
			else if (child.kind != GExpansion.Kind.Action) return -1;
		}
		return -1;
	}

	private String recursiveCallVar(List<GExpansion> prefix, String symbol) {
		if (prefix == null) return null;
		for (GExpansion child : prefix) {
			if (isRecursiveCall(child, symbol)) return child.name;
		}
		return null;
	}

	static class SplitProduction {
		List<GExpansion> prefix;
		List<GExpansion> body;
		List<GExpansion> suffix;

		int precedence;
		String prefixVar;
		String suffixVar;

		public GExpansion toChoice(String symbol) {
			boolean explicitlyRightAssoc = body.stream().anyMatch(e ->
					(e.kind == Kind.NonTerminal || e.kind == Kind.Terminal) && e.rightAssociative
			);
			boolean leftAssoc = !explicitlyRightAssoc && !(prefix == null);

			List<GExpansion> expansions = new ArrayList<>();

			if (prefix != null) {
				GExpansion first = prefix.get(0);
				if (first.kind == Kind.Action && first.action.get(0).equals(stmt("run();").build()))
					expansions.add(action("lateRun();"));
			}

			expansions.addAll(this.body);

			if (suffix != null) {
				// This is a binary right-hand side operand

				int nextPrecedence = leftAssoc ? precedence + 1 : precedence;
				expansions.addAll(rewriteRecursiveCall(suffix, symbol, nextPrecedence));
			}

			expansions = renameVar(expansions, prefixVar, RESULT_VAR);
			expansions = renameVar(expansions, suffixVar, RHS_VAR);

			expansions = rewriteReturns(expansions);

			if (prefix != null) {
				// Insert guard
				// TODO Add a real guard predicate used by ALL* prediction
				expansions.add(0, action("if (" + PRECEDENCE_VAR + " > " + precedence + ") return " + RESULT_VAR + ";"));
			}

			return sequence(expansions);
		}

		private List<GExpansion> rewriteRecursiveCall(List<GExpansion> expansions, String symbol, int nextPrecedence) {
			return expansions.stream().map(child -> rewriteRecursiveCall(child, symbol, nextPrecedence))
					.collect(Collectors.toList());
		}

		private GExpansion rewriteRecursiveCall(GExpansion expansion, String symbol, int nextPrecedence) {
			switch (expansion.kind) {
				case Choice:
				case Sequence:
				case ZeroOrOne:
				case ZeroOrMore:
				case OneOrMore:
					return expansion.withChildren(rewriteRecursiveCall(expansion.children, symbol, nextPrecedence));

				case NonTerminal:
					if (expansion.symbol.equals(symbol)) {
						NodeList<Expr> newHints = expansion.hints.append(literalExpr(nextPrecedence));
						return nonTerminal(expansion.name, expansion.symbol + "Rec", newHints, expansion.arguments);
					} else return expansion;

				case Terminal:
				case Action:
					return expansion;

				default:
					throw new IllegalArgumentException();
			}
		}

		private List<GExpansion> rewriteReturns(List<GExpansion> expansions) {
			return expansions.stream().map(child -> rewriteReturns(child)).collect(Collectors.toList());
		}

		private GExpansion rewriteReturns(GExpansion expansion) {
			switch (expansion.kind) {
				case Choice:
				case Sequence:
				case ZeroOrOne:
				case ZeroOrMore:
				case OneOrMore:
					return expansion.withChildren(rewriteReturns(expansion.children));

				case NonTerminal:
				case Terminal:
					return expansion;

				case Action:
					return action(expansion.action.map(a ->
							((TreeCombinators<Stmt>) a).forAll(stmt("return $e;"), (t, s) ->
									expressionStmt(assignExpr(name(RESULT_VAR), AssignOp.Normal, s.get("e")))
							)
					));

				default:
					throw new IllegalArgumentException();
			}
		}

		private List<GExpansion> renameVar(List<GExpansion> expansions, String oldName, String newName) {
			if (oldName == null) return expansions;
			return expansions.stream().map(child -> renameVar(child, oldName, newName)).collect(Collectors.toList());
		}

		private GExpansion renameVar(GExpansion expansion, String oldName, String newName) {
			switch (expansion.kind) {
				case Choice:
				case Sequence:
				case ZeroOrOne:
				case ZeroOrMore:
				case OneOrMore:
					return expansion.withChildren(renameVar(expansion.children, oldName, newName));

				case NonTerminal:
					if (expansion.name.equals(oldName))
						return nonTerminal(newName, expansion.symbol,
								renameVar(expansion.hints, oldName, newName),
								renameVar(expansion.arguments, oldName, newName)
						);
					else
						return nonTerminal(expansion.name, expansion.symbol,
								renameVar(expansion.hints, oldName, newName),
								renameVar(expansion.arguments, oldName, newName)
						);

				case Terminal:
					return expansion;

				case Action:
					return action(renameVar(expansion.action, oldName, newName));

				default:
					throw new IllegalArgumentException();
			}
		}

		@SuppressWarnings("unchecked")
		private <T extends Tree> NodeList<T> renameVar(NodeList<T> list, String oldName, String newName) {
			if (list == null) return null;
			return list.map(a -> ((TreeCombinators<T>) a).forAll(expr(oldName), (t, s) -> name(newName)));
		}

		@Override
		public String toString() {
			return "SplitProduction{" +
					"\n\tprecedence=" + precedence +
					"\n\tprefix=" + (prefix == null ? "" : prefix.toString().replace("\n", "")) +
					"\n\tprefixVar=" + (prefixVar == null ? "" : prefixVar) +
					",\n\tbody=" + (body == null ? "" : body.toString().replace("\n", "")) +
					",\n\tsuffix=" + (suffix == null ? "" : suffix.toString().replace("\n", "")) +
					"\n\tsuffixVar=" + (suffixVar == null ? "" : suffixVar) +
					"\n}";
		}
	}

	private ExpressionStmt variableDecls(Type type, String... varNames) {
		return expressionStmt(variableDeclarationExpr(localVariableDecl(type).withVariables(listOf(
				Arrays.stream(varNames).map(n ->
						variableDeclarator(variableDeclaratorId(name(n)))
				).collect(Collectors.toList())
		))));
	}
}
