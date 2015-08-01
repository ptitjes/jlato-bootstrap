package org.jlato.bootstrap.ast;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.param;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClassDescriptor {

	public static final Name TREE_NAME = new Name("Tree");
	public static final Name STATE_NAME = new Name("State");
	public static final Name STREE_STATE_NAME = new Name("STreeState");

	public final Name packageName;
	public final Name name;
	public final boolean customTailored;
	public final Name superTypeName;
	public final String description;

	public final NodeList<FormalParameter> parameters;

	public TreeClassDescriptor(Name packageName, Name name,
	                           boolean customTailored, Name superTypeName,
	                           String description,
	                           NodeList<FormalParameter> parameters) {
		this.packageName = packageName;
		this.name = name;
		this.customTailored = customTailored;
		this.superTypeName = superTypeName;
		this.description = description;
		this.parameters = parameters;
	}

	public String treeClassFileName() {
		return "org/jlato/tree/" + packageName + "/" + name + ".java";
	}

	public QualifiedType stateType() {
		return qualifiedType().withScope(some(qualifiedType().withName(name))).withName(STATE_NAME);
	}

	public QualifiedType stateSuperType() {
		if (superTypeName.equals(TREE_NAME)) return qualifiedType().withName(STREE_STATE_NAME);
		return qualifiedType().withScope(some(qualifiedType().withName(superTypeName))).withName(STATE_NAME);
	}

	public String prefixedDescription() {
		char firstChar = description.startsWith("'") ? description.charAt(1) :
				description.startsWith("\\\"") ? description.charAt(2) :
						description.charAt(0);
		switch (firstChar) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return "an " + description;
			default:
				return "a " + description;
		}
	}

	public static final TreeClassDescriptor[] ALL = new TreeClassDescriptor[] {

			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationDecl"), false, new Name("TypeDecl"),
					"annotation type declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationMemberDecl"), false, new Name("MemberDecl"),
					"annotation type member declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<Expr> defaultValue").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ArrayDim"), false, new Name("Tree"),
					"array dimension",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ClassDecl"), false, new Name("TypeDecl"),
					"class declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeOption<QualifiedType> extendsClause").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("CompilationUnit"), false, new Name("Tree"),
					"compilation unit",
					NodeList.of(
							param("PackageDecl packageDecl").build(),
							param("NodeList<ImportDecl> imports").build(),
							param("NodeList<TypeDecl> types").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ConstructorDecl"), false, new Name("MemberDecl"),
					"constructor declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("BlockStmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyMemberDecl"), false, new Name("MemberDecl"),
					"empty member declaration",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyTypeDecl"), false, new Name("TypeDecl"),
					"empty type declaration",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumConstantDecl"), false, new Name("MemberDecl"),
					"enum constant declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Expr>> args").build(),
							param("NodeOption<NodeList<MemberDecl>> classBody").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumDecl"), false, new Name("TypeDecl"),
					"enum declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<EnumConstantDecl> enumConstants").build(),
							param("boolean trailingComma").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FieldDecl"), false, new Name("MemberDecl"),
					"field declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FormalParameter"), false, new Name("Tree"),
					"formal parameter",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("boolean isVarArgs").build(),
							param("VariableDeclaratorId id").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ImportDecl"), false, new Name("Tree"),
					"import declaration",
					NodeList.of(
							param("QualifiedName name").build(),
							param("boolean isStatic").build(),
							param("boolean isOnDemand").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InitializerDecl"), false, new Name("MemberDecl"),
					"initializer declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("BlockStmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InterfaceDecl"), false, new Name("TypeDecl"),
					"interface declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeList<QualifiedType> extendsClause").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("LocalVariableDecl"), false, new Name("Decl"),
					"local variable declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("MethodDecl"), false, new Name("MemberDecl"),
					"method declaration",
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("NodeOption<BlockStmt> body").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("Modifier"), true, new Name("ExtendedModifier"),
					"modifier",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("PackageDecl"), false, new Name("Tree"),
					"package declaration",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("QualifiedName name").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("TypeParameter"), false, new Name("Tree"),
					"type parameter",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Name name").build(),
							param("NodeList<Type> bounds").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclarator"), false, new Name("Tree"),
					"variable declarator",
					NodeList.of(
							param("VariableDeclaratorId id").build(),
							param("NodeOption<Expr> init").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclaratorId"), false, new Name("Tree"),
					"variable declarator identifier",
					NodeList.of(
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayAccessExpr"), false, new Name("Expr"),
					"array access expression",
					NodeList.of(
							param("Expr name").build(),
							param("Expr index").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayCreationExpr"), false, new Name("Expr"),
					"array creation expression",
					NodeList.of(
							param("Type type").build(),
							param("NodeList<ArrayDimExpr> dimExprs").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<ArrayInitializerExpr> init").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayDimExpr"), false, new Name("Tree"),
					"array dimension expression",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayInitializerExpr"), false, new Name("Expr"),
					"array initializer expression",
					NodeList.of(
							param("NodeList<Expr> values").build(),
							param("boolean trailingComma").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("AssignExpr"), false, new Name("Expr"),
					"assignment expression",
					NodeList.of(
							param("Expr target").build(),
							param("AssignOp op").build(),
							param("Expr value").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("BinaryExpr"), false, new Name("Expr"),
					"binary expression",
					NodeList.of(
							param("Expr left").build(),
							param("BinaryOp op").build(),
							param("Expr right").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("CastExpr"), false, new Name("Expr"),
					"cast expression",
					NodeList.of(
							param("Type type").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ClassExpr"), false, new Name("Expr"),
					"'class' expression",
					NodeList.of(
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ConditionalExpr"), false, new Name("Expr"),
					"conditional expression",
					NodeList.of(
							param("Expr condition").build(),
							param("Expr thenExpr").build(),
							param("Expr elseExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("FieldAccessExpr"), false, new Name("Expr"),
					"field access expression",
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("InstanceOfExpr"), false, new Name("Expr"),
					"'instanceof' expression",
					NodeList.of(
							param("Expr expr").build(),
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LambdaExpr"), false, new Name("Expr"),
					"lambda expression",
					NodeList.of(
							param("NodeList<FormalParameter> params").build(),
							param("boolean hasParens").build(),
							param("NodeEither<Expr, BlockStmt> body").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LiteralExpr"), false, new Name("Expr"),
					"literal expression",
					NodeList.of(
							param("Class<T> literalClass").build(),
							param("String literalString").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MarkerAnnotationExpr"), false, new Name("AnnotationExpr"),
					"marker annotation expression",
					NodeList.of(
							param("QualifiedName name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MemberValuePair"), false, new Name("Tree"),
					"annotation member value pair",
					NodeList.of(
							param("Name name").build(),
							param("Expr value").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodInvocationExpr"), false, new Name("Expr"),
					"method invocation expression",
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build(),
							param("NodeList<Expr> args").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodReferenceExpr"), false, new Name("Expr"),
					"method reference expression",
					NodeList.of(
							param("Expr scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("NormalAnnotationExpr"), false, new Name("AnnotationExpr"),
					"normal annotation expression",
					NodeList.of(
							param("QualifiedName name").build(),
							param("NodeList<MemberValuePair> pairs").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ObjectCreationExpr"), false, new Name("Expr"),
					"object creation expression",
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("QualifiedType type").build(),
							param("NodeList<Expr> args").build(),
							param("NodeOption<NodeList<MemberDecl>> body").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ParenthesizedExpr"), false, new Name("Expr"),
					"parenthesized expression",
					NodeList.of(
							param("Expr inner").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SingleMemberAnnotationExpr"), false, new Name("AnnotationExpr"),
					"single member annotation expression",
					NodeList.of(
							param("QualifiedName name").build(),
							param("Expr memberValue").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SuperExpr"), false, new Name("Expr"),
					"'super' expression",
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ThisExpr"), false, new Name("Expr"),
					"'this' expression",
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("TypeExpr"), false, new Name("Expr"),
					"type expression",
					NodeList.of(
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("UnaryExpr"), false, new Name("Expr"),
					"unary expression",
					NodeList.of(
							param("UnaryOp op").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("VariableDeclarationExpr"), false, new Name("Expr"),
					"variable declaration expression",
					NodeList.of(
							param("LocalVariableDecl declaration").build()
					)
			),
			new TreeClassDescriptor(new Name("name"), new Name("Name"), false, new Name("Expr"),
					"name",
					NodeList.of(
							param("String id").build()
					)
			),
			new TreeClassDescriptor(new Name("name"), new Name("QualifiedName"), false, new Name("Tree"),
					"qualified name",
					NodeList.of(
							param("NodeOption<QualifiedName> qualifier").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("AssertStmt"), false, new Name("Stmt"),
					"'assert' statement",
					NodeList.of(
							param("Expr check").build(),
							param("NodeOption<Expr> msg").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BlockStmt"), false, new Name("Stmt"),
					"block statement",
					NodeList.of(
							param("NodeList<Stmt> stmts").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BreakStmt"), false, new Name("Stmt"),
					"'break' statement",
					NodeList.of(
							param("NodeOption<Name> id").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("CatchClause"), false, new Name("Tree"),
					"catch clause",
					NodeList.of(
							param("FormalParameter except").build(),
							param("BlockStmt catchBlock").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ContinueStmt"), false, new Name("Stmt"),
					"'continue' statement",
					NodeList.of(
							param("NodeOption<Name> id").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("DoStmt"), false, new Name("Stmt"),
					"'do-while' statement",
					NodeList.of(
							param("Stmt body").build(),
							param("Expr condition").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("EmptyStmt"), false, new Name("Stmt"),
					"empty statement",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExplicitConstructorInvocationStmt"), false, new Name("Stmt"),
					"explicit constructor invocation statement",
					NodeList.of(
							param("NodeList<Type> typeArgs").build(),
							param("boolean isThis").build(),
							param("NodeOption<Expr> expr").build(),
							param("NodeList<Expr> args").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExpressionStmt"), false, new Name("Stmt"),
					"expression statement",
					NodeList.of(
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForStmt"), false, new Name("Stmt"),
					"'for' statement",
					NodeList.of(
							param("NodeList<Expr> init").build(),
							param("Expr compare").build(),
							param("NodeList<Expr> update").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForeachStmt"), false, new Name("Stmt"),
					"\"enhanced\" 'for' statement",
					NodeList.of(
							param("VariableDeclarationExpr var").build(),
							param("Expr iterable").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("IfStmt"), false, new Name("Stmt"),
					"'if' statement",
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt thenStmt").build(),
							param("NodeOption<Stmt> elseStmt").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("LabeledStmt"), false, new Name("Stmt"),
					"labeled statement",
					NodeList.of(
							param("Name label").build(),
							param("Stmt stmt").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ReturnStmt"), false, new Name("Stmt"),
					"'return' statement",
					NodeList.of(
							param("NodeOption<Expr> expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchCase"), false, new Name("Tree"),
					"'switch' case",
					NodeList.of(
							param("NodeOption<Expr> label").build(),
							param("NodeList<Stmt> stmts").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchStmt"), false, new Name("Stmt"),
					"'switch' statement",
					NodeList.of(
							param("Expr selector").build(),
							param("NodeList<SwitchCase> cases").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SynchronizedStmt"), false, new Name("Stmt"),
					"'synchronized' statement",
					NodeList.of(
							param("Expr expr").build(),
							param("BlockStmt block").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ThrowStmt"), false, new Name("Stmt"),
					"'throw' statement",
					NodeList.of(
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TryStmt"), false, new Name("Stmt"),
					"'try' statement",
					NodeList.of(
							param("NodeList<VariableDeclarationExpr> resources").build(),
							param("boolean trailingSemiColon").build(),
							param("BlockStmt tryBlock").build(),
							param("NodeList<CatchClause> catchs").build(),
							param("NodeOption<BlockStmt> finallyBlock").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TypeDeclarationStmt"), false, new Name("Stmt"),
					"type declaration statement",
					NodeList.of(
							param("TypeDecl typeDecl").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("WhileStmt"), false, new Name("Stmt"),
					"'while' statement",
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("ArrayType"), false, new Name("ReferenceType"),
					"array type",
					NodeList.of(
							param("Type componentType").build(),
							param("NodeList<ArrayDim> dims").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("IntersectionType"), false, new Name("Type"),
					"intersection type",
					NodeList.of(
							param("NodeList<Type> types").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("PrimitiveType"), false, new Name("Type"),
					"primitive type",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Primitive primitive").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("QualifiedType"), false, new Name("ReferenceType"),
					"qualified type",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<QualifiedType> scope").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Type>> typeArgs").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnionType"), false, new Name("Type"),
					"union type",
					NodeList.of(
							param("NodeList<Type> types").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnknownType"), false, new Name("Type"),
					"unknown type",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("type"), new Name("VoidType"), false, new Name("Type"),
					"void type",
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("type"), new Name("WildcardType"), false, new Name("Type"),
					"wildcard type",
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<ReferenceType> ext").build(),
							param("NodeOption<ReferenceType> sup").build()
					)
			),
	};
}
