package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.type;
import static org.jlato.rewrite.Quotes.param;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClassDescriptor extends TreeTypeDescriptor {

	public final boolean customTailored;
	public final NodeList<FormalParameter> parameters;

	public TreeClassDescriptor(Name packageName, Name name, String description,
	                           NodeList<QualifiedType> superInterfaces,
	                           boolean customTailored,
	                           NodeList<FormalParameter> parameters) {
		super(packageName, name, description, superInterfaces);
		this.customTailored = customTailored;
		this.parameters = parameters;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public String treeFilePath() {
		return "org/jlato/tree/" + packageName + "/" + name + ".java";
	}

	@Override
	public QualifiedType stateType() {
		return qualifiedType().withScope(some(qualifiedType().withName(name))).withName(STATE_NAME);
	}

	public static final TreeClassDescriptor[] ALL = new TreeClassDescriptor[] {
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationDecl"), "annotation type declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationMemberDecl"), "annotation type member declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<Expr> defaultValue").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ArrayDim"), "array dimension",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ClassDecl"), "class declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeOption<QualifiedType> extendsClause").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("CompilationUnit"), "compilation unit",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("PackageDecl packageDecl").build(),
							param("NodeList<ImportDecl> imports").build(),
							param("NodeList<TypeDecl> types").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ConstructorDecl"), "constructor declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("BlockStmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyMemberDecl"), "empty member declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyTypeDecl"), "empty type declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					false,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumConstantDecl"), "enum constant declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Expr>> args").build(),
							param("NodeOption<NodeList<MemberDecl>> classBody").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumDecl"), "enum declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<EnumConstantDecl> enumConstants").build(),
							param("boolean trailingComma").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FieldDecl"), "field declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FormalParameter"), "formal parameter",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("boolean isVarArgs").build(),
							param("VariableDeclaratorId id").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ImportDecl"), "import declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("QualifiedName name").build(),
							param("boolean isStatic").build(),
							param("boolean isOnDemand").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InitializerDecl"), "initializer declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("BlockStmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InterfaceDecl"), "interface declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeList<QualifiedType> extendsClause").build(),
							param("NodeList<MemberDecl> members").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("LocalVariableDecl"), "local variable declaration",
					NodeList.of(
							(QualifiedType) type("Decl").build()
					),
					false,
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("MethodDecl"), "method declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					false,
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
			new TreeClassDescriptor(new Name("decl"), new Name("Modifier"), "modifier",
					NodeList.of(
							(QualifiedType) type("ExtendedModifier").build()
					),
					true,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("decl"), new Name("PackageDecl"), "package declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("QualifiedName name").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("TypeParameter"), "type parameter",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Name name").build(),
							param("NodeList<Type> bounds").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclarator"), "variable declarator",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("VariableDeclaratorId id").build(),
							param("NodeOption<Expr> init").build()
					)
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclaratorId"), "variable declarator identifier",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayAccessExpr"), "array access expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr name").build(),
							param("Expr index").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayCreationExpr"), "array creation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Type type").build(),
							param("NodeList<ArrayDimExpr> dimExprs").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<ArrayInitializerExpr> init").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayDimExpr"), "array dimension expression",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayInitializerExpr"), "array initializer expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeList<Expr> values").build(),
							param("boolean trailingComma").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("AssignExpr"), "assignment expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr target").build(),
							param("AssignOp op").build(),
							param("Expr value").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("BinaryExpr"), "binary expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr left").build(),
							param("BinaryOp op").build(),
							param("Expr right").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("CastExpr"), "cast expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Type type").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ClassExpr"), "'class' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ConditionalExpr"), "conditional expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr condition").build(),
							param("Expr thenExpr").build(),
							param("Expr elseExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("FieldAccessExpr"), "field access expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("InstanceOfExpr"), "'instanceof' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr expr").build(),
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LambdaExpr"), "lambda expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeList<FormalParameter> params").build(),
							param("boolean hasParens").build(),
							param("NodeEither<Expr, BlockStmt> body").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LiteralExpr"), "literal expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Class<T> literalClass").build(),
							param("String literalString").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MarkerAnnotationExpr"), "marker annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					false,
					NodeList.of(
							param("QualifiedName name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MemberValuePair"), "annotation member value pair",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("Name name").build(),
							param("Expr value").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodInvocationExpr"), "method invocation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build(),
							param("NodeList<Expr> args").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodReferenceExpr"), "method reference expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("NormalAnnotationExpr"), "normal annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					false,
					NodeList.of(
							param("QualifiedName name").build(),
							param("NodeList<MemberValuePair> pairs").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ObjectCreationExpr"), "object creation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("QualifiedType type").build(),
							param("NodeList<Expr> args").build(),
							param("NodeOption<NodeList<MemberDecl>> body").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ParenthesizedExpr"), "parenthesized expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Expr inner").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SingleMemberAnnotationExpr"), "single member annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					false,
					NodeList.of(
							param("QualifiedName name").build(),
							param("Expr memberValue").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SuperExpr"), "'super' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ThisExpr"), "'this' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("TypeExpr"), "type expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("Type type").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("UnaryExpr"), "unary expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("UnaryOp op").build(),
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("expr"), new Name("VariableDeclarationExpr"), "variable declaration expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("LocalVariableDecl declaration").build()
					)
			),
			new TreeClassDescriptor(new Name("name"), new Name("Name"), "name",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					false,
					NodeList.of(
							param("String id").build()
					)
			),
			new TreeClassDescriptor(new Name("name"), new Name("QualifiedName"), "qualified name",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeOption<QualifiedName> qualifier").build(),
							param("Name name").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("AssertStmt"), "'assert' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr check").build(),
							param("NodeOption<Expr> msg").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BlockStmt"), "block statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeList<Stmt> stmts").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BreakStmt"), "'break' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Name> id").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("CatchClause"), "catch clause",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("FormalParameter except").build(),
							param("BlockStmt catchBlock").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ContinueStmt"), "'continue' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Name> id").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("DoStmt"), "'do-while' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Stmt body").build(),
							param("Expr condition").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("EmptyStmt"), "empty statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExplicitConstructorInvocationStmt"), "explicit constructor invocation statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeList<Type> typeArgs").build(),
							param("boolean isThis").build(),
							param("NodeOption<Expr> expr").build(),
							param("NodeList<Expr> args").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExpressionStmt"), "expression statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForStmt"), "'for' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeList<Expr> init").build(),
							param("Expr compare").build(),
							param("NodeList<Expr> update").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForeachStmt"), "\"enhanced\" 'for' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("VariableDeclarationExpr var").build(),
							param("Expr iterable").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("IfStmt"), "'if' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt thenStmt").build(),
							param("NodeOption<Stmt> elseStmt").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("LabeledStmt"), "labeled statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Name label").build(),
							param("Stmt stmt").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ReturnStmt"), "'return' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchCase"), "'switch' case",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					false,
					NodeList.of(
							param("NodeOption<Expr> label").build(),
							param("NodeList<Stmt> stmts").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchStmt"), "'switch' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr selector").build(),
							param("NodeList<SwitchCase> cases").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SynchronizedStmt"), "'synchronized' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr expr").build(),
							param("BlockStmt block").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ThrowStmt"), "'throw' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr expr").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TryStmt"), "'try' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("NodeList<VariableDeclarationExpr> resources").build(),
							param("boolean trailingSemiColon").build(),
							param("BlockStmt tryBlock").build(),
							param("NodeList<CatchClause> catchs").build(),
							param("NodeOption<BlockStmt> finallyBlock").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TypeDeclarationStmt"), "type declaration statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("TypeDecl typeDecl").build()
					)
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("WhileStmt"), "'while' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					false,
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt body").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("ArrayType"), "array type",
					NodeList.of(
							(QualifiedType) type("ReferenceType").build()
					),
					false,
					NodeList.of(
							param("Type componentType").build(),
							param("NodeList<ArrayDim> dims").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("IntersectionType"), "intersection type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.of(
							param("NodeList<Type> types").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("PrimitiveType"), "primitive type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Primitive primitive").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("QualifiedType"), "qualified type",
					NodeList.of(
							(QualifiedType) type("ReferenceType").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<QualifiedType> scope").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Type>> typeArgs").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnionType"), "union type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.of(
							param("NodeList<Type> types").build()
					)
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnknownType"), "unknown type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("type"), new Name("VoidType"), "void type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.<FormalParameter>empty()
			),
			new TreeClassDescriptor(new Name("type"), new Name("WildcardType"), "wildcard type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					false,
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<ReferenceType> ext").build(),
							param("NodeOption<ReferenceType> sup").build()
					)
			),
	};
}
