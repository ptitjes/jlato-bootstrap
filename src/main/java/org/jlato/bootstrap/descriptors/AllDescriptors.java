package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.util.HashMap;

import static org.jlato.rewrite.Quotes.*;

/**
 * @author Didier Villevalois
 */
public class AllDescriptors {

	public static TreeTypeDescriptor get(Name name) {
		return perName.get(name);
	}

	private static final HashMap<Name, TreeTypeDescriptor> perName = new HashMap<>();

	public static final TreeInterfaceDescriptor[] ALL_INTERFACES = new TreeInterfaceDescriptor[] {
			new TreeInterfaceDescriptor(new Name("decl"), new Name("Decl"), "declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.<MemberDecl>empty(),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("ExtendedModifier"), "extended modifier",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("LexicalShape singleLineShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(space())\n" +
									"\t);").build(),
							memberDecl("LexicalShape multiLineShape = list(\n" +
									"\t\t\tnone(),\n" +
									"\t\t\talternative(\n" +
									"\t\t\t\t\twithKind(Kind.Modifier),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(space()),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t\t\t),\n" +
									"\t\t\talternative(\n" +
									"\t\t\t\t\tchildHas(SNodeListState.lastTraversal(), withKind(Kind.Modifier)),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(space()),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("MemberDecl"), "member declaration",
					NodeList.of(
							(QualifiedType) type("Decl").build()
					),
					NodeList.of(
							memberDecl("LexicalShape bodyShape = list(true,\n" +
									"\t\t\talternative(empty(),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacing(space(), newLine())\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(TYPE_BODY)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacing(space(), spacing(ClassBody_BeforeMembers))\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(TYPE_BODY))\n" +
									"\t\t\t),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BetweenMembers)),\n" +
									"\t\t\talternative(empty(),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TYPE_BODY)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TYPE_BODY))\n" +
									"\t\t\t\t\t\t\t.withSpacingBefore(spacing(ClassBody_AfterMembers))\n" +
									"\t\t\t)\n" +
									"\t);").build(),
							memberDecl("LexicalShape membersShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BeforeMembers)),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BetweenMembers)),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_AfterMembers))\n" +
									"\t);").build()
					),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("TypeDecl"), "type declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("LexicalShape listShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(CompilationUnit_BetweenTopLevelDecl))\n" +
									"\t);").build()
					),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("expr"), new Name("AnnotationExpr"), "annotation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build(),
							(QualifiedType) type("ExtendedModifier").build()
					),
					NodeList.of(
							memberDecl("LexicalShape singleLineAnnotationsShape = list(\n" +
									"\t\t\tnone(),\n" +
									"\t\t\tnone().withSpacingAfter(space()),\n" +
									"\t\t\tnone().withSpacingAfter(space())\n" +
									"\t);").build(),
							memberDecl("LexicalShape singleLineAnnotationsShapeWithSpaceBefore = list(\n" +
									"\t\t\tnone().withSpacingBefore(space()),\n" +
									"\t\t\tnone().withSpacingBefore(space()),\n" +
									"\t\t\tnone().withSpacingBefore(space())\n" +
									"\t);").build(),
							memberDecl("LexicalShape multiLineAnnotationsShape = list(\n" +
									"\t\t\tnone(),\n" +
									"\t\t\tnone().withSpacingAfter(newLine()),\n" +
									"\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("QualifiedName name").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("expr"), new Name("Expr"), "expression",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("LexicalShape argumentsShape = list(true,\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build(),
							memberDecl("LexicalShape listShape = list(\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space())\n" +
									"\t);").build()
					),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("stmt"), new Name("Stmt"), "statement",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("LexicalShape listShape = list(none().withSpacingAfter(newLine()));").build()
					),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("type"), new Name("ReferenceType"), "reference type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.<MemberDecl>empty(),
					NodeList.<FormalParameter>empty()
			),
			new TreeInterfaceDescriptor(new Name("type"), new Name("Type"), "type",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("LexicalShape typeArgumentsShape = list(\n" +
									"\t\t\ttoken(LToken.Less),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.Greater)\n" +
									"\t);").build(),
							memberDecl("LexicalShape typeArgumentsOrDiamondShape = list(true,\n" +
									"\t\t\ttoken(LToken.Less),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.Greater)\n" +
									"\t);").build(),
							memberDecl("LexicalShape intersectionShape = list(token(LToken.BinAnd).withSpacing(space(), space()));").build(),
							memberDecl("LexicalShape unionShape = list(token(LToken.BinOr).withSpacing(space(), space()));").build()
					),
					NodeList.<FormalParameter>empty()
			),
	};


	public static final TreeClassDescriptor[] ALL_CLASSES = new TreeClassDescriptor[] {
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationDecl"), "annotation type declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\ttoken(LToken.At), token(LToken.Interface).withSpacingAfter(space()),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(MEMBERS, MemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<MemberDecl> members").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("AnnotationMemberDecl"), "annotation type member declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape defaultValShape = composite(token(LToken.Default).withSpacingBefore(space()), element());").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE), child(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), token(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(DEFAULT_VALUE, when(some(), defaultValShape)),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<Expr> defaultValue").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ArrayDim"), "array dimension",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, AnnotationExpr.singleLineAnnotationsShapeWithSpaceBefore),\n" +
									"\t\t\ttoken(LToken.BracketLeft), token(LToken.BracketRight)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ClassDecl"), "class declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Class),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_PARAMS, TypeParameter.listShape),\n" +
									"\t\t\tchild(EXTENDS_CLAUSE, when(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Extends), element())\n" +
									"\t\t\t)),\n" +
									"\t\t\tchild(IMPLEMENTS_CLAUSE, QualifiedType.implementsClauseShape),\n" +
									"\t\t\tchild(MEMBERS, MemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeOption<QualifiedType> extendsClause").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<MemberDecl> members").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("CompilationUnit"), "compilation unit",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(PACKAGE_DECL).withSpacingAfter(spacing(CompilationUnit_AfterPackageDecl)),\n" +
									"\t\t\tchild(IMPORTS, ImportDecl.listShape),\n" +
									"\t\t\tchild(TYPES, TypeDecl.listShape),\n" +
									"\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("PackageDecl packageDecl").build(),
							param("NodeList<ImportDecl> imports").build(),
							param("NodeList<TypeDecl> types").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ConstructorDecl"), "constructor declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE_PARAMS, TypeParameter.listShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PARAMS, FormalParameter.listShape),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(THROWS_CLAUSE, QualifiedType.throwsClauseShape),\n" +
									"\t\t\tnone().withSpacingAfter(space()), child(BODY)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("BlockStmt body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyMemberDecl"), "empty member declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EmptyTypeDecl"), "empty type declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumConstantDecl"), "enum constant declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(ARGS, when(some(), element(Expr.argumentsShape))),\n" +
									"\t\t\tchild(CLASS_BODY, when(some(),\n" +
									"\t\t\t\t\telement(MemberDecl.bodyShape).withSpacingAfter(spacing(EnumConstant_AfterBody))\n" +
									"\t\t\t))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(EnumBody_BeforeConstants)),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(spacing(EnumBody_BetweenConstants)),\n" +
									"\t\t\tnull\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Expr>> args").build(),
							param("NodeOption<NodeList<MemberDecl>> classBody").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("EnumDecl"), "enum declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Enum),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(IMPLEMENTS_CLAUSE, QualifiedType.implementsClauseShape),\n" +
									"\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t.withSpacingBefore(space())\n" +
									"\t\t\t\t\t.withIndentationAfter(indent(TYPE_BODY)),\n" +
									"\t\t\tchild(ENUM_CONSTANTS, EnumConstantDecl.listShape),\n" +
									"\t\t\twhen(data(TRAILING_COMMA), token(LToken.Comma).withSpacingAfter(spacing(EnumBody_BetweenConstants))),\n" +
									"\t\t\twhen(childIs(MEMBERS, empty()),\n" +
									"\t\t\t\t\talternative(childIs(ENUM_CONSTANTS, empty()),\n" +
									"\t\t\t\t\t\t\tnone().withSpacingAfter(newLine()),\n" +
									"\t\t\t\t\t\t\tnone().withSpacingAfter(spacing(EnumBody_AfterConstants))\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t),\n" +
									"\t\t\twhen(childIs(MEMBERS, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.SemiColon).withSpacingAfter(spacing(EnumBody_AfterConstants))\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(MEMBERS, MemberDecl.membersShape),\n" +
									"\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t.withIndentationBefore(unIndent(TYPE_BODY))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<EnumConstantDecl> enumConstants").build(),
							param("boolean trailingComma").build(),
							param("NodeList<MemberDecl> members").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FieldDecl"), "field declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(VARIABLES, VariableDeclarator.listShape).withSpacingBefore(space()),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("FormalParameter"), "formal parameter",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.singleLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\twhen(data(VAR_ARGS), token(LToken.Ellipsis)),\n" +
									"\t\t\twhen(not(childIs(TYPE, withKind(Kind.UnknownType))), none().withSpacingAfter(space())),\n" +
									"\t\t\tchild(ID)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(true,\n" +
									"\t\t\tnone(),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\tnone()\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("boolean isVarArgs").build(),
							param("VariableDeclaratorId id").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("ImportDecl"), "import declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Import),\n" +
									"\t\t\twhen(data(STATIC), keyword(LToken.Static)),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\twhen(data(ON_DEMAND), composite(token(LToken.Dot), token(LToken.Times))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(\n" +
									"\t\t\tnone(),\n" +
									"\t\t\tnone().withSpacingAfter(newLine()),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(CompilationUnit_AfterImports))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("QualifiedName name").build(),
							param("boolean isStatic").build(),
							param("boolean isOnDemand").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InitializerDecl"), "initializer declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("BlockStmt body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("InterfaceDecl"), "interface declaration",
					NodeList.of(
							(QualifiedType) type("TypeDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Interface),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_PARAMS, TypeParameter.listShape),\n" +
									"\t\t\tchild(EXTENDS_CLAUSE, QualifiedType.extendsClauseShape),\n" +
									"\t\t\tchild(MEMBERS, MemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeList<QualifiedType> extendsClause").build(),
							param("NodeList<MemberDecl> members").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("LocalVariableDecl"), "local variable declaration",
					NodeList.of(
							(QualifiedType) type("Decl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.singleLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(VARIABLES, VariableDeclarator.listShape).withSpacingBefore(space())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("MethodDecl"), "method declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, ExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE_PARAMS, TypeParameter.listShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tnone().withSpacingAfter(space()),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PARAMS, FormalParameter.listShape),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(DIMS, ArrayDim.listShape),\n" +
									"\t\t\tchild(THROWS_CLAUSE, QualifiedType.throwsClauseShape),\n" +
									"\t\t\tchild(BODY, alternative(some(),\n" +
									"\t\t\t\t\telement().withSpacingBefore(space()),\n" +
									"\t\t\t\t\ttoken(LToken.SemiColon)\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("NodeOption<BlockStmt> body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("Modifier"), "modifier",
					NodeList.of(
							(QualifiedType) type("ExtendedModifier").build()
					),
					NodeList.<MemberDecl>empty(),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					true
			),
			new TreeClassDescriptor(new Name("decl"), new Name("PackageDecl"), "package declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, list()),\n" +
									"\t\t\tkeyword(LToken.Package),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("QualifiedName name").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("TypeParameter"), "type parameter",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape boundsShape = list(\n" +
									"\t\t\tkeyword(LToken.Extends),\n" +
									"\t\t\ttoken(LToken.BinAnd).withSpacing(space(), space()),\n" +
									"\t\t\tnone()\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, list()),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(BOUNDS, boundsShape)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(\n" +
									"\t\t\ttoken(LToken.Less),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.Greater).withSpacingAfter(space())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Name name").build(),
							param("NodeList<Type> bounds").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclarator"), "variable declarator",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape initializerShape = composite(\n" +
									"\t\t\ttoken(LToken.Assign).withSpacing(space(), space()),\n" +
									"\t\t\telement()\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ID),\n" +
									"\t\t\tchild(INIT, when(some(), initializerShape))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(none(), token(LToken.Comma).withSpacingAfter(space()), none());").build()
					),
					NodeList.of(
							param("VariableDeclaratorId id").build(),
							param("NodeOption<Expr> init").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("decl"), new Name("VariableDeclaratorId"), "variable declarator identifier",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(DIMS, list())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayAccessExpr"), "array access expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.BracketLeft), child(INDEX), token(LToken.BracketRight)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr name").build(),
							param("Expr index").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayCreationExpr"), "array creation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.New),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(DIM_EXPRS, list()),\n" +
									"\t\t\tchild(DIMS, list()),\n" +
									"\t\t\tchild(INIT, when(some(),\n" +
									"\t\t\t\t\telement().withSpacingBefore(space())\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Type type").build(),
							param("NodeList<ArrayDimExpr> dimExprs").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<ArrayInitializerExpr> init").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayDimExpr"), "array dimension expression",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, AnnotationExpr.singleLineAnnotationsShapeWithSpaceBefore),\n" +
									"\t\t\ttoken(LToken.BracketLeft), child(EXPR), token(LToken.BracketRight)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Expr expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ArrayInitializerExpr"), "array initializer expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\talternative(childIs(VALUES, not(empty())), composite(\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft).withSpacingAfter(space()),\n" +
									"\t\t\t\t\tchild(VALUES, Expr.listShape),\n" +
									"\t\t\t\t\twhen(data(TRAILING_COMMA), token(LToken.Comma)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight).withSpacingBefore(space())\n" +
									"\t\t\t), composite(\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft),\n" +
									"\t\t\t\t\twhen(data(TRAILING_COMMA), token(LToken.Comma)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Expr> values").build(),
							param("boolean trailingComma").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("AssignExpr"), "assignment expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TARGET),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\t\t\tfinal AssignOp op = ((State) tree.state).op;\n" +
									"\t\t\t\t\tswitch (op) {\n" +
									"\t\t\t\t\t\tcase Normal:\n" +
									"\t\t\t\t\t\t\treturn LToken.Assign;\n" +
									"\t\t\t\t\t\tcase Plus:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignPlus;\n" +
									"\t\t\t\t\t\tcase Minus:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignMinus;\n" +
									"\t\t\t\t\t\tcase Times:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignTimes;\n" +
									"\t\t\t\t\t\tcase Divide:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignDivide;\n" +
									"\t\t\t\t\t\tcase And:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignAnd;\n" +
									"\t\t\t\t\t\tcase Or:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignOr;\n" +
									"\t\t\t\t\t\tcase XOr:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignXOr;\n" +
									"\t\t\t\t\t\tcase Remainder:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignRemainder;\n" +
									"\t\t\t\t\t\tcase LeftShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignLShift;\n" +
									"\t\t\t\t\t\tcase RightSignedShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignRSignedShift;\n" +
									"\t\t\t\t\t\tcase RightUnsignedShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.AssignRUnsignedShift;\n" +
									"\t\t\t\t\t\tdefault:\n" +
									"\t\t\t\t\t\t\t// Can't happen by definition of enum\n" +
									"\t\t\t\t\t\t\tthrow new IllegalStateException();\n" +
									"\t\t\t\t\t}\n" +
									"\t\t\t\t}\n" +
									"\t\t\t}).withSpacing(space(), space()),\n" +
									"\t\t\tchild(VALUE)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr target").build(),
							param("AssignOp op").build(),
							param("Expr value").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("BinaryExpr"), "binary expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(LEFT),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\t\t\tfinal BinaryOp op = ((State) tree.state).op;\n" +
									"\t\t\t\t\tswitch (op) {\n" +
									"\t\t\t\t\t\tcase Or:\n" +
									"\t\t\t\t\t\t\treturn LToken.Or;\n" +
									"\t\t\t\t\t\tcase And:\n" +
									"\t\t\t\t\t\t\treturn LToken.And;\n" +
									"\t\t\t\t\t\tcase BinOr:\n" +
									"\t\t\t\t\t\t\treturn LToken.BinOr;\n" +
									"\t\t\t\t\t\tcase BinAnd:\n" +
									"\t\t\t\t\t\t\treturn LToken.BinAnd;\n" +
									"\t\t\t\t\t\tcase XOr:\n" +
									"\t\t\t\t\t\t\treturn LToken.XOr;\n" +
									"\t\t\t\t\t\tcase Equal:\n" +
									"\t\t\t\t\t\t\treturn LToken.Equal;\n" +
									"\t\t\t\t\t\tcase NotEqual:\n" +
									"\t\t\t\t\t\t\treturn LToken.NotEqual;\n" +
									"\t\t\t\t\t\tcase Less:\n" +
									"\t\t\t\t\t\t\treturn LToken.Less;\n" +
									"\t\t\t\t\t\tcase Greater:\n" +
									"\t\t\t\t\t\t\treturn LToken.Greater;\n" +
									"\t\t\t\t\t\tcase LessOrEqual:\n" +
									"\t\t\t\t\t\t\treturn LToken.LessOrEqual;\n" +
									"\t\t\t\t\t\tcase GreaterOrEqual:\n" +
									"\t\t\t\t\t\t\treturn LToken.GreaterOrEqual;\n" +
									"\t\t\t\t\t\tcase LeftShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.LShift;\n" +
									"\t\t\t\t\t\tcase RightSignedShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.RSignedShift;\n" +
									"\t\t\t\t\t\tcase RightUnsignedShift:\n" +
									"\t\t\t\t\t\t\treturn LToken.RUnsignedShift;\n" +
									"\t\t\t\t\t\tcase Plus:\n" +
									"\t\t\t\t\t\t\treturn LToken.Plus;\n" +
									"\t\t\t\t\t\tcase Minus:\n" +
									"\t\t\t\t\t\t\treturn LToken.Minus;\n" +
									"\t\t\t\t\t\tcase Times:\n" +
									"\t\t\t\t\t\t\treturn LToken.Times;\n" +
									"\t\t\t\t\t\tcase Divide:\n" +
									"\t\t\t\t\t\t\treturn LToken.Divide;\n" +
									"\t\t\t\t\t\tcase Remainder:\n" +
									"\t\t\t\t\t\t\treturn LToken.Remainder;\n" +
									"\t\t\t\t\t\tdefault:\n" +
									"\t\t\t\t\t\t\t// Can't happen by definition of enum\n" +
									"\t\t\t\t\t\t\tthrow new IllegalStateException();\n" +
									"\t\t\t\t\t}\n" +
									"\t\t\t\t}\n" +
									"\t\t\t}).withSpacing(space(), space()),\n" +
									"\t\t\tchild(RIGHT)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr left").build(),
							param("BinaryOp op").build(),
							param("Expr right").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("CastExpr"), "cast expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), child(TYPE), token(LToken.ParenthesisRight).withSpacingAfter(space()), child(EXPR)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Type type").build(),
							param("Expr expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ClassExpr"), "'class' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\ttoken(LToken.Dot), token(LToken.Class)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Type type").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ConditionalExpr"), "conditional expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.QuestionMark).withSpacing(space(), space()),\n" +
									"\t\t\tchild(THEN_EXPR),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacing(space(), space()),\n" +
									"\t\t\tchild(ELSE_EXPR)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr condition").build(),
							param("Expr thenExpr").build(),
							param("Expr elseExpr").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("FieldAccessExpr"), "field access expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("Name name").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("InstanceOfExpr"), "'instanceof' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR),\n" +
									"\t\t\tkeyword(LToken.InstanceOf),\n" +
									"\t\t\tchild(TYPE)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr expr").build(),
							param("Type type").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LambdaExpr"), "lambda expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\twhen(data(PARENS), token(LToken.ParenthesisLeft)),\n" +
									"\t\t\tchild(PARAMS, Expr.listShape),\n" +
									"\t\t\twhen(data(PARENS), token(LToken.ParenthesisRight)),\n" +
									"\t\t\ttoken(LToken.Arrow).withSpacing(space(), space()),\n" +
									"\t\t\tchild(BODY, leftOrRight())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<FormalParameter> params").build(),
							param("boolean hasParens").build(),
							param("NodeEither<Expr, BlockStmt> body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) expr("true").build(),
							(Expr) expr("NodeEither.<Expr, BlockStmt>right(blockStmt())").build()
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("LiteralExpr"), "literal expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.<MemberDecl>empty(),
					NodeList.of(
							param("Class<T> literalClass").build(),
							param("String literalString").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MarkerAnnotationExpr"), "marker annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("QualifiedName name").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MemberValuePair"), "annotation member value pair",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.Assign).withSpacing(space(), space()),\n" +
									"\t\t\tchild(VALUE)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Name name").build(),
							param("Expr value").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodInvocationExpr"), "method invocation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(TYPE_ARGS, Type.typeArgumentsShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(ARGS, Expr.argumentsShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build(),
							param("NodeList<Expr> args").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("MethodReferenceExpr"), "method reference expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE),\n" +
									"\t\t\ttoken(LToken.DoubleColon),\n" +
									"\t\t\tchild(TYPE_ARGS, Type.typeArgumentsShape),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("NormalAnnotationExpr"), "normal annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PAIRS, list(token(LToken.Comma).withSpacingAfter(space()))),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("QualifiedName name").build(),
							param("NodeList<MemberValuePair> pairs").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ObjectCreationExpr"), "object creation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.New).withSpacingAfter(space()),\n" +
									"\t\t\tchild(TYPE_ARGS, Type.typeArgumentsShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(ARGS, Expr.argumentsShape),\n" +
									"\t\t\tchild(BODY, when(some(), element(MemberDecl.bodyShape)))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("QualifiedType type").build(),
							param("NodeList<Expr> args").build(),
							param("NodeOption<NodeList<MemberDecl>> body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ParenthesizedExpr"), "parenthesized expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), child(INNER), token(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr inner").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SingleMemberAnnotationExpr"), "single member annotation expression",
					NodeList.of(
							(QualifiedType) type("AnnotationExpr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(MEMBER_VALUE),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("QualifiedName name").build(),
							param("Expr memberValue").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("SuperExpr"), "'super' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CLASS_EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.Super)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("ThisExpr"), "'this' expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CLASS_EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.This)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> classExpr").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("TypeExpr"), "type expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = child(TYPE);").build()
					),
					NodeList.of(
							param("Type type").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("UnaryExpr"), "unary expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape opShape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\tfinal UnaryOp op = ((State) tree.state).op;\n" +
									"\t\t\tswitch (op) {\n" +
									"\t\t\t\tcase Positive:\n" +
									"\t\t\t\t\treturn LToken.Plus;\n" +
									"\t\t\t\tcase Negative:\n" +
									"\t\t\t\t\treturn LToken.Minus;\n" +
									"\t\t\t\tcase PreIncrement:\n" +
									"\t\t\t\t\treturn LToken.Increment;\n" +
									"\t\t\t\tcase PreDecrement:\n" +
									"\t\t\t\t\treturn LToken.Decrement;\n" +
									"\t\t\t\tcase Not:\n" +
									"\t\t\t\t\treturn LToken.Not;\n" +
									"\t\t\t\tcase Inverse:\n" +
									"\t\t\t\t\treturn LToken.Inverse;\n" +
									"\t\t\t\tcase PostIncrement:\n" +
									"\t\t\t\t\treturn LToken.Increment;\n" +
									"\t\t\t\tcase PostDecrement:\n" +
									"\t\t\t\t\treturn LToken.Decrement;\n" +
									"\t\t\t\tdefault:\n" +
									"\t\t\t\t\t// Can't happen by definition of enum\n" +
									"\t\t\t\t\tthrow new IllegalStateException();\n" +
									"\t\t\t}\n" +
									"\t\t}\n" +
									"\t});").build(),
							memberDecl("public static final LexicalShape shape = alternative(new LSCondition() {\n" +
									"\t\tpublic boolean test(STree tree) {\n" +
									"\t\t\tfinal UnaryOp op = ((State) tree.state).op;\n" +
									"\t\t\treturn op.isPrefix();\n" +
									"\t\t}\n" +
									"\t}, composite(opShape, child(EXPR)), composite(child(EXPR), opShape));").build()
					),
					NodeList.of(
							param("UnaryOp op").build(),
							param("Expr expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("expr"), new Name("VariableDeclarationExpr"), "variable declaration expression",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = child(DECLARATION);").build()
					),
					NodeList.of(
							param("LocalVariableDecl declaration").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("name"), new Name("Name"), "name",
					NodeList.of(
							(QualifiedType) type("Expr").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\treturn new LToken(ParserImplConstants.IDENTIFIER, ((State) tree.state).id);\n" +
									"\t\t}\n" +
									"\t});").build()
					),
					NodeList.of(
							param("String id").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("name"), new Name("QualifiedName"), "qualified name",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(QUALIFIER, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<QualifiedName> qualifier").build(),
							param("Name name").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("AssertStmt"), "'assert' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Assert),\n" +
									"\t\t\tchild(CHECK),\n" +
									"\t\t\tchild(MSG, when(some(),\n" +
									"\t\t\t\t\tcomposite(token(LToken.Colon).withSpacing(space(), space()), element())\n" +
									"\t\t\t)),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr check").build(),
							param("NodeOption<Expr> msg").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BlockStmt"), "block statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = alternative(childIs(STMTS, not(empty())),\n" +
									"\t\t\tcomposite(\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(BLOCK)),\n" +
									"\t\t\t\t\tchild(STMTS, listShape),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(BLOCK))\n" +
									"\t\t\t\t\t\t\t.withSpacingBefore(newLine())\n" +
									"\t\t\t),\n" +
									"\t\t\tcomposite(\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(BLOCK)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(BLOCK))\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Stmt> stmts").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("BreakStmt"), "'break' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Break),\n" +
									"\t\t\tchild(ID, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Name> id").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("CatchClause"), "catch clause",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Catch),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(EXCEPT),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(CATCH_BLOCK)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					NodeList.of(
							param("FormalParameter except").build(),
							param("BlockStmt catchBlock").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ContinueStmt"), "'continue' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Continue),\n" +
									"\t\t\tchild(ID, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Name> id").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("DoStmt"), "'do-while' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Do),\n" +
									"\t\t\tchild(BODY),\n" +
									"\t\t\tkeyword(LToken.While),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Stmt body").build(),
							param("Expr condition").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("EmptyStmt"), "empty statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExplicitConstructorInvocationStmt"), "explicit constructor invocation statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(TYPE_ARGS, Type.typeArgumentsShape),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\t\t\treturn ((State) tree.state).isThis ? LToken.This : LToken.Super;\n" +
									"\t\t\t\t}\n" +
									"\t\t\t}),\n" +
									"\t\t\tchild(ARGS, Expr.argumentsShape),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Type> typeArgs").build(),
							param("boolean isThis").build(),
							param("NodeOption<Expr> expr").build(),
							param("NodeList<Expr> args").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ExpressionStmt"), "expression statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR), token(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForStmt"), "'for' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.For), token(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(INIT, list(token(LToken.Comma).withSpacingAfter(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon).withSpacingAfter(space()),\n" +
									"\t\t\tchild(COMPARE),\n" +
									"\t\t\ttoken(LToken.SemiColon).withSpacingAfter(space()),\n" +
									"\t\t\tchild(UPDATE, list(token(LToken.Comma).withSpacingAfter(space()))),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Expr> init").build(),
							param("Expr compare").build(),
							param("NodeList<Expr> update").build(),
							param("Stmt body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ForeachStmt"), "\"enhanced\" 'for' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.For), token(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(VAR),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacing(space(), space()),\n" +
									"\t\t\tchild(ITERABLE),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("VariableDeclarationExpr var").build(),
							param("Expr iterable").build(),
							param("Stmt body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("IfStmt"), "'if' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.If),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(THEN_STMT,\n" +
									"\t\t\t\t\talternative(withKind(Kind.BlockStmt),\n" +
									"\t\t\t\t\t\t\tdefaultShape().withSpacingBefore(space()),\n" +
									"\t\t\t\t\t\t\talternative(withKind(Kind.ExpressionStmt),\n" +
									"\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ThenExpressionStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine()),\n" +
									"\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ThenOtherStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t)\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(ELSE_STMT, when(some(), composite(\n" +
									"\t\t\t\t\tkeyword(LToken.Else),\n" +
									"\t\t\t\t\telement(alternative(withKind(Kind.BlockStmt),\n" +
									"\t\t\t\t\t\t\tdefaultShape().withSpacingBefore(space()),\n" +
									"\t\t\t\t\t\t\talternative(withKind(Kind.IfStmt),\n" +
									"\t\t\t\t\t\t\t\t\tdefaultShape().withSpacingBefore(spacing(IfStmt_ElseIfStmt)),\n" +
									"\t\t\t\t\t\t\t\t\talternative(withKind(Kind.ExpressionStmt),\n" +
									"\t\t\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ElseExpressionStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(BLOCK)),\n" +
									"\t\t\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ElseOtherStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t\t\t)\n" +
									"\t\t\t\t\t\t\t)\n" +
									"\t\t\t\t\t))\n" +
									"\t\t\t)))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt thenStmt").build(),
							param("NodeOption<Stmt> elseStmt").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("LabeledStmt"), "labeled statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tnone().withIndentationAfter(indent(IndentationContext.LABEL)),\n" +
									"\t\t\tchild(LABEL),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacingAfter(spacing(LabeledStmt_AfterLabel)),\n" +
									"\t\t\tnone().withIndentationBefore(unIndent(IndentationContext.LABEL)),\n" +
									"\t\t\tchild(STMT)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Name label").build(),
							param("Stmt stmt").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ReturnStmt"), "'return' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Return),\n" +
									"\t\t\tchild(EXPR, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeOption<Expr> expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchCase"), "'switch' case",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(LABEL, alternative(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Case), element()),\n" +
									"\t\t\t\t\ttoken(LToken.Default)\n" +
									"\t\t\t)),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacingAfter(newLine()),\n" +
									"\t\t\tnone().withIndentationAfter(indent(BLOCK)),\n" +
									"\t\t\tchild(STMTS, Stmt.listShape),\n" +
									"\t\t\tnone().withIndentationBefore(unIndent(BLOCK))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(none().withSpacingAfter(newLine()));").build()
					),
					NodeList.of(
							param("NodeOption<Expr> label").build(),
							param("NodeList<Stmt> stmts").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SwitchStmt"), "'switch' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Switch),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(spacing(SwitchStmt_AfterSwitchKeyword)),\n" +
									"\t\t\tchild(SELECTOR),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\talternative(childIs(CASES, not(empty())),\n" +
									"\t\t\t\t\tcomposite(\n" +
									"\t\t\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationAfter(indent(BLOCK)),\n" +
									"\t\t\t\t\t\t\tchild(CASES, SwitchCase.listShape),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationBefore(unIndent(BLOCK))\n" +
									"\t\t\t\t\t\t\t\t\t.withSpacingBefore(newLine())\n" +
									"\t\t\t\t\t),\n" +
									"\t\t\t\t\tcomposite(\n" +
									"\t\t\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationAfter(indent(BLOCK)),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationBefore(unIndent(BLOCK))\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr selector").build(),
							param("NodeList<SwitchCase> cases").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("SynchronizedStmt"), "'synchronized' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Synchronized),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(EXPR),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BLOCK)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr expr").build(),
							param("BlockStmt block").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("ThrowStmt"), "'throw' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Throw), child(EXPR), token(LToken.SemiColon)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr expr").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TryStmt"), "'try' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Try),\n" +
									"\t\t\twhen(childIs(RESOURCES, not(empty())),\n" +
									"\t\t\t\t\tcomposite(\n" +
									"\t\t\t\t\t\t\ttoken(LToken.ParenthesisLeft)\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationAfter(indent(TRY_RESOURCES)),\n" +
									"\t\t\t\t\t\t\tchild(RESOURCES, list(token(LToken.SemiColon).withSpacingAfter(newLine()))),\n" +
									"\t\t\t\t\t\t\twhen(data(TRAILING_SEMI_COLON), token(LToken.SemiColon)),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t\t\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TRY_RESOURCES))\n" +
									"\t\t\t\t\t\t\t\t\t.withSpacingAfter(space())\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(TRY_BLOCK),\n" +
									"\t\t\tchild(CATCHS, CatchClause.listShape),\n" +
									"\t\t\tchild(FINALLY_BLOCK, when(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Finally), element())\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<VariableDeclarationExpr> resources").build(),
							param("boolean trailingSemiColon").build(),
							param("BlockStmt tryBlock").build(),
							param("NodeList<CatchClause> catchs").build(),
							param("NodeOption<BlockStmt> finallyBlock").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("TypeDeclarationStmt"), "type declaration statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPE_DECL)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("TypeDecl typeDecl").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("stmt"), new Name("WhileStmt"), "'while' statement",
					NodeList.of(
							(QualifiedType) type("Stmt").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.While),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Expr condition").build(),
							param("Stmt body").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("ArrayType"), "array type",
					NodeList.of(
							(QualifiedType) type("ReferenceType").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(COMPONENT_TYPE),\n" +
									"\t\t\tchild(DIMS, list())\n" +
									"\t);").build()
					),
					NodeList.of(
							param("Type componentType").build(),
							param("NodeList<ArrayDim> dims").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("IntersectionType"), "intersection type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPES, Type.intersectionShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Type> types").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("PrimitiveType"), "primitive type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, list()),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(STree tree) {\n" +
									"\t\t\t\t\tfinal Primitive primitive = ((State) tree.state).primitive;\n" +
									"\t\t\t\t\tswitch (primitive) {\n" +
									"\t\t\t\t\t\tcase Boolean:\n" +
									"\t\t\t\t\t\t\treturn LToken.Boolean;\n" +
									"\t\t\t\t\t\tcase Char:\n" +
									"\t\t\t\t\t\t\treturn LToken.Char;\n" +
									"\t\t\t\t\t\tcase Byte:\n" +
									"\t\t\t\t\t\t\treturn LToken.Byte;\n" +
									"\t\t\t\t\t\tcase Short:\n" +
									"\t\t\t\t\t\t\treturn LToken.Short;\n" +
									"\t\t\t\t\t\tcase Int:\n" +
									"\t\t\t\t\t\t\treturn LToken.Int;\n" +
									"\t\t\t\t\t\tcase Long:\n" +
									"\t\t\t\t\t\t\treturn LToken.Long;\n" +
									"\t\t\t\t\t\tcase Float:\n" +
									"\t\t\t\t\t\t\treturn LToken.Float;\n" +
									"\t\t\t\t\t\tcase Double:\n" +
									"\t\t\t\t\t\t\treturn LToken.Double;\n" +
									"\t\t\t\t\t\tdefault:\n" +
									"\t\t\t\t\t\t\t// Can't happen by definition of enum\n" +
									"\t\t\t\t\t\t\tthrow new IllegalStateException();\n" +
									"\t\t\t\t\t}\n" +
									"\t\t\t\t}\n" +
									"\t\t\t})\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Primitive primitive").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("QualifiedType"), "qualified type",
					NodeList.of(
							(QualifiedType) type("ReferenceType").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape scopeShape = composite(element(), token(LToken.Dot));").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), scopeShape)),\n" +
									"\t\t\tchild(ANNOTATIONS, AnnotationExpr.singleLineAnnotationsShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_ARGS, when(some(), element(Type.typeArgumentsOrDiamondShape)))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape extendsClauseShape = list(\n" +
									"\t\t\tkeyword(LToken.Extends),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\tnull\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape implementsClauseShape = list(\n" +
									"\t\t\tkeyword(LToken.Implements),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\tnull\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape throwsClauseShape = list(\n" +
									"\t\t\tkeyword(LToken.Throws),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\tnull\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<QualifiedType> scope").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Type>> typeArgs").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnionType"), "union type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPES, Type.unionShape)\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<Type> types").build()
					),
					NodeList.<Expr>of(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("UnknownType"), "unknown type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = none();").build()
					),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("VoidType"), "void type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = token(LToken.Void);").build()
					),
					NodeList.<FormalParameter>empty(),
					NodeList.<Expr>empty(),
					false
			),
			new TreeClassDescriptor(new Name("type"), new Name("WildcardType"), "wildcard type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					),
					NodeList.of(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, AnnotationExpr.singleLineAnnotationsShape),\n" +
									"\t\t\ttoken(LToken.QuestionMark),\n" +
									"\t\t\tchild(EXT, when(some(), composite(keyword(LToken.Extends), element()))),\n" +
									"\t\t\tchild(SUP, when(some(), composite(keyword(LToken.Super), element())))\n" +
									"\t);").build()
					),
					NodeList.of(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<ReferenceType> ext").build(),
							param("NodeOption<ReferenceType> sup").build()
					),
					NodeList.<Expr>of(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
	};

	static {
		for (TreeInterfaceDescriptor descriptor : ALL_INTERFACES) {
			perName.put(descriptor.name, descriptor);
		}
		for (TreeClassDescriptor descriptor : ALL_CLASSES) {
			perName.put(descriptor.name, descriptor);
		}
	}
}
