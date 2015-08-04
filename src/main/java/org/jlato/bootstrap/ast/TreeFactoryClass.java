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
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;
import org.jlato.util.Function1;

import static org.jlato.rewrite.Quotes.expr;
import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreeFactoryClass extends Utils implements DeclPattern<TreeClassDescriptor[], ClassDecl> {

	@Override
	public Pattern<? extends Decl> matcher(TreeClassDescriptor[] arg) {
		return typeDecl("public abstract class Trees { ..$_ }");
	}

	@Override
	public ClassDecl rewrite(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		decl = classDecl(name("Trees"))
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Abstract));

		if (GenSettings.generateDocs)
			decl = decl.insertLeadingComment("/** A factory for tree nodes. */");

		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.bu.Literals")));
		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.td.coll")).setOnDemand(true));

		NodeList<MemberDecl> factoryMethods = emptyList();

		factoryMethods = factoryMethods.append(memberDecl("public static <T extends Tree> NodeOption<T> none() {\n" +
				"\t\treturn TDNodeOption.none();\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <T extends Tree> NodeOption<T> some(T t) {\n" +
				"\t\treturn TDNodeOption.some(t);\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <T extends Tree> NodeOption<T> optionOf(T t) {\n" +
				"\t\treturn TDNodeOption.of(t);\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <TL extends Tree, TR extends Tree> NodeEither<TL, TR> left(TL t) {\n" +
				"\t\treturn TDNodeEither.<TL, TR>left(t);\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <TL extends Tree, TR extends Tree> NodeEither<TL, TR> right(TR t) {\n" +
				"\t\treturn TDNodeEither.<TL, TR>right(t);\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <T extends Tree> NodeList<T> emptyList() {\n" +
				"\t\treturn TDNodeList.empty();\n" +
				"\t}").build());

		factoryMethods = factoryMethods.append(memberDecl("public static <T extends Tree> NodeList<T> listOf(Iterable<T> ts) {\n" +
				"\t\treturn TDNodeList.of(ts);\n" +
				"\t}").build());

		for (int i = 1; i <= 23; i++) factoryMethods = factoryMethods.append(generateNodeListOf(i));

		for (TreeClassDescriptor descriptor : arg) {
			importManager.addImport(importDecl(descriptor.classPackageName()).setOnDemand(true));

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
						"\t\t\tname = new TDQualifiedName(optionOf(name), new TDName(part));\n" +
						"\t\t}\n" +
						"\t\treturn name;\n" +
						"\t}").build());
			} else if (descriptor.name.id().equals("LiteralExpr")) {
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Void> nullLiteralExpr() {\n" +
						"\treturn new TDLiteralExpr<Void>(Void.class, Literals.from(Void.class, null));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Boolean> literalExpr(boolean value) {\n" +
						"\treturn new TDLiteralExpr<Boolean>(Boolean.class, Literals.from(Boolean.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Integer> literalExpr(int value) {\n" +
						"\treturn new TDLiteralExpr<Integer>(Integer.class, Literals.from(Integer.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Long> literalExpr(long value) {\n" +
						"\treturn new TDLiteralExpr<Long>(Long.class, Literals.from(Long.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Float> literalExpr(float value) {\n" +
						"\treturn new TDLiteralExpr<Float>(Float.class, Literals.from(Float.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Double> literalExpr(double value) {\n" +
						"\treturn new TDLiteralExpr<Double>(Double.class, Literals.from(Double.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<Character> literalExpr(char value) {\n" +
						"\treturn new TDLiteralExpr<Character>(Character.class, Literals.from(Character.class, value));\n" +
						"}").build());
				factoryMethods = factoryMethods.append(memberDecl("public static LiteralExpr<String> literalExpr(String value) {\n" +
						"\treturn new TDLiteralExpr<String>(String.class, Literals.from(String.class, value));\n" +
						"}").build());
			}
		}

		return decl.withMembers(factoryMethods);
	}

	private MemberDecl generateNodeListOf(int count) {
		return memberDecl("public static <T extends Tree> NodeList<T> listOf(" + makeIteratedString(count, i -> "T t" + i) + ") {\n" +
				"\t\treturn TDNodeList.of(" + makeIteratedString(count, i -> "t" + i) + ");\n" +
				"\t}").build();
	}

	private String makeIteratedString(int count, Function1<Integer, String> gen) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int i = 1; i <= count; i++) {
			if (first) first = false;
			else builder.append(", ");

			builder.append(gen.apply(i));
		}
		return builder.toString();
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
		NodeList<FormalParameter> params = emptyList();
		NodeList<Expr> args = emptyList();
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
						args = args.append(expr("Trees.<" + ((QualifiedType) type).typeArgs().get().get(0) + ">emptyList()").build());
						break;
					case "NodeOption":
						args = args.append(expr("Trees.<" + ((QualifiedType) type).typeArgs().get().get(0) + ">none()").build());
						break;
					default:
						if (noNulls) {
							params = params.append(param);
							args = args.append(param.id().name());
						} else {
							args = args.append(expr("(" + param.type() + ") null").build());
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

		QualifiedType resultType = descriptor.classType();
		Stmt creation = returnStmt().withExpr(
				some(objectCreationExpr(resultType).withArgs(args).withBody(none()))
		);

		MethodDecl method = methodDecl(descriptor.interfaceType(), name(lowerCaseFirst(descriptor.name.id())))
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
