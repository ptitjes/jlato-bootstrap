package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.*;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.NodeOption;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.ExpressionStmt;
import org.jlato.tree.stmt.IfStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.stmt.SwitchCase;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.Type;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ParserPattern extends TypePattern.OfClass<TreeClassDescriptor[]> {

	private GProductions productions;
	private final String implementationName;

	public ParserPattern(GProductions productions, String implementationName) {
		this.productions = productions;
		this.implementationName = implementationName;
	}

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "class " + implementationName + " extends ParserBaseALL { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor[] arg) {
		return "Internal implementation of the Java parser as a recursive descent parser using ALL(*) predictions.";
	}

	@Override
	protected ClassDecl contributeBody(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		importManager.addImports(listOf(
				importDecl(qualifiedName("org.jlato.internal.bu")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.coll")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.decl")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.expr")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.name")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.stmt")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.type")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.parser.Token")),
				importDecl(qualifiedName("org.jlato.internal.parser.TokenType")),
				importDecl(qualifiedName("org.jlato.tree.Problem.Severity")),
				importDecl(qualifiedName("org.jlato.parser.ParseException")),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.decl.ModifierKeyword")),
				importDecl(qualifiedName("org.jlato.tree.type.Primitive"))
		));

		productions = new GrammarAnalysis().analysis(productions);

		productions.recomputeReferences();
		List<GProduction> allProductions = productions.getAll();

		NodeList<MemberDecl> members = Trees.emptyList();
		members = members.append(memberDecl("protected Grammar initializeGrammar() { return new JavaGrammar(); }").build());

		members = members.append(grammarClass(importManager, allProductions));

		for (GProduction production : allProductions) {
			if (excluded(production)) continue;
			members = members.append(parseMethod(importManager, production));
		}

		return decl.withMembers(members);
	}

	private boolean excluded(GProduction production) {
		return false;
	}

	/* ALL(*) Grammar declaration */

	private int constantCount = 0;
	private int stateCount = 0;

	private MemberDecl grammarClass(ImportManager importManager, List<GProduction> allProductions) {
		importManager.addImports(listOf(
				importDecl(qualifiedName("org.jlato.internal.parser.all.Grammar")),
				importDecl(qualifiedName("org.jlato.internal.parser.all.GrammarProduction")).setOnDemand(true).setStatic(true),
				importDecl(qualifiedName("org.jlato.internal.parser.TokenType"))
		));

		NodeList<MemberDecl> members = Trees.emptyList();

		Map<String, MemberDecl> constants = new TreeMap<>();
		for (GProduction production : allProductions) {
			grammarConstants(production, constants);
		}
		for (MemberDecl memberDecl : constants.values()) {
			members = members.append(memberDecl);
		}

		for (GProduction production : allProductions) {
			stateCount += 2;
			members = grammarElements(production.expansion, members);
		}

		// Make initializePreductions method
		NodeList<Stmt> stmts = emptyList();
		for (GProduction production : allProductions) {
			stmts = grammarDefStmts(production, stmts);
		}

		members = members.append(
				methodDecl(voidType(), name("initializeProductions"))
						.withModifiers(listOf(Modifier.Protected))
						.withBody(blockStmt().withStmts(stmts))
		);

		ConstructorDecl constructor = constructorDecl(name("JavaGrammar")).withBody(blockStmt().withStmts(listOf(
				explicitConstructorInvocationStmt().withArgs(listOf(
						literalExpr(stateCount), literalExpr(constantCount)
				))
		)));

		return classDecl(name("JavaGrammar")).withExtendsClause(qType("Grammar"))
				.withModifiers(listOf(Modifier.Private, Modifier.Static))
				.withMembers(members.prepend(constructor));
	}

	private void grammarConstants(GProduction production, Map<String, MemberDecl> members) {
		String name = camelToConstant(lowerCaseFirst(production.symbol));
		if (!members.containsKey(name)) {
			members.put(name, fieldDecl(qType("int"))
					.withModifiers(listOf(Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(name)))
									.withInit(literalExpr(constantCount++))
					))
			);
		}

		grammarConstants(production.expansion, members);
	}

	private void grammarConstants(GExpansion expansion, Map<String, MemberDecl> members) {
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore: {
				String name = camelToConstant(lowerCaseFirst(expansion.constantName));
				if (!members.containsKey(name)) {
					members.put(name, fieldDecl(qType("int"))
							.withModifiers(listOf(Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(name)))
											.withInit(literalExpr(constantCount++))
							))
					);
				}
			}
			case Sequence: {
				for (GExpansion child : expansion.children) {
					if (isAction(child)) continue;
					grammarConstants(child, members);
				}
				break;
			}
			case NonTerminal: {
				String name = camelToConstant(lowerCaseFirst(expansion.symbol));
				if (!members.containsKey(name)) {
					members.put(name, fieldDecl(qType("int"))
							.withModifiers(listOf(Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(name)))
											.withInit(literalExpr(constantCount++))
							))
					);
				}
				break;
			}
			default:
		}
	}

	private NodeList<MemberDecl> grammarElements(GExpansion expansion, NodeList<MemberDecl> members) {
		switch (expansion.kind) {
			case Choice: {
				int count = 1;
				NodeList<Expr> childrenExprs = emptyList();
				for (GExpansion child : expansion.children) {
					stateCount++;

					childrenExprs = grammarElementExpression(child, childrenExprs);
					members = grammarElements(child, members);
				}

				members = members.append(fieldDecl(qType("Choice"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
										methodInvocationExpr(name("choice"))
												.withArgs(childrenExprs.prepend(literalExpr(expansion.constantName)))
								)
						))
				);
				break;
			}
			case Sequence: {
				NodeList<Expr> childrenExprs = emptyList();
				for (GExpansion child : expansion.children) {
					if (isAction(child)) continue;
					stateCount++;

					childrenExprs = grammarElementExpression(child, childrenExprs);
					members = grammarElements(child, members);
				}
				stateCount--;

				if (!expansion.constantName.contains("_")) {
					members = members.append(fieldDecl(qType("Sequence"))
							.withModifiers(listOf(Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
											methodInvocationExpr(name("sequence"))
													.withArgs(childrenExprs.prepend(literalExpr(expansion.constantName)))
									)
							))
					);
				}
				break;
			}
			case ZeroOrOne: {
				stateCount++;

				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = uniqueChild(expansion);

				childrenExprs = grammarElementExpression(child, childrenExprs);
				members = grammarElements(child, members);

				members = members.append(fieldDecl(qType("ZeroOrOne"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
										methodInvocationExpr(name("zeroOrOne"))
												.withArgs(childrenExprs.prepend(literalExpr(expansion.constantName)))
								)
						))
				);
				break;
			}
			case ZeroOrMore: {
				stateCount++;

				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = uniqueChild(expansion);

				childrenExprs = grammarElementExpression(child, childrenExprs);
				members = grammarElements(child, members);

				members = members.append(fieldDecl(qType("ZeroOrMore"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
										methodInvocationExpr(name("zeroOrMore"))
												.withArgs(childrenExprs.prepend(literalExpr(expansion.constantName)))
								)
						))
				);
				break;
			}
			case OneOrMore: {
				stateCount++;

				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = uniqueChild(expansion);

				childrenExprs = grammarElementExpression(child, childrenExprs);
				members = grammarElements(child, members);

				members = members.append(fieldDecl(qType("OneOrMore"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
										methodInvocationExpr(name("oneOrMore"))
												.withArgs(childrenExprs.prepend(literalExpr(expansion.constantName)))
								)
						))
				);
				break;
			}
			case NonTerminal: {
				String ntConstantName = camelToConstant(lowerCaseFirst(expansion.symbol));
				members = members.append(fieldDecl(qType("NonTerminal"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(expansion.constantName))).withInit(
										methodInvocationExpr(name("nonTerminal"))
												.withArgs(listOf(literalExpr(expansion.constantName), name(ntConstantName)))
								)
						))
				);
				break;
			}
			default:
		}
		return members;
	}

	private boolean isAction(GExpansion expansion) {
		return expansion.kind == GExpansion.Kind.Action;
	}

	private GExpansion uniqueChild(GExpansion expansion) {
		for (GExpansion child : expansion.children) {
			switch (child.kind) {
				case Action:
					break;
				default:
					return child;
			}
		}
		throw new IllegalArgumentException();
	}

	private NodeList<Expr> grammarElementExpression(GExpansion expansion, NodeList<Expr> childrenExprs) {
		NodeList<Expr> localChildrenExprs = emptyList();
		switch (expansion.kind) {
			case Choice:
				childrenExprs = childrenExprs.append(name(expansion.constantName));
				break;
			case Sequence: {
				for (GExpansion child : expansion.children) {
					if (isAction(child)) continue;

					localChildrenExprs = grammarElementExpression(child, localChildrenExprs);
				}
				childrenExprs = childrenExprs.append(methodInvocationExpr(name("sequence"))
						.withArgs(localChildrenExprs.prepend(literalExpr(expansion.constantName))));
				break;
			}
			case ZeroOrOne:
				childrenExprs = childrenExprs.append(name(expansion.constantName));
				break;
			case ZeroOrMore:
				childrenExprs = childrenExprs.append(name(expansion.constantName));
				break;
			case OneOrMore:
				childrenExprs = childrenExprs.append(name(expansion.constantName));
				break;
			case NonTerminal: {
				childrenExprs = childrenExprs.append(name(expansion.constantName));
				break;
			}
			case Terminal: {
				String name = expansion.symbol;
				childrenExprs = childrenExprs.append(methodInvocationExpr(name("terminal"))
						.withArgs(listOf(literalExpr(expansion.constantName), fieldAccessExpr(name(name)).withScope(name("TokenType"))))
				);
				break;
			}
			default:
		}
		return childrenExprs;
	}

	private NodeList<Stmt> grammarDefStmts(GProduction production, NodeList<Stmt> stmts) {
		String symbol = production.symbol;
		String productionConstantName = camelToConstant(lowerCaseFirst(symbol));
		stmts = stmts.append(
				expressionStmt(
						methodInvocationExpr(name("addProduction")).withArgs(listOf(
								name(productionConstantName),
								name(symbol),
								literalExpr(symbol.endsWith("Entry") && !symbol.equals("SwitchEntry"))
						))
				)
		);

		stmts = grammarDefStmts(production.expansion, stmts);
		return stmts;
	}

	private NodeList<Stmt> grammarDefStmts(GExpansion expansion, NodeList<Stmt> stmts) {
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore: {
				String constantName = camelToConstant(lowerCaseFirst(expansion.constantName));
				stmts = stmts.append(
						expressionStmt(
								methodInvocationExpr(name("addChoicePoint"))
										.withArgs(listOf(name(constantName), name(expansion.constantName)))
						)
				);
			}
			case Sequence: {
				for (GExpansion child : expansion.children) {
					if (isAction(child)) continue;
					stmts = grammarDefStmts(child, stmts);
				}
				break;
			}
			default:
		}
		return stmts;
	}

	/* Parse methods */

	private MethodDecl parseMethod(ImportManager importManager, GProduction production) {
		String symbol = production.symbol;
		Type type = production.returnType;

		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(production.declarations);
		stmts = stmts.append(tokenVarDeclaration());
		stmts = stmts.appendAll(parseStatementsFor(symbol, production.location(), production.hintParams, false));

		// Add push/pop callStack calls
		String ntName = camelToConstant(lowerCaseFirst(symbol));
		Expr constant = fieldAccessExpr(name(ntName)).withScope(name("JavaGrammar"));

		return methodDecl(type, name("parse" + upperCaseFirst(symbol)))
				.withModifiers(listOf(symbol.endsWith("Entry") ? Modifier.Public : Modifier.Protected))
				.withParams(production.hintParams.appendAll(production.dataParams))
				.withThrowsClause(listOf(qualifiedType(name("ParseException"))))
				.withBody(blockStmt().withStmts(stmts))
				.appendLeadingComment(production.expansion.toString(), true);
	}

	private NodeList<Stmt> parseStatementsFor(String symbol, GLocation location, NodeList<FormalParameter> hintParams, boolean optional) {
		NodeList<Stmt> stmts = emptyList();
		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(parseStatementsForChildren(symbol, location, hintParams, false));
				break;
			case ZeroOrOne: {
				if (expansion.children.size() == 1 && expansion.children.get(0).kind == GExpansion.Kind.Choice) {
					stmts = stmts.appendAll(parseStatementsForChildren(symbol, location, hintParams, true));
				} else {
					stmts = stmts.append(tokenVarUpdate());
					stmts = stmts.append(ifStmt(
							makeKleeneCondition(expansion.constantName, location, hintParams),
							blockStmt().withStmts(parseStatementsForChildren(symbol, location, hintParams, false))
					));
				}
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(tokenVarUpdate());
				stmts = stmts.append(whileStmt(
						makeKleeneCondition(expansion.constantName, location, hintParams),
						blockStmt().withStmts(parseStatementsForChildren(symbol, location, hintParams, false).append(tokenVarUpdate()))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(parseStatementsForChildren(symbol, location, hintParams, false).append(tokenVarUpdate())),
						makeKleeneCondition(expansion.constantName, location, hintParams)
				));
				break;
			}
			case Choice: {
				List<Set<String>> ll1DecisionTerminals = computeChoiceLL1DecisionTerminals(location);
				if (ll1DecisionTerminals != null) {

					NodeList<IfStmt> cases = emptyList();

					int count = 1;
					for (GLocation child : location.allChildren()) {
						Set<String> terminals = ll1DecisionTerminals.get(count++ - 1);

						// Produce statements
						NodeList<Stmt> caseStmts = parseStatementsFor(symbol, child, hintParams, optional);

						// Produce case
						cases = cases.append(ifStmt(matchExpression(terminals), blockStmt().withStmts(caseStmts)));
					}

					NodeOption<Stmt> stmt = cases.foldRight(
							optional ? Trees.<Stmt>none() : some(throwStmt(
									methodInvocationExpr(name("produceParseException"))
											.withArgs(listOf(prefixedAndOrderedConstants(firstTerminalsOf(location))))
							)),
							(ifStmt, elseStmt) -> some(ifStmt.withElseStmt(elseStmt))
					);

					stmts = stmts.appendAll(listOf(
							tokenVarUpdate(),
							stmt.get()
					));
				} else {
					Expr selector = predict(expansion.constantName, hintParams);

					NodeList<SwitchCase> cases = emptyList();

					int count = 1;
					for (GLocation child : location.allChildren()) {
						LiteralExpr<Integer> label = literalExpr(count++);

						NodeList<Stmt> caseStmts = parseStatementsFor(symbol, child, hintParams, optional);
						if (caseStmts.last().kind() != Kind.ReturnStmt) caseStmts = caseStmts.append(breakStmt());

						cases = cases.append(switchCase().withLabel(label).withStmts(caseStmts));
					}

					if (!optional) {
						cases = cases.append(switchCase().withStmts(listOf(throwStmt(
								methodInvocationExpr(name("produceParseException"))
										.withArgs(listOf(prefixedAndOrderedConstants(firstTerminalsOf(location))))
						))));
					}

					stmts = stmts.append(switchStmt(selector).withCases(cases));
				}
				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("parse" + upperCaseFirst(expansion.symbol)))
						.withArgs(expansion.hints.appendAll(expansion.arguments));
				ExpressionStmt callStmt = expressionStmt(
						expansion.name == null ? call : assignExpr(name(expansion.name), AssignOp.Normal, call)
				);

				stmts = stmts.append(pushCallStack(expansion.constantName));
				stmts = stmts.append(callStmt);
				stmts = stmts.append(popCallStack());
				break;
			}
			case Terminal: {
				Expr argument = prefixedConstant(expansion.symbol);
				Expr call = methodInvocationExpr(name("consume")).withArgs(listOf(argument));
				ExpressionStmt callStmt = expressionStmt(
						expansion.name == null ? call : assignExpr(name(expansion.name), AssignOp.Normal, call)
				);

				stmts = stmts.append(callStmt);
				break;
			}
			case Action: {
				stmts = stmts.appendAll(expansion.action);
				break;
			}
			default:
		}
		return stmts;
	}

	private static final Name TOKEN_VAR_NAME = name("__token");

	private Stmt tokenVarDeclaration() {
		return expressionStmt(variableDeclarationExpr(
				localVariableDecl(primitiveType(Primitive.Int)).withVariables(listOf(
						variableDeclarator(variableDeclaratorId(TOKEN_VAR_NAME))
				))
		));
	}

	private Stmt tokenVarUpdate() {
		return expressionStmt(assignExpr(TOKEN_VAR_NAME, AssignOp.Normal, getTokenKind(literalExpr(0))));
	}

	private Expr matchExpression(Collection<String> terminals) {
		return matchExpression(prefixedAndOrderedConstants(terminals));
	}

	private Expr matchExpression(List<Expr> terminalNames) {
		// TODO Have a better heuristic to choose between explicit matches and bit operations
		if (terminalNames.size() == 1)
			return matchExpression(terminalNames.get(0));
		else if (terminalNames.size() == 2)
			return binaryExpr(matchExpression(terminalNames.get(0)), BinaryOp.Or, matchExpression(terminalNames.get(1)));
		else {
			Expr test = null;
			Expr mask = null;
			int firstValue = -1;

			for (Expr terminal : terminalNames) {
				int value = terminalTable.get(terminal);

				if (firstValue == -1) firstValue = value;
				if (value >= firstValue + 64) {
					Expr thisTest = testFor(mask, firstValue);

					if (test == null) test = thisTest;
					else test = binaryExpr(test, BinaryOp.Or, thisTest);

					firstValue = value;
					mask = null;
				}

				Expr thisMask = maskFor(terminal, firstValue);
				if (mask == null) mask = thisMask;
				else mask = binaryExpr(mask, BinaryOp.BinOr, thisMask);
			}

			if (mask != null) {
				Expr thisTest = testFor(mask, firstValue);

				if (test == null) test = thisTest;
				else test = binaryExpr(parenthesizedExpr(test), BinaryOp.Or, parenthesizedExpr(thisTest));
			}

			return test;
		}
	}

	private Expr testFor(Expr mask, int firstValue) {
		return binaryExpr(
				binaryExpr(
						parenthesizedBinExpr(
								binaryExpr(TOKEN_VAR_NAME, BinaryOp.Minus, literalExpr(firstValue)),
								BinaryOp.BinAnd,
								unaryExpr(UnaryOp.Inverse, literalExpr(63))
						),
						BinaryOp.Equal,
						literalExpr(0)
				),
				BinaryOp.And,
				binaryExpr(
						parenthesizedBinExpr(maskFor(TOKEN_VAR_NAME, firstValue), BinaryOp.BinAnd, parenthesizedExpr(mask)),
						BinaryOp.NotEqual,
						literalExpr(0)
				)
		);
	}

	private Expr maskFor(Expr name, int firstValue) {
		return binaryExpr(literalExpr(1L), BinaryOp.LeftShift, binaryExpr(name, BinaryOp.Minus, literalExpr(firstValue)));
	}

	private Expr parenthesizedBinExpr(Expr left, BinaryOp op, Expr right) {
		return parenthesizedExpr(binaryExpr(left, op, right));
	}

	private Expr matchExpression(Expr terminalName) {
		return binaryExpr(TOKEN_VAR_NAME, BinaryOp.Equal, terminalName);
	}

	private NodeList<Stmt> parseStatementsForChildren(String symbol, GLocation location, NodeList<FormalParameter> hintParams, boolean optional) {
		NodeList<Stmt> stmts = emptyList();
		for (GLocation child : location.allChildren()) {
			stmts = stmts.appendAll(parseStatementsFor(symbol, child, hintParams, optional));
		}
		return stmts;
	}

	private List<Set<String>> computeChoiceLL1DecisionTerminals(GLocation location) {
		List<GLocation> children = location.allChildren().asList();

		List<Set<String>> terminalSets = new ArrayList<>(children.size());
		for (GLocation child : children) {
			GContinuation continuation = new GContinuation(child).moveToNextTerminals(productions);
			if (continuation == null) return null;
			terminalSets.add(continuation.asTerminals());
		}

		// Verify that the terminals don't intersect pairwise
		for (int i = 0; i < terminalSets.size(); i++) {
			Set<String> terminalSet1 = terminalSets.get(i);
			for (int j = i + 1; j < terminalSets.size(); j++) {
				Set<String> terminalSet2 = terminalSets.get(j);
				if (terminalSet1.stream().anyMatch(terminalSet2::contains)) return null;
			}
		}

		return terminalSets;
	}

	private Expr makeKleeneCondition(String namePrefix, GLocation location, NodeList<FormalParameter> hintParams) {
		List<String> ll1DecisionTerminals = computeKleeneLL1DecisionTerminals(location);
		return ll1DecisionTerminals != null ?
				matchExpression(ll1DecisionTerminals) :
				binaryExpr(predict(namePrefix, hintParams), BinaryOp.Equal, literalExpr(1));
	}

	private List<String> computeKleeneLL1DecisionTerminals(GLocation location) {
		GLocation firstChild = location.firstChild();
		GLocation nextSibling = location.nextSibling();

		List<String> ll1DecisionTerminals = null;
		if (firstChild != null && nextSibling != null) {
			GContinuation inside = new GContinuation(firstChild).moveToNextTerminals(productions);
			GContinuation after = new GContinuation(nextSibling).moveToNextTerminals(productions);
			if (inside != null && after != null && !inside.intersects(after))
				ll1DecisionTerminals = new ArrayList<>(inside.asTerminals());
		}
		return ll1DecisionTerminals;
	}

	private Expr predict(String namePrefix, NodeList<FormalParameter> hintParams) {
		String name = camelToConstant(lowerCaseFirst(namePrefix));
		Expr constant = fieldAccessExpr(name(name)).withScope(name("JavaGrammar"));

		return methodInvocationExpr(name("predict")).withArgs(listOf(constant));
	}

	private Expr getTokenKind(Expr lookahead) {
		return fieldAccessExpr(name("kind")).withScope(methodInvocationExpr(name("getToken")).withArgs(listOf(lookahead)));
	}

	private Expr match(Collection<String> tokens, Expr lookahead) {
		List<Expr> terminals = prefixedAndOrderedConstants(tokens);
		return methodInvocationExpr(name("match")).withArgs(listOf(terminals).prepend(lookahead));
	}

	private Stmt pushCallStack(String name) {
		Expr constant = fieldAccessExpr(name(name)).withScope(name("JavaGrammar"));

		return expressionStmt(methodInvocationExpr(name("pushCallStack")).withArgs(listOf(constant)));
	}

	private Stmt popCallStack() {
		return expressionStmt(methodInvocationExpr(name("popCallStack")));
	}

	private List<Expr> prefixedAndOrderedConstants(Collection<String> tokens) {
		return tokens.stream()
				.filter(t -> t != null)
				.map(this::prefixedConstant)
				.sorted(terminalComparator)
				.collect(Collectors.toList());
	}

	private FieldAccessExpr prefixedConstant(String token) {
		return fieldAccessExpr(name(token)).withScope(name("TokenType"));
	}

	private Set<String> firstTerminalsOf(GLocation location) {
		GContinuation c = new GContinuation(location);
		c.moveToNextTerminals(productions);
		return c.asTerminals();
	}

	private Comparator<Expr> terminalComparator = new Comparator<Expr>() {
		@Override
		public int compare(Expr o1, Expr o2) {
			return terminalTable.get(o1).compareTo(terminalTable.get(o2));
		}
	};

	private Map<Expr, Integer> terminalTable = new HashMap<>();

	private void addTerminal(String name, int value) {
		terminalTable.put(prefixedConstant(name), value);
	}

	{
		addTerminal("EOF", 0);
		addTerminal("WHITESPACE", 1);
		addTerminal("NEWLINE", 2);
		addTerminal("SINGLE_LINE_COMMENT", 3);
		addTerminal("JAVA_DOC_COMMENT", 6);
		addTerminal("MULTI_LINE_COMMENT", 7);
		addTerminal("ABSTRACT", 9);
		addTerminal("ASSERT", 10);
		addTerminal("BOOLEAN", 11);
		addTerminal("BREAK", 12);
		addTerminal("BYTE", 13);
		addTerminal("CASE", 14);
		addTerminal("CATCH", 15);
		addTerminal("CHAR", 16);
		addTerminal("CLASS", 17);
		addTerminal("CONST", 18);
		addTerminal("CONTINUE", 19);
		addTerminal("DEFAULT", 20);
		addTerminal("DO", 21);
		addTerminal("DOUBLE", 22);
		addTerminal("ELSE", 23);
		addTerminal("ENUM", 24);
		addTerminal("EXTENDS", 25);
		addTerminal("FALSE", 26);
		addTerminal("FINAL", 27);
		addTerminal("FINALLY", 28);
		addTerminal("FLOAT", 29);
		addTerminal("FOR", 30);
		addTerminal("GOTO", 31);
		addTerminal("IF", 32);
		addTerminal("IMPLEMENTS", 33);
		addTerminal("IMPORT", 34);
		addTerminal("INSTANCEOF", 35);
		addTerminal("INT", 36);
		addTerminal("INTERFACE", 37);
		addTerminal("LONG", 38);
		addTerminal("NATIVE", 39);
		addTerminal("NEW", 40);
		addTerminal("NULL", 41);
		addTerminal("PACKAGE", 42);
		addTerminal("PRIVATE", 43);
		addTerminal("PROTECTED", 44);
		addTerminal("PUBLIC", 45);
		addTerminal("RETURN", 46);
		addTerminal("SHORT", 47);
		addTerminal("STATIC", 48);
		addTerminal("STRICTFP", 49);
		addTerminal("SUPER", 50);
		addTerminal("SWITCH", 51);
		addTerminal("SYNCHRONIZED", 52);
		addTerminal("THIS", 53);
		addTerminal("THROW", 54);
		addTerminal("THROWS", 55);
		addTerminal("TRANSIENT", 56);
		addTerminal("TRUE", 57);
		addTerminal("TRY", 58);
		addTerminal("VOID", 59);
		addTerminal("VOLATILE", 60);
		addTerminal("WHILE", 61);
		addTerminal("LONG_LITERAL", 62);
		addTerminal("INTEGER_LITERAL", 63);
		addTerminal("FLOAT_LITERAL", 68);
		addTerminal("DOUBLE_LITERAL", 69);
		addTerminal("CHARACTER_LITERAL", 78);
		addTerminal("STRING_LITERAL", 79);
		addTerminal("LPAREN", 80);
		addTerminal("RPAREN", 81);
		addTerminal("LBRACE", 82);
		addTerminal("RBRACE", 83);
		addTerminal("LBRACKET", 84);
		addTerminal("RBRACKET", 85);
		addTerminal("SEMICOLON", 86);
		addTerminal("COMMA", 87);
		addTerminal("DOT", 88);
		addTerminal("AT", 89);
		addTerminal("ASSIGN", 90);
		addTerminal("LT", 91);
		addTerminal("BANG", 92);
		addTerminal("TILDE", 93);
		addTerminal("HOOK", 94);
		addTerminal("COLON", 95);
		addTerminal("EQ", 96);
		addTerminal("LE", 97);
		addTerminal("GE", 98);
		addTerminal("NE", 99);
		addTerminal("SC_OR", 100);
		addTerminal("SC_AND", 101);
		addTerminal("INCR", 102);
		addTerminal("DECR", 103);
		addTerminal("PLUS", 104);
		addTerminal("MINUS", 105);
		addTerminal("STAR", 106);
		addTerminal("SLASH", 107);
		addTerminal("BIT_AND", 108);
		addTerminal("BIT_OR", 109);
		addTerminal("XOR", 110);
		addTerminal("REM", 111);
		addTerminal("LSHIFT", 112);
		addTerminal("PLUSASSIGN", 113);
		addTerminal("MINUSASSIGN", 114);
		addTerminal("STARASSIGN", 115);
		addTerminal("SLASHASSIGN", 116);
		addTerminal("ANDASSIGN", 117);
		addTerminal("ORASSIGN", 118);
		addTerminal("XORASSIGN", 119);
		addTerminal("REMASSIGN", 120);
		addTerminal("LSHIFTASSIGN", 121);
		addTerminal("RSIGNEDSHIFTASSIGN", 122);
		addTerminal("RUNSIGNEDSHIFTASSIGN", 123);
		addTerminal("ELLIPSIS", 124);
		addTerminal("ARROW", 125);
		addTerminal("DOUBLECOLON", 126);
		addTerminal("RUNSIGNEDSHIFT", 127);
		addTerminal("RSIGNEDSHIFT", 128);
		addTerminal("GT", 129);
		addTerminal("NODE_VARIABLE", 130);
		addTerminal("NODE_LIST_VARIABLE", 131);
		addTerminal("IDENTIFIER", 132);
	}
}
