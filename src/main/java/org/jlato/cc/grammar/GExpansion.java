package org.jlato.cc.grammar;

import org.jlato.bootstrap.Utils;
import org.jlato.printer.Printer;
import org.jlato.tree.NodeList;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.jlato.bootstrap.Utils.*;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class GExpansion {

	public enum Kind {
		Choice,
		Sequence,
		ZeroOrOne,
		ZeroOrMore,
		OneOrMore,
		LookAhead,
		NonTerminal,
		Terminal,
		Action,
	}

	public static GExpansion choice(List<GExpansion> children) {
		return new GExpansion(Kind.Choice, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion choice(GExpansion... children) {
		return new GExpansion(Kind.Choice, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion sequence(List<GExpansion> children) {
		return new GExpansion(Kind.Sequence, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion sequence(GExpansion... children) {
		return new GExpansion(Kind.Sequence, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion zeroOrOne(List<GExpansion> children) {
		return new GExpansion(Kind.ZeroOrOne, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion zeroOrOne(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrOne, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion zeroOrMore(List<GExpansion> children) {
		return new GExpansion(Kind.ZeroOrMore, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion zeroOrMore(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrMore, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion oneOrMore(List<GExpansion> children) {
		return new GExpansion(Kind.OneOrMore, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion oneOrMore(GExpansion... children) {
		return new GExpansion(Kind.OneOrMore, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion lookAhead(List<GExpansion> children) {
		return new GExpansion(Kind.LookAhead, children, null, null, null, null, null, -1, null);
	}

	public static GExpansion lookAhead(GExpansion... children) {
		return new GExpansion(Kind.LookAhead, Arrays.asList(children), null, null, null, null, null, -1, null);
	}

	public static GExpansion lookAhead(int amount) {
		return new GExpansion(Kind.LookAhead, null, null, null, null, null, null, amount, null);
	}

	public static GExpansion lookAhead(Expr semanticLookahead) {
		return new GExpansion(Kind.LookAhead, null, null, null, null, null, null, -1, semanticLookahead);
	}

	/*
		public static GExpansion nonTerminal(String symbol) {
			return new GExpansion(Kind.NonTerminal, null, null, symbol, null, null, null, -1, null);
		}
	*/
	public static GExpansion nonTerminal(String name, String symbol) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, null, null, null, -1, null);
	}

	public static GExpansion nonTerminal(String name, String symbol, NodeList<Expr> hints) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, hints, null, null, -1, null);
	}

	public static GExpansion nonTerminal(String name, String symbol, NodeList<Expr> hints, NodeList<Expr> arguments) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, hints, arguments, null, -1, null);
	}

	public static GExpansion terminal(String name, String symbol) {
		return new GExpansion(Kind.Terminal, null, name, symbol, null, null, null, -1, null);
	}

	public static GExpansion action(NodeList<Stmt> action) {
		return new GExpansion(Kind.Action, null, null, null, null, null, action, -1, null);
	}

	public final Kind kind;
	public final List<GExpansion> children;
	public final String name;
	public final String symbol;
	public final NodeList<Expr> hints;
	public final NodeList<Expr> arguments;
	public final NodeList<Stmt> action;
	public final int amount;
	public final Expr semanticLookahead;

	public GExpansion(Kind kind, List<GExpansion> children, String name, String symbol, NodeList<Expr> hints, NodeList<Expr> arguments, NodeList<Stmt> action, int amount, Expr semanticLookahead) {
		this.kind = kind;
		this.children = children;
		this.name = name;
		this.symbol = symbol;
		this.hints = hints;
		this.arguments = arguments;
		this.action = action;
		this.amount = amount;
		this.semanticLookahead = semanticLookahead;
	}

	public GExpansion rewrite(Function<GExpansion, GExpansion> f) {
		switch (kind) {
			case LookAhead:
				if (semanticLookahead != null || amount != -1) {
					return f.apply(this);
				}
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				List<GExpansion> rewroteChildren =
						children.stream().map(e -> e.rewrite(f)).collect(Collectors.toList());
				return f.apply(new GExpansion(kind, rewroteChildren, null, null, hints, null, null, amount, null));
			case NonTerminal:
			case Terminal:
			case Action:
				return f.apply(this);
			default:
		}
		return null;
	}

	public GLocation location() {
		return new GLocation(this);
	}

	public MethodInvocationExpr toExpr() {
		switch (kind) {
			case LookAhead:
				if (semanticLookahead != null) {
					return factoryCall(listOf(
							reify("expr", semanticLookahead).insertNewLineBefore().insertNewLineAfter()
					));
				}
				if (amount != -1) {
					return factoryCall(listOf(literalExpr(amount)));
				}
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				return factoryCall(insertNewLineAfterLast(listOf(
						children.stream().map(c -> c.toExpr()).collect(Collectors.toList())
				)));
			case NonTerminal: {
				NodeList<Expr> factoryArgs = listOf(
						name == null ? nullLiteralExpr() : literalExpr(name),
						literalExpr(symbol)
				);
				if (hints != null) {
					factoryArgs = factoryArgs.append(reifyList("expr", hints));
				} else if (arguments != null) {
					factoryArgs = factoryArgs.append(nullLiteralExpr());
				}
				if (arguments != null) {
					factoryArgs = factoryArgs.append(reifyList("expr", arguments));
				}
				return factoryCall(factoryArgs);
			}
			case Terminal:
				return factoryCall(listOf(name == null ? nullLiteralExpr() : literalExpr(name), literalExpr(symbol)));
			case Action:
				return factoryCall(listOf(
						reifyList("stmt", action).insertNewLineBefore().insertNewLineAfter()
				));
			default:
				throw new IllegalArgumentException();
		}
	}

	private MethodInvocationExpr factoryCall(NodeList<Expr> args) {
		Name methodName = name(lowerCaseFirst(kind.name()));
		return methodInvocationExpr(methodName).withArgs(args)
				.insertNewLineBefore();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.print(builder, 0, e -> true);
		return builder.toString();
	}

	public String toString(Predicate<GExpansion> filter) {
		StringBuilder builder = new StringBuilder();
		this.print(builder, 0, filter);
		return builder.toString();
	}

	private void print(StringBuilder builder, int indent, Predicate<GExpansion> filter) {
		if (!filter.test(this)) return;

		builder.append("\n");
		Utils.printIndent(builder, indent);
		builder.append(Utils.lowerCaseFirst(kind.toString())).append("(");

		switch (kind) {
			case LookAhead:
				if (semanticLookahead != null) {
					builder.append("{ ");
					builder.append(Printer.printToString(semanticLookahead, true).trim());
					builder.append(" }");
					break;
				}
				if (amount != -1) {
					builder.append(amount);
					break;
				}
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				for (GExpansion child : children) {
					child.print(builder, indent + 1, filter);
				}
				builder.append("\n");
				Utils.printIndent(builder, indent);
				break;
			case NonTerminal:
				if (name != null) builder.append(name).append(", ");
				builder.append(symbol);
				break;
			case Terminal:
				if (name != null) builder.append(name).append(", ");
				builder.append(symbol);
				break;
			case Action:
				builder.append("{");
				if (action.size() == 1 && action.first().kind() != org.jlato.tree.Kind.IfStmt) {
					builder.append(" ");
					builder.append(Printer.printToString(action.first(), true).trim());
					builder.append(" ");
				} else {
					builder.append("\n");
					Utils.printIndented(action, builder, indent + 1);
					Utils.printIndent(builder, indent);
				}
				builder.append("}");
				break;
			default:
		}

		builder.append(")");
	}
}
