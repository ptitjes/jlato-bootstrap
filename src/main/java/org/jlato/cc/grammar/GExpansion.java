package org.jlato.cc.grammar;

import org.jlato.bootstrap.Utils;
import org.jlato.pattern.Quotes;
import org.jlato.printer.Printer;
import org.jlato.tree.NodeList;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;

import java.util.*;
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
		NonTerminal,
		Terminal,
		Action,
	}

	public static GExpansion choice(GExpansion... children) {
		return new GExpansion(Kind.Choice, Arrays.asList(children), null, null, null, null, null);
	}

	public static GExpansion sequence(GExpansion... children) {
		return sequence(Arrays.asList(children));
	}

	private static GExpansion sequence(List<GExpansion> children) {
		return new GExpansion(Kind.Sequence, children, null, null, null, null, null);
	}

	public static GExpansion zeroOrOne(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrOne, ensureUniqueChild(Arrays.asList(children)), null, null, null, null, null);
	}

	public static GExpansion zeroOrMore(GExpansion... children) {
		return new GExpansion(Kind.ZeroOrMore, ensureUniqueChild(Arrays.asList(children)), null, null, null, null, null);
	}

	public static GExpansion oneOrMore(GExpansion... children) {
		return new GExpansion(Kind.OneOrMore, ensureUniqueChild(Arrays.asList(children)), null, null, null, null, null);
	}

	private static List<GExpansion> ensureUniqueChild(List<GExpansion> children) {
		return children.size() == 1 ? children : Collections.singletonList(sequence(children));
	}

	public static GExpansion nonTerminal(String symbol) {
		return new GExpansion(Kind.NonTerminal, null, null, symbol, null, null, null);
	}

	public static GExpansion nonTerminal(String name, String symbol) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, null, null, null);
	}

	public static GExpansion nonTerminal(String name, String symbol, NodeList<Expr> hints) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, hints, null, null);
	}

	public static GExpansion nonTerminal(String name, String symbol, NodeList<Expr> hints, NodeList<Expr> arguments) {
		return new GExpansion(Kind.NonTerminal, null, name, symbol, hints, arguments, null);
	}

	public static GExpansion terminal(String symbol) {
		return new GExpansion(Kind.Terminal, null, null, symbol, null, null, null);
	}

	public static GExpansion terminal(String name, String symbol) {
		return new GExpansion(Kind.Terminal, null, name, symbol, null, null, null);
	}

	public static GExpansion action(NodeList<Stmt> action) {
		return new GExpansion(Kind.Action, null, null, null, null, null, action);
	}

	public static GExpansion action(String action) {
		return new GExpansion(Kind.Action, null, null, null, null, null, listOf(Quotes.stmt(action).build()));
	}

	public final Kind kind;
	public final List<GExpansion> children;
	public final String name;
	public final String symbol;
	public final NodeList<Expr> hints;
	public final NodeList<Expr> arguments;
	public final NodeList<Stmt> action;

	public String constantName;
	public int constantId;

	public boolean canUseLL1;
	public List<Set<String>> ll1Decisions;

	public GrammarState startState;
	public GrammarState endState;
	public GrammarState choiceState;

	public GExpansion(Kind kind, List<GExpansion> children, String name, String symbol, NodeList<Expr> hints, NodeList<Expr> arguments, NodeList<Stmt> action) {
		this.kind = kind;
		this.children = children;
		this.name = name;
		this.symbol = symbol;
		this.hints = hints == null ? emptyList() : hints;
		this.arguments = arguments == null ? emptyList() : arguments;
		this.action = action;
	}

	public GExpansion rewrite(Function<GExpansion, GExpansion> f) {
		switch (kind) {
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				List<GExpansion> rewroteChildren =
						children.stream().map(e -> e.rewrite(f)).filter(Objects::nonNull).collect(Collectors.toList());
				return f.apply(new GExpansion(kind, rewroteChildren, name, symbol, hints, arguments, action));
			case NonTerminal:
			case Terminal:
			case Action:
				return f.apply(this);
			default:
		}
		return null;
	}

	public MethodInvocationExpr toExpr() {
		switch (kind) {
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
						literalExpr(symbol)
				);
				if (name != null) {
					factoryArgs = factoryArgs.prepend(literalExpr(name));
				}
				if (hints != null && !hints.isEmpty()) {
					factoryArgs = factoryArgs.append(reifyList("expr", hints));
				} else if (arguments != null && !arguments.isEmpty()) {
					factoryArgs = factoryArgs.append(nullLiteralExpr());
				}
				if (arguments != null && !arguments.isEmpty()) {
					factoryArgs = factoryArgs.append(reifyList("expr", arguments));
				}
				return factoryCall(factoryArgs);
			}
			case Terminal: {
				NodeList<Expr> factoryArgs = listOf(
						literalExpr(symbol)
				);
				if (name != null) {
					factoryArgs = factoryArgs.prepend(literalExpr(name));
				}
				return factoryCall(factoryArgs);
			}
			case Action:
				if (action.size() > 1) return factoryCall(listOf(reifyList("stmt", action)));
				else return factoryCall(listOf(literalExpr(Printer.printToString(action.first(), true))));
			default:
				throw new IllegalArgumentException();
		}
	}

	private MethodInvocationExpr factoryCall(NodeList<Expr> args) {
		Name methodName = name(lowerCaseFirst(kind.name()));
		return methodInvocationExpr(methodName).withArgs(args)
				.prependLeadingNewLine();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.print(builder, 0, e -> true);
		return builder.toString();
	}

	private void print(StringBuilder builder, int indent, Predicate<GExpansion> filter) {
		if (!filter.test(this)) return;

		builder.append("\n");
		Utils.printIndent(builder, indent);
		String kindString = kind.toString();
		builder.append(Utils.lowerCaseFirst(kindString)).append("(");

		switch (kind) {
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
				if (this.name != null) builder.append(this.name).append(", ");
				builder.append(symbol);
				break;
			case Terminal:
				if (this.name != null) builder.append(this.name).append(", ");
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
