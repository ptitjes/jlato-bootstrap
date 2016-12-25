package org.jlato.cc.grammar;

import org.jlato.bootstrap.Utils;
import org.jlato.printer.Printer;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Type;

import java.util.function.Function;

import static org.jlato.bootstrap.Utils.reify;
import static org.jlato.bootstrap.Utils.reifyList;
import static org.jlato.bootstrap.Utils.upperCaseFirst;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class GProduction {

	public static GProduction production(String symbol, Type returnType,
	                                     NodeList<FormalParameter> hintParams, NodeList<FormalParameter> dataParams,
	                                     NodeList<Stmt> declarations, GExpansion expansion) {
		return new GProduction(symbol, returnType, hintParams, dataParams, declarations, expansion);
	}

	public final String symbol;
	public final Type returnType;
	public final NodeList<FormalParameter> hintParams;
	public final NodeList<FormalParameter> dataParams;
	public final NodeList<Stmt> declarations;
	public final GExpansion expansion;
	public final boolean memoizeMatches;


	public GProduction(String symbol, Type returnType,
	                   NodeList<FormalParameter> hintParams, NodeList<FormalParameter> dataParams,
	                   NodeList<Stmt> declarations, GExpansion expansion) {
		this(symbol, returnType, hintParams, dataParams, declarations, expansion, false);
	}

	public GProduction(String symbol, Type returnType,
	                   NodeList<FormalParameter> hintParams, NodeList<FormalParameter> dataParams,
	                   NodeList<Stmt> declarations, GExpansion expansion, boolean memoizeMatches) {
		this.symbol = symbol;
		this.returnType = returnType == null ? voidType() : returnType;
		this.hintParams = hintParams;
		this.dataParams = dataParams;
		this.declarations = declarations;
		this.expansion = expansion;
		this.memoizeMatches = memoizeMatches;
	}

	public GProduction rewrite(Function<GExpansion, GExpansion> f) {
		return new GProduction(symbol, returnType, hintParams, dataParams, declarations, expansion.rewrite(f), memoizeMatches);
	}

	public GLocation location() {
		return new GLocation(this, null, expansion);
	}

	public GLocation location(GLocation parent) {
		return new GLocation(this, parent, expansion);
	}

	public MethodInvocationExpr toExpr() {
		MethodInvocationExpr creation = methodInvocationExpr(name("production"))
				.withArgs(listOf(
						literalExpr(symbol),
						returnType.kind() == Kind.VoidType ? nullLiteralExpr() : reify("type", returnType),
						reifyList("param", hintParams).prependLeadingNewLine(),
						reifyList("param", dataParams).prependLeadingNewLine(),
						reifyList("stmt", declarations).prependLeadingNewLine(),
						expansion.toExpr().appendTrailingNewLine()
				));
		if (memoizeMatches)
			creation = methodInvocationExpr(name("memoizeMatches"))
					.withScope(creation.appendTrailingNewLine());
		return creation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(Printer.printToString(
				methodDecl(returnType, name("parse" + upperCaseFirst(symbol)))
						.withParams(hintParams.appendAll(dataParams))
		));
		builder.append("\n");
		builder.append("\n");

		Utils.printIndented(declarations, builder, 1);

		builder.append(expansion.toString());
		return builder.toString();
	}

	public GProduction memoizeMatches() {
		return new GProduction(symbol, returnType, hintParams, dataParams, declarations, expansion, true);
	}
}
