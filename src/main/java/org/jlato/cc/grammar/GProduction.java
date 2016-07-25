package org.jlato.cc.grammar;

import org.jlato.bootstrap.Utils;
import org.jlato.printer.Printer;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.stmt.Stmt;

import java.util.function.Function;

import static org.jlato.bootstrap.Utils.reify;
import static org.jlato.bootstrap.Utils.reifyList;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class GProduction {

	public final String symbol;
	public final MethodDecl signature;
	public final NodeList<Stmt> declarations;
	public final GExpansion expansion;

	public GProduction(String symbol, MethodDecl signature, NodeList<Stmt> declarations, GExpansion expansion) {
		this.symbol = symbol;
		this.signature = signature;
		this.declarations = declarations;
		this.expansion = expansion;
	}

	public GProduction rewrite(Function<GExpansion, GExpansion> f) {
		return new GProduction(symbol, signature, declarations, expansion.rewrite(f));
	}

	public GLocation location() {
		return new GLocation(expansion);
	}

	public ObjectCreationExpr toExpr() {
		return objectCreationExpr(qualifiedType(name("GProduction")))
				.withArgs(listOf(
						literalExpr(symbol),
						reify(signature).insertNewLineBefore(),
						reifyList("stmt", declarations).insertNewLineBefore(),
						expansion.toExpr().insertNewLineAfter()
				));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(Printer.printToString(signature));
		builder.append("\n");
		builder.append("\n");

		Utils.printIndented(declarations, builder, 1);

		builder.append(expansion.toString());
		return builder.toString();
	}
}
