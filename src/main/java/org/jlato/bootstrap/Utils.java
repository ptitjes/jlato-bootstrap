package org.jlato.bootstrap;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Substitution;
import org.jlato.rewrite.TypeSafeMatcher;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.AnnotationExpr;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.stmt.*;
import org.jlato.tree.type.*;
import org.jlato.util.Function1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.jlato.tree.Trees.*;
import static org.jlato.tree.Trees.listOf;
import static org.jlato.tree.expr.BinaryOp.Less;
import static org.jlato.tree.expr.UnaryOp.PostIncrement;

/**
 * @author Didier Villevalois
 */
public class Utils {

	public static NodeList<FormalParameter> collectConstructorParams(ClassDecl decl) {
		Iterator<ConstructorDecl> iterator = decl.findAll(publicConstructorMatcher).iterator();
		if (!iterator.hasNext()) return null;
		final ConstructorDecl treeConstructor = iterator.next();
		return treeConstructor.params();
	}

	protected static boolean nullable(FormalParameter p) {
		final Type type = p.type();
		if (type instanceof PrimitiveType) return false;
		final String name = ((QualifiedType) type).name().id();
		return !(name.equals("NodeOption") || name.equals("NodeList") || name.equals("NodeEither"));
	}

	public static String constantName(FormalParameter parameter) {
		return constantName(parameter.id().name().id(), parameter.type());
	}

