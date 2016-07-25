package org.jlato.cc.grammar;

import org.jlato.bootstrap.Utils;
import org.jlato.tree.NodeList;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jlato.bootstrap.Utils.insertNewLineAfterLast;
import static org.jlato.bootstrap.Utils.lowerCaseFirst;
import static org.jlato.bootstrap.Utils.reifyList;
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
		return new GExpansion(Kind.Choice, children, null, null, null, -1);
	}

	public static GExpansion choice(GExpansion... children) {
		return new GExpansion(Kind.Choice, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion sequence(List<GExpansion> children) {
		return new GExpansion(Kind.Sequence, children, null, null, null, -1);
	}

	public static GExpansion sequence(GExpansion... children) {
		return new GExpansion(Kind.Sequence, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion zeroOrOne(List<GExpansion> children) {
		return new GExpansion(Kind.ZeroOrOne, children, null, null, null, -1);
	}

	public static GExpansion zeroOrOne(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrOne, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion zeroOrMore(List<GExpansion> children) {
		return new GExpansion(Kind.ZeroOrMore, children, null, null, null, -1);
	}

	public static GExpansion zeroOrMore(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrMore, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion oneOrMore(List<GExpansion> children) {
		return new GExpansion(Kind.OneOrMore, children, null, null, null, -1);
	}

	public static GExpansion oneOrMore(GExpansion... children) {
		return new GExpansion(Kind.OneOrMore, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion lookAhead(List<GExpansion> children) {
		return new GExpansion(Kind.LookAhead, children, null, null, null, -1);
	}

	public static GExpansion lookAhead(GExpansion... children) {
		return new GExpansion(Kind.LookAhead, Arrays.asList(children), null, null, null, -1);
	}

	public static GExpansion lookAhead(int amount) {
		return new GExpansion(Kind.LookAhead, null, null, null, null, amount);
	}

	public static GExpansion nonTerminal(String name, String symbol) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, null, -1);
	}

	public static GExpansion terminal(String name, String symbol) {
		return new GExpansion(Kind.Terminal, null, name, symbol, null, -1);
	}

	public static GExpansion action(NodeList<Stmt> action) {
		return new GExpansion(Kind.Action, null, null, null, action, -1);
	}

	public final Kind kind;
	public final List<GExpansion> children;
	public final String name;
	public final String symbol;
	public final NodeList<Stmt> action;
	public final int amount;

	public GExpansion(Kind kind, List<GExpansion> children, String name, String symbol, NodeList<Stmt> action, int amount) {
		this.kind = kind;
		this.children = children;
		this.name = name;
		this.symbol = symbol;
		this.action = action;
		this.amount = amount;
	}

	public GExpansion rewrite(Function<GExpansion, GExpansion> f) {
		switch (kind) {
			case LookAhead:
				if (amount != -1) {
					return f.apply(this);
				}
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				List<GExpansion> rewroteChildren =
						children.stream().map(e -> e.rewrite(f)).collect(Collectors.toList());
				return f.apply(new GExpansion(kind, rewroteChildren, null, null, null, amount));
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
			case NonTerminal:
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
		return methodInvocationExpr(methodName).withScope(name("GExpansion")).withArgs(args)
				.insertNewLineBefore();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.print(builder, 1);
		return builder.toString();
	}

	private void print(StringBuilder builder, int indent) {
		builder.append("\n");
		Utils.printIndent(builder, indent);
		builder.append(kind).append("(");

		switch (kind) {
			case LookAhead:
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
					child.print(builder, indent + 1);
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
				builder.append("\"");
				builder.append(symbol);
				builder.append("\"");
				break;
			case Action:
				builder.append("{");
				builder.append("\n");
				Utils.printIndented(action, builder, indent + 1);
				Utils.printIndent(builder, indent);
				builder.append("}");
				break;
			default:
		}

		builder.append(")");
	}
}
