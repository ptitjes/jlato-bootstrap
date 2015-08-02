package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.NodeOption.none;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class TreeFactoryClass extends Utils implements DeclPattern<TreeClassDescriptor[], ClassDecl> {

	@Override
	public Pattern<? extends Decl> matcher(TreeClassDescriptor[] arg) {
		return typeDecl("public abstract class TreeFactory { ..$_ }");
	}

	@Override
	public ClassDecl rewrite(ClassDecl decl, TreeClassDescriptor[] arg) {
		decl = classDecl()
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Abstract))
				.withName(new Name("TreeFactory"));

		if (GenSettings.generateDocs)
			decl = decl.insertLeadingComment("/** A factory for tree nodes. */");

		NodeList<MemberDecl> factoryMethods = NodeList.empty();
		for (TreeClassDescriptor descriptor : arg) {
			if (descriptor.customTailored) continue;
			if (descriptor.name.id().equals("LiteralExpr")) continue;

			factoryMethods = factoryMethods.append(generateFactoryMethod(descriptor, false));
		}

		return decl.withMembers(factoryMethods);
	}

	private MethodDecl generateFactoryMethod(TreeClassDescriptor descriptor, boolean noNulls) {
		NodeList<FormalParameter> params = NodeList.empty();
		NodeList<Expr> args = NodeList.empty();
		for (FormalParameter param : safeList(descriptor.parameters)) {
			Type type = param.type();
			if (type instanceof QualifiedType) {
				final QualifiedType qualifiedType = (QualifiedType) type;
				final String id = qualifiedType.name().id();

				switch (id) {
					case "NodeList":
						args = args.append(
								methodInvocationExpr()
										.withScope(some(qualifiedType.name()))
										.withTypeArgs(qualifiedType.typeArgs().get())
										.withName(new Name("empty"))
						);
						break;
					case "NodeOption":
						args = args.append(
								methodInvocationExpr()
										.withScope(some(qualifiedType.name()))
										.withTypeArgs(qualifiedType.typeArgs().get())
										.withName(new Name("none"))
						);
						break;
					case "NodeEither":
						// Special hack for LambdaExpr.body
						args = args.append(
								methodInvocationExpr()
										.withScope(some(qualifiedType.name()))
										.withTypeArgs(qualifiedType.typeArgs().get())
										.withName(new Name("right"))
										.withArgs(NodeList.of(
												methodInvocationExpr().withName(new Name("blockStmt"))
										))
						);
						break;
					default:
						if (noNulls) {
							params = params.append(param);
							args = args.append(param.id().name());
						} else {
							args = args.append(LiteralExpr.nullLiteral());
						}
						break;
				}
			} else if (type instanceof PrimitiveType) {
				Primitive primitive = ((PrimitiveType) type).primitive();
				switch (primitive) {
					case Boolean:
						args = args.append(LiteralExpr.of(false));
						break;
				}
			}
		}

		QualifiedType resultType = qualifiedType().withName(descriptor.name);
		Stmt creation = returnStmt().withExpr(
				some(objectCreationExpr().withType(resultType).withArgs(args).withBody(none()))
		);

		MethodDecl method = methodDecl()
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Static))
				.withType(resultType)
				.withName(new Name(lowerCaseFirst(descriptor.name.id())))
				.withParams(params)
				.withBody(some(blockStmt().withStmts(s -> s.append(creation))));

		if (GenSettings.generateDocs)
			method = method.insertLeadingComment(
					genDoc(method,
							"Creates " + descriptor.prefixedDescription() + ".",
							new String[0],
							"the new " + descriptor.description + " instance."
					)
			);

		return method;
	}
}