	public static String constantName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(2)));
			} else if (propertyName.startsWith("has")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(3)));
			}
		}
		return camelToConstant(propertyName);
	}

	public static String propertySetterName(FormalParameter parameter) {
		return propertySetterName(parameter.id().name().id(), parameter.type());
	}

	public static String propertySetterName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return "set" + upperCaseFirst(propertyName.substring(2));
			} else if (propertyName.startsWith("has")) {
				return "set" + upperCaseFirst(propertyName.substring(3));
			}
		}
		return "with" + upperCaseFirst(propertyName);
	}

	public static Type boxedType(Type type) {
		if (type instanceof QualifiedType) return type;
		else if (type instanceof PrimitiveType) {
			Primitive primitive = ((PrimitiveType) type).primitive();
			switch (primitive) {
				case Boolean:
					return qType("Boolean");
				case Byte:
					return qType("Byte");
				case Short:
					return qType("Short");
				case Int:
					return qType("Integer");
				case Long:
					return qType("Long");
				case Float:
					return qType("Float");
				case Double:
					return qType("Double");
				case Char:
					return qType("Character");
			}
		}
		return null;
	}

	public static AnnotationExpr overrideAnn() {
		return markerAnnotationExpr(qualifiedName("Override"));
	}

	public static AnnotationExpr deprecatedAnn() {
		return markerAnnotationExpr(qualifiedName("Deprecated"));
	}

	public static NodeList<FormalParameter> deriveStateParams(NodeList<FormalParameter> treeConstructorParams) {
		NodeList<FormalParameter> stateConstructorParams = emptyList();
		for (FormalParameter param : treeConstructorParams) {
			Type treeType = param.type();

			Type stateParamType = treeTypeToSTreeType(treeType);
			stateConstructorParams = stateConstructorParams.append(
					formalParameter(stateParamType, param.id())
			);
		}
		return stateConstructorParams;
	}

	public static Type treeTypeToSTreeType(Type treeType) {
		if (propertyFieldType(treeType)) {
			return treeType;
		} else {
			final QualifiedType qualifiedType = (QualifiedType) treeType;

			final TreeTypeDescriptor descriptor = AllDescriptors.get(qualifiedType.name());
			boolean isInterface = descriptor != null && descriptor.isInterface();

			final QualifiedType stateType = treeTypeToStateType(qualifiedType);
			return qualifiedType(AllDescriptors.BU_TREE)
					.withTypeArgs(some(listOf(isInterface ? wildcardType().withExt(some(stateType)) : stateType)));
		}
	}

	public static Type treeTypeToStateType(Type treeType) {
		if (propertyFieldType(treeType)) {
			return treeType;
		} else {
			return treeTypeToStateType((QualifiedType) treeType);
		}
	}

	public static QualifiedType treeTypeToStateType(QualifiedType treeType) {
		final Name name = treeType.name();
		final String id = name.id();
		switch (id) {
			case "NodeList":
				return qualifiedType(AllDescriptors.S_NODE_LIST);
			case "NodeOption":
				return qualifiedType(AllDescriptors.S_NODE_OPTION);
			case "NodeEither":
				return qualifiedType(AllDescriptors.S_NODE_EITHER);
			default:
				final TreeTypeDescriptor descriptor = AllDescriptors.get(name);
				return descriptor.stateType();
		}
	}

	public static boolean propertyFieldType(Type treeType) {
		if (treeType instanceof PrimitiveType) return true;
		if (treeType instanceof QualifiedType) {
			String name = ((QualifiedType) treeType).name().id();
			return name.equals("String") ||
					name.equals("Class") ||
					name.equals("ModifierKeyword") ||
					name.equals("Primitive") ||
					name.endsWith("Op");
		}
		return false;
	}

	public static boolean nameFieldType(Type treeType) {
		if (treeType instanceof QualifiedType) {
			String name = ((QualifiedType) treeType).name().id();
			return name.equals("Name");
		}
		return false;
	}

	public static <T extends Tree> NodeList<T> safeList(NodeList<T> list) {
		return list == null ? emptyList() : list;
	}

	public static QualifiedType qType(String typeName) {
		return Trees.qualifiedType(name(typeName));
	}

	public static QualifiedType qType(String typeName, Type typeArg) {
		return Trees.qualifiedType(name(typeName)).withTypeArgs(some(listOf(typeArg)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2) {
		return Trees.qualifiedType(name(typeName)).withTypeArgs(some(listOf(typeArg1, typeArg2)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2, Type typeArg3) {
		return Trees.qualifiedType(name(typeName)).withTypeArgs(some(listOf(typeArg1, typeArg2, typeArg3)));
	}

	public static String constantToCamel(String constantName) {
		StringBuilder buffer = new StringBuilder();
		String[] split = constantName.split("_");
		boolean first = true;
		for (String s : split) {
			String part = s.toLowerCase();
			if (first) {
				first = false;
				buffer.append(part);
			} else buffer.append(upperCaseFirst(part));
		}
		return buffer.toString();
	}

	public static String camelToConstant(String name) {
		StringBuilder buffer = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				buffer.append("_");
			}
			buffer.append(Character.toUpperCase(c));
		}
		return buffer.toString();
	}

	public static String lowerCaseFirst(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String upperCaseFirst(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String genDoc(FieldDecl decl, String description) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append("* ").append(description).append("\n");
		builder.append("*/");
		return builder.toString();
	}

	public static String genDoc(MethodDecl decl, String description, String[] paramDescription, String returnDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append("* ").append(description).append("\n");
		builder.append("*\n");
		appendParamsDoc(decl.params(), paramDescription, builder);
		if (returnDescription != null)
			builder.append("* @return ").append(returnDescription).append("\n");
		builder.append("*/");
		return builder.toString();
	}

	private static void appendParamsDoc(NodeList<FormalParameter> parameters, String[] paramDescription, StringBuilder builder) {
		int maxNameLength = 0;
		for (FormalParameter param : parameters) {
			Name name = param.id().name();
			int length = name.id().length();
			maxNameLength = Math.max(maxNameLength, length);
		}

		int index = 0;
		for (FormalParameter param : parameters) {
			Name name = param.id().name();
			builder.append("* @param ").append(name);
			for (int i = 0; i < maxNameLength - name.id().length(); i++) {
				builder.append(' ');
			}
			builder.append(' ').append(paramDescription[index]).append("\n");
			index++;
		}
	}

	public static String genDoc(ConstructorDecl decl, String description, String[] paramDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append("* ").append(description).append("\n");
		builder.append("*\n");
		appendParamsDoc(decl.params(), paramDescription, builder);
		builder.append("*/");
		return builder.toString();
	}

	public static String[] paramDoc(NodeList<FormalParameter> params, Function1<FormalParameter, String> f) {
		List<String> docs = new ArrayList();
		for (FormalParameter param : params) {
			docs.add(f.apply(param));
		}
		return docs.toArray(new String[params.size()]);
	}

	public static Stmt junitAssert(String assertName, Expr... arguments) {
		return expressionStmt(
				methodInvocationExpr(name(assertName))
						.withScope(some(name("Assert")))
						.withArgs(listOf(Arrays.asList(arguments)))
		);
	}

	public static Stmt newVarStmt(Type type, Name name, Expr init) {
		return expressionStmt(
				variableDeclarationExpr(
						localVariableDecl(type)
								.withVariables(listOf(
										variableDeclarator(variableDeclaratorId(name))
												.withInit(some(init))
								))
				)
		);
	}

	public static MethodInvocationExpr hashCode(Expr e) {
		return methodInvocationExpr(name("hashCode")).withScope(some(e));
	}

	public static MethodInvocationExpr equals(Expr e1, Expr e2) {
		return methodInvocationExpr(name("equals")).withScope(some(e1)).withArgs(listOf(e2));
	}

	public static String makeDocumentationName(Name name) {
		List<String> words = extractWords(name.id());

		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (String word : words) {
			if (!first) {
				buffer.append(" ");
			} else first = false;

			word = mapWord(word);
			buffer.append(word);
		}

		return mapDocumentation(buffer.toString());
	}

	private static String mapWord(String word) {
		switch (word) {
			case "foreach":
				return "\\\"enhanced\\\" 'for'";
			case "do":
				return "'do-while'";
			case "while":
			case "for":
			case "if":
			case "switch":
			case "try":
			case "catch":
			case "finally":
			case "throw":
			case "throws":
			case "synchronized":
			case "return":
			case "continue":
			case "break":
			case "assert":

			case "this":
			case "super":
			case "extends":
			case "implements":
				return "'" + word + "'";

			case "ext":
				return "upper bound";
			case "sup":
				return "lower bound";

			case "decl":
				return "declaration";
			case "decls":
				return "declarations";
			case "stmt":
				return "statement";
			case "stmts":
				return "statements";
			case "expr":
				return "expression";
			case "exprs":
				return "expressions";

			case "dim":
				return "dimension";
			case "dims":
				return "dimensions";
			case "id":
				return "identifier";
			case "param":
				return "parameter";
			case "params":
				return "parameters";
			case "imports":
				return "import declarations";
		}
		return word;
	}

	private static String mapDocumentation(String documentation) {
		switch (documentation) {
			case "class expression":
				return "'class' expression";
			case "instance of expression":
				return "'instanceof' expression";
			case "member value pair":
				return "annotation member value pair";
			case "assign expression":
				return "assignment expression";
			case "annotation declaration":
				return "annotation type declaration";
			case "annotation member declaration":
				return "annotation type member declaration";
			case "is var args":
				return "is a variadic parameter";
			case "has parens":
				return "has its arguments parenthesized";
			case "trailing comma":
				return "has a trailing comma";
			case "trailing semi colon":
				return "has a trailing semi-colon for its resources";
			case "is on demand":
				return "is on-demand";
		}
		return documentation;
	}

	private static List<String> extractWords(String name) {
		List<String> words = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c) && !first) {
				words.add(buffer.toString());
				buffer = new StringBuilder();
			}
			if (first) first = false;

			buffer.append(Character.toLowerCase(c));
		}
		words.add(buffer.toString());
		return words;
	}

	protected static String facadeAccessorDoc(MethodDecl decl, TreeTypeDescriptor arg, FormalParameter param) {
		Type type = param.type();
		if (type instanceof PrimitiveType &&
				((PrimitiveType) type).primitive() == Primitive.Boolean) {
			return genDoc(decl,
					"Tests whether this " + arg.description + " " + makeDocumentationName(param.id().name()) + ".",
					new String[]{},
					"<code>true</code> if this " + arg.description + " " + makeDocumentationName(param.id().name()) + ", <code>false</code> otherwise."
			);
		} else {
			return genDoc(decl,
					"Returns the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + ".",
					new String[]{},
					"the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + "."
			);
		}
	}

	protected static String facadeMutatorDoc(MethodDecl decl, TreeTypeDescriptor arg, FormalParameter param) {
		Type type = param.type();
		if (type instanceof PrimitiveType &&
				((PrimitiveType) type).primitive() == Primitive.Boolean) {
			return genDoc(decl,
					"Sets whether this " + arg.description + " " + makeDocumentationName(param.id().name()) + ".",
					new String[]{"<code>true</code> if this " + arg.description + " " + makeDocumentationName(param.id().name()) + ", <code>false</code> otherwise."},
					"the resulting mutated " + arg.description + "."
			);
		} else {
			return genDoc(decl,
					"Replaces the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + ".",
					new String[]{"the replacement for the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + "."},
					"the resulting mutated " + arg.description + "."
			);
		}
	}

	protected static String facadeLambdaMutatorDoc(MethodDecl decl, TreeTypeDescriptor arg, FormalParameter param) {
		Type type = param.type();
		if (type instanceof PrimitiveType &&
				((PrimitiveType) type).primitive() == Primitive.Boolean) {
			return genDoc(decl,
					"Mutates whether this " + arg.description + " " + makeDocumentationName(param.id().name()) + ".",
					new String[]{"the mutation to apply to whether this " + arg.description + " " + makeDocumentationName(param.id().name()) + "."},
					"the resulting mutated " + arg.description + "."
			);
		} else {
			return genDoc(decl,
					"Mutates the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + ".",
					new String[]{"the mutation to apply to the " + makeDocumentationName(param.id().name()) + " of this " + arg.description + "."},
					"the resulting mutated " + arg.description + "."
			);
		}
	}

	public Stmt loopFor(int count, NodeList<Stmt> loopStmts) {
		final Name i = name("i");
		return forStmt(binaryExpr(i, Less, literalExpr(count)), blockStmt().withStmts(loopStmts))
				.withInit(listOf(
						variableDeclarationExpr(
								localVariableDecl(primitiveType(Primitive.Int))
										.withVariables(listOf(
												variableDeclarator(variableDeclaratorId(i))
														.withInit(some(literalExpr(0)))
										))
						)
				))
				.withUpdate(listOf(
						unaryExpr(PostIncrement, i)
				));
	}

	public static Expr factoryCall(TreeClassDescriptor descriptor, ImportManager importManager) {
		importManager.addImportByName(qualifiedName("org.jlato.tree.Trees"));
		return methodInvocationExpr(name(lowerCaseFirst(descriptor.name.id())))
				.withScope(some(name("Trees")));
	}


	public static MethodInvocationExpr arbitraryCall(Name arbitrary, Type type) {
		return methodInvocationExpr(name(arbitraryGenMethodName(type))).withScope(some(arbitrary));
	}

	public static String arbitraryGenMethodName(Type type) {
		return "arbitrary" + arbitraryDesc(type);
	}

	private static String arbitraryDesc(Type type) {
		String arbitraryDesc = "";
		if (type instanceof PrimitiveType) {
			arbitraryDesc = upperCaseFirst(((PrimitiveType) type).primitive().toString());
		} else if (type instanceof QualifiedType) {
			final QualifiedType qualifiedType = (QualifiedType) type;
			String shortName = qualifiedType.name().id();

			if (shortName.startsWith("Node")) {
				shortName = shortName.substring(4);
			}

			if (qualifiedType.typeArgs().isNone()) {
				arbitraryDesc = shortName;
			} else {
				StringBuilder builder = new StringBuilder();
				for (Type ta : qualifiedType.typeArgs().get()) {
					builder.append(arbitraryDesc(ta));
				}
				arbitraryDesc = shortName + builder.toString();
			}
		}
		return arbitraryDesc;
	}

	// Matchers

	public static TypeSafeMatcher<ConstructorDecl> constructors(final Function1<ConstructorDecl, Boolean> predicate) {
		return new MatcherImpl<ConstructorDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof ConstructorDecl)) return null;
				ConstructorDecl decl = (ConstructorDecl) o;
				if (!predicate.apply(decl)) return null;
				return substitution;
			}
		};
	}

	public static final TypeSafeMatcher<ConstructorDecl> publicConstructorMatcher = constructors(c -> c.modifiers().contains(Modifier.Public));

	public static abstract class MatcherImpl<T> implements TypeSafeMatcher<T> {
		@Override
		public final Substitution match(Object o) {
			return match(o, Substitution.empty());
		}
	}
}
