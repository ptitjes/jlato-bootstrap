package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.type.Primitive;

import java.util.Map;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TokenTypePattern extends TypePattern.OfInterface<TreeClassDescriptor[]> {

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "public interface TokenType { ..$_ }";
	}

	@Override
	protected String makeDoc(InterfaceDecl decl, TreeClassDescriptor[] arg) {
		return "Token literal values and constants.";
	}

	@Override
	protected InterfaceDecl contributeBody(InterfaceDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		NodeList<MemberDecl> members = emptyList();
		NodeList<Expr> images = emptyList();

		boolean first = true;
		for (Map.Entry<String, Integer> entry : JavaGrammar.terminals.entrySet()) {
			String name = entry.getKey();
			members = members.append(fieldDecl(primitiveType(Primitive.Int)).withVariables(listOf(
					variableDeclarator(variableDeclaratorId(name(name)))
							.withInit(literalExpr(entry.getValue()))
			)));

			LiteralExpr<String> image = literalExpr(JavaGrammar.terminalsImage.get(name)).prependLeadingNewLine();
			images = images.append(image);
		}

		members = members.append(fieldDecl(arrayType(qualifiedType(name("String"))).withDims(listOf(arrayDim())))
				.withVariables(listOf(
						variableDeclarator(variableDeclaratorId(name("tokenImage")))
								.withInit(arrayInitializerExpr().withTrailingComma(true).withValues(images))
				))
		);

		decl = decl.withMembers(members);
		return decl;
	}
}
