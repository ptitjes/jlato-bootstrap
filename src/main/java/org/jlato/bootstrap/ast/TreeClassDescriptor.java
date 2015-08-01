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
	public final Name superTypeName;
	public final NodeList<FormalParameter> parameters;

	public TreeClassDescriptor(Name packageName, Name name, Name superTypeName, NodeList<FormalParameter> parameters) {
		this.packageName = packageName;
		this.name = name;
		this.superTypeName = superTypeName;
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

	public static final TreeClassDescriptor[] ALL = new TreeClassDescriptor[]{
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationDecl"), new Name("TypeDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Name name").build(), param("NodeList<MemberDecl> members").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationMemberDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Type type").build(), param("Name name").build(), param("NodeList<ArrayDim> dims").build(), param("NodeOption<Expr> defaultValue").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("ArrayDim"), new Name("Tree"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("ClassDecl"), new Name("TypeDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Name name").build(), param("NodeList<TypeParameter> typeParams").build(), param("NodeOption<QualifiedType> extendsClause").build(), param("NodeList<QualifiedType> implementsClause").build(), param("NodeList<MemberDecl> members").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("CompilationUnit"), new Name("Tree"), NodeList.of(param("PackageDecl packageDecl").build(), param("NodeList<ImportDecl> imports").build(), param("NodeList<TypeDecl> types").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("ConstructorDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("NodeList<TypeParameter> typeParams").build(), param("Name name").build(), param("NodeList<FormalParameter> params").build(), param("NodeList<QualifiedType> throwsClause").build(), param("BlockStmt body").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyMemberDecl"), new Name("MemberDecl"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyTypeDecl"), new Name("TypeDecl"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumConstantDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Name name").build(), param("NodeOption<NodeList<Expr>> args").build(), param("NodeOption<NodeList<MemberDecl>> classBody").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumDecl"), new Name("TypeDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Name name").build(), param("NodeList<QualifiedType> implementsClause").build(), param("NodeList<EnumConstantDecl> enumConstants").build(), param("boolean trailingComma").build(), param("NodeList<MemberDecl> members").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("FieldDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Type type").build(), param("NodeList<VariableDeclarator> variables").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("FormalParameter"), new Name("Tree"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Type type").build(), param("boolean isVarArgs").build(), param("VariableDeclaratorId id").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("ImportDecl"), new Name("Tree"), NodeList.of(param("QualifiedName name").build(), param("boolean isStatic").build(), param("boolean isOnDemand").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("InitializerDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("BlockStmt body").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("InterfaceDecl"), new Name("TypeDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Name name").build(), param("NodeList<TypeParameter> typeParams").build(), param("NodeList<QualifiedType> extendsClause").build(), param("NodeList<MemberDecl> members").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("LocalVariableDecl"), new Name("Decl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("Type type").build(), param("NodeList<VariableDeclarator> variables").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("MethodDecl"), new Name("MemberDecl"), NodeList.of(param("NodeList<ExtendedModifier> modifiers").build(), param("NodeList<TypeParameter> typeParams").build(), param("Type type").build(), param("Name name").build(), param("NodeList<FormalParameter> params").build(), param("NodeList<ArrayDim> dims").build(), param("NodeList<QualifiedType> throwsClause").build(), param("NodeOption<BlockStmt> body").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("Modifier"), new Name("ExtendedModifier"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("decl"), new Name("PackageDecl"), new Name("Tree"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("QualifiedName name").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("TypeParameter"), new Name("Tree"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("Name name").build(), param("NodeList<Type> bounds").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclarator"), new Name("Tree"), NodeList.of(param("VariableDeclaratorId id").build(), param("NodeOption<Expr> init").build())),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclaratorId"), new Name("Tree"), NodeList.of(param("Name name").build(), param("NodeList<ArrayDim> dims").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayAccessExpr"), new Name("Expr"), NodeList.of(param("Expr name").build(), param("Expr index").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayCreationExpr"), new Name("Expr"), NodeList.of(param("Type type").build(), param("NodeList<ArrayDimExpr> dimExprs").build(), param("NodeList<ArrayDim> dims").build(), param("NodeOption<ArrayInitializerExpr> init").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayDimExpr"), new Name("Tree"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("Expr expr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayInitializerExpr"), new Name("Expr"), NodeList.of(param("NodeList<Expr> values").build(), param("boolean trailingComma").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("AssignExpr"), new Name("Expr"), NodeList.of(param("Expr target").build(), param("AssignOp op").build(), param("Expr value").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("BinaryExpr"), new Name("Expr"), NodeList.of(param("Expr left").build(), param("BinaryOp op").build(), param("Expr right").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("CastExpr"), new Name("Expr"), NodeList.of(param("Type type").build(), param("Expr expr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ClassExpr"), new Name("Expr"), NodeList.of(param("Type type").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ConditionalExpr"), new Name("Expr"), NodeList.of(param("Expr condition").build(), param("Expr thenExpr").build(), param("Expr elseExpr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("FieldAccessExpr"), new Name("Expr"), NodeList.of(param("NodeOption<Expr> scope").build(), param("Name name").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("InstanceOfExpr"), new Name("Expr"), NodeList.of(param("Expr expr").build(), param("Type type").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("LambdaExpr"), new Name("Expr"), NodeList.of(param("NodeList<FormalParameter> params").build(), param("boolean hasParens").build(), param("NodeEither<Expr, BlockStmt> body").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("LiteralExpr"), new Name("Expr"), NodeList.of(param("Class<T> literalClass").build(), param("String literalString").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("MarkerAnnotationExpr"), new Name("AnnotationExpr"), NodeList.of(param("QualifiedName name").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("MemberValuePair"), new Name("Tree"), NodeList.of(param("Name name").build(), param("Expr value").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodInvocationExpr"), new Name("Expr"), NodeList.of(param("NodeOption<Expr> scope").build(), param("NodeList<Type> typeArgs").build(), param("Name name").build(), param("NodeList<Expr> args").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodReferenceExpr"), new Name("Expr"), NodeList.of(param("Expr scope").build(), param("NodeList<Type> typeArgs").build(), param("Name name").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("NormalAnnotationExpr"), new Name("AnnotationExpr"), NodeList.of(param("QualifiedName name").build(), param("NodeList<MemberValuePair> pairs").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ObjectCreationExpr"), new Name("Expr"), NodeList.of(param("NodeOption<Expr> scope").build(), param("NodeList<Type> typeArgs").build(), param("QualifiedType type").build(), param("NodeList<Expr> args").build(), param("NodeOption<NodeList<MemberDecl>> body").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ParenthesizedExpr"), new Name("Expr"), NodeList.of(param("Expr inner").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("SingleMemberAnnotationExpr"), new Name("AnnotationExpr"), NodeList.of(param("QualifiedName name").build(), param("Expr memberValue").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("SuperExpr"), new Name("Expr"), NodeList.of(param("NodeOption<Expr> classExpr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("ThisExpr"), new Name("Expr"), NodeList.of(param("NodeOption<Expr> classExpr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("TypeExpr"), new Name("Expr"), NodeList.of(param("Type type").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("UnaryExpr"), new Name("Expr"), NodeList.of(param("UnaryOp op").build(), param("Expr expr").build())),
			new TreeClassDescriptor(new Name("expr"), new Name("VariableDeclarationExpr"), new Name("Expr"), NodeList.of(param("LocalVariableDecl declaration").build())),
			new TreeClassDescriptor(new Name("name"), new Name("Name"), new Name("Expr"), NodeList.of(param("String id").build())),
			new TreeClassDescriptor(new Name("name"), new Name("QualifiedName"), new Name("Tree"), NodeList.of(param("NodeOption<QualifiedName> qualifier").build(), param("Name name").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("AssertStmt"), new Name("Stmt"), NodeList.of(param("Expr check").build(), param("NodeOption<Expr> msg").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("BlockStmt"), new Name("Stmt"), NodeList.of(param("NodeList<Stmt> stmts").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("BreakStmt"), new Name("Stmt"), NodeList.of(param("NodeOption<Name> id").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("CatchClause"), new Name("Tree"), NodeList.of(param("FormalParameter except").build(), param("BlockStmt catchBlock").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ContinueStmt"), new Name("Stmt"), NodeList.of(param("NodeOption<Name> id").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("DoStmt"), new Name("Stmt"), NodeList.of(param("Stmt body").build(), param("Expr condition").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("EmptyStmt"), new Name("Stmt"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExplicitConstructorInvocationStmt"), new Name("Stmt"), NodeList.of(param("NodeList<Type> typeArgs").build(), param("boolean isThis").build(), param("NodeOption<Expr> expr").build(), param("NodeList<Expr> args").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExpressionStmt"), new Name("Stmt"), NodeList.of(param("Expr expr").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForStmt"), new Name("Stmt"), NodeList.of(param("NodeList<Expr> init").build(), param("Expr compare").build(), param("NodeList<Expr> update").build(), param("Stmt body").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForeachStmt"), new Name("Stmt"), NodeList.of(param("VariableDeclarationExpr var").build(), param("Expr iterable").build(), param("Stmt body").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("IfStmt"), new Name("Stmt"), NodeList.of(param("Expr condition").build(), param("Stmt thenStmt").build(), param("NodeOption<Stmt> elseStmt").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("LabeledStmt"), new Name("Stmt"), NodeList.of(param("Name label").build(), param("Stmt stmt").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ReturnStmt"), new Name("Stmt"), NodeList.of(param("NodeOption<Expr> expr").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchCase"), new Name("Tree"), NodeList.of(param("NodeOption<Expr> label").build(), param("NodeList<Stmt> stmts").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchStmt"), new Name("Stmt"), NodeList.of(param("Expr selector").build(), param("NodeList<SwitchCase> cases").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("SynchronizedStmt"), new Name("Stmt"), NodeList.of(param("Expr expr").build(), param("BlockStmt block").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("ThrowStmt"), new Name("Stmt"), NodeList.of(param("Expr expr").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("TryStmt"), new Name("Stmt"), NodeList.of(param("NodeList<VariableDeclarationExpr> resources").build(), param("boolean trailingSemiColon").build(), param("BlockStmt tryBlock").build(), param("NodeList<CatchClause> catchs").build(), param("NodeOption<BlockStmt> finallyBlock").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("TypeDeclarationStmt"), new Name("Stmt"), NodeList.of(param("TypeDecl typeDecl").build())),
			new TreeClassDescriptor(new Name("stmt"), new Name("WhileStmt"), new Name("Stmt"), NodeList.of(param("Expr condition").build(), param("Stmt body").build())),
			new TreeClassDescriptor(new Name("type"), new Name("ArrayType"), new Name("ReferenceType"), NodeList.of(param("Type componentType").build(), param("NodeList<ArrayDim> dims").build())),
			new TreeClassDescriptor(new Name("type"), new Name("IntersectionType"), new Name("Type"), NodeList.of(param("NodeList<Type> types").build())),
			new TreeClassDescriptor(new Name("type"), new Name("PrimitiveType"), new Name("Type"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("Primitive primitive").build())),
			new TreeClassDescriptor(new Name("type"), new Name("QualifiedType"), new Name("ReferenceType"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("NodeOption<QualifiedType> scope").build(), param("Name name").build(), param("NodeOption<NodeList<Type>> typeArgs").build())),
			new TreeClassDescriptor(new Name("type"), new Name("UnionType"), new Name("Type"), NodeList.of(param("NodeList<Type> types").build())),
			new TreeClassDescriptor(new Name("type"), new Name("UnknownType"), new Name("Type"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("type"), new Name("VoidType"), new Name("Type"), NodeList.<FormalParameter>empty()),
			new TreeClassDescriptor(new Name("type"), new Name("WildcardType"), new Name("Type"), NodeList.of(param("NodeList<AnnotationExpr> annotations").build(), param("NodeOption<ReferenceType> ext").build(), param("NodeOption<ReferenceType> sup").build())),
	};
}
