package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.rewrite.Quotes.memberDecl;
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
	public ClassDecl rewrite(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		decl = classDecl(new Name("TreeFactory"))
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Abstract));

		if (GenSettings.generateDocs)
			decl = decl.insertLeadingComment("/** A factory for tree nodes. */");

		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.bu.Literals")));

		NodeList<MemberDecl> factoryMethods = NodeList.empty();
		for (TreeClassDescriptor descriptor : arg) {
			if (descriptor.customTailored) continue;

			importManager.addImport(importDecl(descriptor.packageQualifiedName(importManager)).setOnDemand(true));

			if (!descriptor.name.id().equals("LiteralExpr")) {
				if (!noNullsFormHasNoParams(descriptor))
					factoryMethods = factoryMethods.append(generateFactoryMethod(descriptor, false));
				factoryMethods = factoryMethods.append(generateFactoryMethod(descriptor, true));
			}

			if (descriptor.name.id().equals("QualifiedName")) {
				factoryMethods = factoryMethods.append(memberDecl("public static QualifiedName qualifiedName(String nameString) {\n" +
						"\t\tfinal String[] split = nameString.split(\"\\\\.\");\n" +
						"\t\tQualifiedName name = null;\n" +
						"\t\tfor (String part : split) {\n" +
						"\t\t\tname = new QualifiedName(NodeOption.of(name), new Name(part));\n" +
						"\t\t}\n" +
						"\t\treturn name;\n" +
						"\t}").build());
			} else if (descriptor.name.id().equals("LiteralExpr")) {
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Void> nullLiteralExpr() {\n" +
						"\treturn new LiteralExpr<Void>(Void.class, Literals.from(Void.class, null));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Boolean> literalExpr(boolean value) {\n" +
						"\treturn new LiteralExpr<Boolean>(Boolean.class, Literals.from(Boolean.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Integer> literalExpr(int value) {\n" +
						"\treturn new LiteralExpr<Integer>(Integer.class, Literals.from(Integer.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Long> literalExpr(long value) {\n" +
						"\treturn new LiteralExpr<Long>(Long.class, Literals.from(Long.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Float> literalExpr(float value) {\n" +
						"\treturn new LiteralExpr<Float>(Float.class, Literals.from(Float.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Double> literalExpr(double value) {\n" +
						"\treturn new LiteralExpr<Double>(Double.class, Literals.from(Double.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Character> literalExpr(char value) {\n" +
						"\treturn new LiteralExpr<Character>(Character.class, Literals.from(Character.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<String> literalExpr(String value) {\n" +
						"\treturn new LiteralExpr<String>(String.class, Literals.from(String.class, value));\n" +
						"}").build());
			}
		}

		return decl.withMembers(factoryMethods);
	}

	private boolean noNullsFormHasNoParams(TreeClassDescriptor descriptor) {
		int count = 0;
		int index = 0;
		for (FormalParameter param : safeList(descriptor.parameters)) {
			Type type = param.type();

			final Expr defaultValue = descriptor.defaultValues.get(index);
			if (defaultValue == null) {
				if (type instanceof QualifiedType) {
					final QualifiedType qualifiedType = (QualifiedType) type;
					switch (qualifiedType.name().id()) {
						case "NodeList":
							break;
						case "NodeOption":
							break;
						default:
							count++;
							break;
					}
				}
			}
			index++;
		}
		return count == 0;
	}

	private MethodDecl generateFactoryMethod(TreeClassDescriptor descriptor, boolean noNulls) {
		NodeList<FormalParameter> params = NodeList.empty();
		NodeList<Expr> args = NodeList.empty();
		int index = 0;
		for (FormalParameter param : safeList(descriptor.parameters)) {
			Type type = param.type();

			final Expr defaultValue = descriptor.defaultValues.get(index);
			if (defaultValue != null) {
				args = args.append(defaultValue);
			} else if (type instanceof QualifiedType) {
				final QualifiedType qualifiedType = (QualifiedType) type;
				switch (qualifiedType.name().id()) {
					case "NodeList":
						args = args.append(
								methodInvocationExpr(new Name("empty"))
										.withScope(some(qualifiedType.name()))
										.withTypeArgs(qualifiedType.typeArgs().get())
						);
						break;
					case "NodeOption":
						args = args.append(
								methodInvocationExpr(new Name("none"))
										.withScope(some(qualifiedType.name()))
										.withTypeArgs(qualifiedType.typeArgs().get())
						);
						break;
					default:
						if (noNulls) {
							params = params.append(param);
							args = args.append(param.id().name());
						} else {
							args = args.append(nullLiteralExpr());
						}
						break;
				}
			} else if (type instanceof PrimitiveType) {
				Primitive primitive = ((PrimitiveType) type).primitive();
				switch (primitive) {
					case Boolean:
						args = args.append(literalExpr(false));
						break;
				}
			}

			index++;
		}

		QualifiedType resultType = qualifiedType(descriptor.name);
		Stmt creation = returnStmt().withExpr(
				some(objectCreationExpr(resultType).withArgs(args).withBody(none()))
		);

		MethodDecl method = methodDecl(resultType, new Name(lowerCaseFirst(descriptor.name.id())))
				.withModifiers(m -> noNulls ?
								m.append(Modifier.Public).append(Modifier.Static) :
								m.append(deprecatedAnn()).append(Modifier.Public).append(Modifier.Static)
				)
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
