package org.jlato.bootstrap.descriptors;

import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.NodeList;
import org.jlato.tree.NodeOption;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.ReferenceType;
import org.jlato.tree.type.Type;
import org.jlato.tree.type.WildcardType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.jlato.pattern.Quotes.*;
import static org.jlato.tree.Trees.*;
import static org.jlato.tree.Trees.qualifiedName;

/**
 * @author Didier Villevalois
 */
public class AllDescriptors {

	public static final String TREE_INTERFACES_PATH = "org/jlato/tree";
	public static final String TREE_CLASSES_PATH = "org/jlato/internal/td";
	public static final String TREE_STATES_PATH = "org/jlato/internal/bu";
	public static final QualifiedName TREE_INTERFACES_ROOT = qualifiedName(TREE_INTERFACES_PATH.replace('/', '.'));
	public static final QualifiedName TREE_CLASSES_ROOT = qualifiedName(TREE_CLASSES_PATH.replace('/', '.'));
	public static final QualifiedName TREE_STATES_ROOT = qualifiedName(TREE_STATES_PATH.replace('/', '.'));
	public static final QualifiedName TREE_COLL_CLASSES_ROOT = qualifiedName((TREE_CLASSES_PATH + "/coll").replace('/', '.'));
	public static final QualifiedName TREE_COLL_STATES_ROOT = qualifiedName((TREE_STATES_PATH + "/coll").replace('/', '.'));

	public static final Name NODE_LIST = name("NodeList");
	public static final Name NODE_OPTION = name("NodeOption");
	public static final Name NODE_EITHER = name("NodeEither");
	public static final Name NODE_MAP = name("NodeMap");
	public static final List<Name> NODE_CONTAINERS = Arrays.asList(
			NODE_LIST, NODE_EITHER, NODE_OPTION, NODE_MAP
	);

	public static final Name ASSIGN_OP = name("AssignOp");
	public static final Name BINARY_OP = name("BinaryOp");
	public static final Name UNARY_OP = name("UnaryOp");
	public static final Name PRIMITIVE = name("Primitive");
	public static final Name MODIFIER_KEYWORD = name("ModifierKeyword");
	public static final List<Name> VALUE_ENUMS = Arrays.asList(
			ASSIGN_OP, BINARY_OP, UNARY_OP, PRIMITIVE, MODIFIER_KEYWORD
	);
	public static final List<Name> VALUE_ENUMS_PKG = Arrays.asList(
			name("expr"), name("expr"), name("expr"), name("type"), name("decl")
	);

	/* Base Interfaces */

	public static final Name TREE_NAME = name("Tree");
	public static final Name NODE_NAME = name("Node");

	public static final QualifiedName TREE_QUALIFIED = qualifiedName(TREE_NAME).withQualifier(TREE_INTERFACES_ROOT);
	public static final QualifiedName NODE_QUALIFIED = qualifiedName(NODE_NAME).withQualifier(TREE_INTERFACES_ROOT);

	public static final List<Name> UTILITY_INTERFACES = Arrays.asList(
			name("Documentable")
	);
	public static final List<Name> UTILITY_INTERFACES_PKG = Arrays.asList(
			name("decl")
	);

	/* Base Classes */

	public static final Name TD_TREE = name("TDTree");
	public static final Name TD_LOCATION = name("TDLocation");
	public static final Name TD_CONTEXT = name("TDContext");

	public static final QualifiedName TD_TREE_QUALIFIED = qualifiedName(TD_TREE).withQualifier(TREE_CLASSES_ROOT);
	public static final QualifiedName TD_LOCATION_QUALIFIED = qualifiedName(TD_LOCATION).withQualifier(TREE_CLASSES_ROOT);
	public static final QualifiedName TD_CONTEXT_QUALIFIED = qualifiedName(TD_CONTEXT).withQualifier(TREE_CLASSES_ROOT);

	/* Base States */

	public static final Name BU_TREE = name("BUTree");
	public static final Name S_TREE = name("STree");
	public static final Name S_NODE = name("SNode");

	public static final QualifiedName BU_TREE_QUALIFIED = qualifiedName(BU_TREE).withQualifier(TREE_STATES_ROOT);
	public static final QualifiedName S_TREE_QUALIFIED = qualifiedName(S_TREE).withQualifier(TREE_STATES_ROOT);
	public static final QualifiedName S_NODE_QUALIFIED = qualifiedName(S_NODE).withQualifier(TREE_STATES_ROOT);

	/* Collections States */

	public static final Name S_NODE_LIST = name("S" + NODE_LIST);
	public static final Name S_NODE_OPTION = name("S" + NODE_OPTION);
	public static final Name S_NODE_EITHER = name("S" + NODE_EITHER);
	public static final Name S_TREE_SET = name("S" + NODE_MAP);

	public static final QualifiedName S_NODE_LIST_QUALIFIED = qualifiedName(S_NODE_LIST).withQualifier(TREE_COLL_STATES_ROOT);
	public static final QualifiedName S_NODE_OPTION_QUALIFIED = qualifiedName(S_NODE_OPTION).withQualifier(TREE_COLL_STATES_ROOT);
	public static final QualifiedName S_NODE_EITHER_QUALIFIED = qualifiedName(S_NODE_EITHER).withQualifier(TREE_COLL_STATES_ROOT);
	public static final QualifiedName S_TREE_SET_QUALIFIED = qualifiedName(S_TREE_SET).withQualifier(TREE_COLL_STATES_ROOT);

	public static TreeTypeDescriptor get(Name name) {
		return perName.get(name);
	}

	public static QualifiedName asStateTypeQualifiedName(Name name) {
		if (name.equals(TREE_NAME) || name.equals(NODE_NAME)) {
			return qualifiedName(S_TREE).withQualifier(TREE_STATES_ROOT);
		} else if (NODE_CONTAINERS.contains(name)) {
			return qualifiedName(name("S" + name)).withQualifier(TREE_COLL_STATES_ROOT);
		}

		final TreeTypeDescriptor descriptor = perName.get(name);
		if (descriptor == null)
			throw new IllegalArgumentException("Can't resolve name '" + name + "'");
		return descriptor.stateTypeQualifiedName();
	}

	public static void addImports(ImportManager importManager, NodeList<? extends Type> types) {
		for (Type type : types) {
			addImports(importManager, type);
		}
	}

	public static void addImports(ImportManager importManager, Type type) {
		if (type instanceof QualifiedType) {
			final QualifiedType qualifiedType = (QualifiedType) type;
			final QualifiedName resolved = resolve(qualifiedType.name());
			if (resolved != null) importManager.addImportByName(resolved);

			final NodeOption<QualifiedType> scope = qualifiedType.scope();
			if (scope.isDefined()) addImports(importManager, scope.get());

			// Poor man's name resolution needs some dirty hacks
			final String id = qualifiedType.name().id();
			if (id.equals("Class")) return;

			final NodeOption<NodeList<Type>> typeArgs = qualifiedType.typeArgs();
			if (typeArgs.isDefined()) addImports(importManager, typeArgs.get());
		} else if (type instanceof WildcardType) {
			final WildcardType wildcardType = (WildcardType) type;

			final NodeOption<ReferenceType> ext = wildcardType.ext();
			if (ext.isDefined()) addImports(importManager, ext.get());

			final NodeOption<ReferenceType> sup = wildcardType.sup();
			if (sup.isDefined()) addImports(importManager, sup.get());
		}
	}

	public static QualifiedName resolve(Name name) {
		final QualifiedName treeRoot = TREE_INTERFACES_ROOT;

		final String id = name.id();
		if (id.equals("Class") || id.equals("String")) {
			return null;
		} else if (name.equals(TREE_NAME) || name.equals(NODE_NAME) || NODE_CONTAINERS.contains(name)) {
			return qualifiedName(name).withQualifier(treeRoot);
		} else if (VALUE_ENUMS.contains(name)) {
			final Name pkg = VALUE_ENUMS_PKG.get(VALUE_ENUMS.indexOf(name));
			return qualifiedName(name).withQualifier(qualifiedName(pkg).withQualifier(treeRoot));
		} else if (UTILITY_INTERFACES.contains(name)) {
			final Name pkg = UTILITY_INTERFACES_PKG.get(UTILITY_INTERFACES.indexOf(name));
			return qualifiedName(name).withQualifier(qualifiedName(pkg).withQualifier(treeRoot));
		}

		final TreeTypeDescriptor descriptor = perName.get(name);
		if (descriptor == null)
			throw new IllegalArgumentException("Can't resolve name '" + name + "'");
		return descriptor.interfaceQualifiedName();
	}

	private static final HashMap<Name, TreeTypeDescriptor> perName = new HashMap<>();

	public static final TreeInterfaceDescriptor[] ALL_INTERFACES = new TreeInterfaceDescriptor[]{
			new TreeInterfaceDescriptor(name("decl"), name("Decl"), "declaration",
					listOf(
							(QualifiedType) type("Node").build()
					),
					emptyList(),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("decl"), name("ExtendedModifier"), "extended modifier",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
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
									"\t\t\t\t\tchildHas(" + AllDescriptors.S_NODE_LIST + ".lastTraversal(), withKind(Kind.Modifier)),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(space()),\n" +
									"\t\t\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("decl"), name("MemberDecl"), "member declaration",
					listOf(
							(QualifiedType) type("Decl").build()
					),
					listOf(
							memberDecl("LexicalShape bodyShape = list(true,\n" +
									"\t\t\talternative(empty(),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacing(space(), newLine())\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(TypeBody)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t\t\t.withSpacing(space(), spacing(ClassBody_BeforeMembers))\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(TypeBody))\n" +
									"\t\t\t),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BetweenMembers)),\n" +
									"\t\t\talternative(empty(),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TypeBody)),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TypeBody))\n" +
									"\t\t\t\t\t\t\t.withSpacingBefore(spacing(ClassBody_AfterMembers))\n" +
									"\t\t\t)\n" +
									"\t);").build(),
							memberDecl("LexicalShape membersShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BeforeMembers)),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_BetweenMembers)),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(ClassBody_AfterMembers))\n" +
									"\t);").build()
					),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("decl"), name("TypeDecl"), "type declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build()
					),
					listOf(
							memberDecl("LexicalShape listShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(CompilationUnit_BetweenTopLevelDecl))\n" +
									"\t);").build()
					),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("expr"), name("AnnotationExpr"), "annotation expression",
					listOf(
							(QualifiedType) type("Expr").build(),
							(QualifiedType) type("ExtendedModifier").build()
					),
					listOf(
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
					listOf(
							param("QualifiedName name").build()
					)
			),
			new TreeInterfaceDescriptor(name("expr"), name("Expr"), "expression",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("LexicalShape argumentsShape = list(true,\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build(),
							memberDecl("LexicalShape listShape = list(\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space())\n" +
									"\t);").build()
					),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("stmt"), name("Stmt"), "statement",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("LexicalShape listShape = list(none().withSpacingAfter(newLine()));").build()
					),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("type"), name("ReferenceType"), "reference type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					emptyList(),
					emptyList()
			),
			new TreeInterfaceDescriptor(name("type"), name("Type"), "type",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
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
					emptyList()
			),
	};


	public static final TreeClassDescriptor[] ALL_CLASSES = new TreeClassDescriptor[]{
			new TreeClassDescriptor(name("decl"), name("AnnotationDecl"), "annotation type declaration",
					listOf(
							(QualifiedType) type("TypeDecl").build(),
							(QualifiedType) type("Documentable<AnnotationDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\ttoken(LToken.At), token(LToken.Interface).withSpacingAfter(space()),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(MEMBERS, SMemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<MemberDecl> members").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("AnnotationMemberDecl"), "annotation type member declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build(),
							(QualifiedType) type("Documentable<AnnotationMemberDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape defaultValShape = composite(token(LToken.Default).withSpacingBefore(space()), element());").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(NAME).withSpacingBefore(space()),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), token(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(DEFAULT_VALUE, when(some(), defaultValShape)),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<Expr> defaultValue").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("ArrayDim"), "array dimension",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, org.jlato.internal.bu.expr.SAnnotationExpr.singleLineAnnotationsShapeWithSpaceBefore),\n" +
									"\t\t\ttoken(LToken.BracketLeft), token(LToken.BracketRight)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					listOf(
							param("NodeList<AnnotationExpr> annotations").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("ClassDecl"), "class declaration",
					listOf(
							(QualifiedType) type("TypeDecl").build(),
							(QualifiedType) type("Documentable<ClassDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Class),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_PARAMS, STypeParameter.listShape),\n" +
									"\t\t\tchild(EXTENDS_CLAUSE, when(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Extends), element())\n" +
									"\t\t\t)),\n" +
									"\t\t\tchild(IMPLEMENTS_CLAUSE, org.jlato.internal.bu.type.SQualifiedType.implementsClauseShape),\n" +
									"\t\t\tchild(MEMBERS, SMemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeOption<QualifiedType> extendsClause").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<MemberDecl> members").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("CompilationUnit"), "compilation unit",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(PACKAGE_DECL).withSpacingAfter(spacing(CompilationUnit_AfterPackageDecl)),\n" +
									"\t\t\tchild(IMPORTS, SImportDecl.listShape),\n" +
									"\t\t\tchild(TYPES, STypeDecl.listShape),\n" +
									"\t\t\tnone().withSpacingAfter(newLine())\n" +
									"\t);").build()
					),
					listOf(
							param("PackageDecl packageDecl").build(),
							param("NodeList<ImportDecl> imports").build(),
							param("NodeList<TypeDecl> types").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("ConstructorDecl"), "constructor declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build(),
							(QualifiedType) type("Documentable<ConstructorDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE_PARAMS, STypeParameter.listShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PARAMS, SFormalParameter.listShape),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(THROWS_CLAUSE, org.jlato.internal.bu.type.SQualifiedType.throwsClauseShape),\n" +
									"\t\t\tnone().withSpacingAfter(space()), child(BODY)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("BlockStmt body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) expr("Trees.blockStmt()").build()
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("EmptyMemberDecl"), "empty member declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					emptyList(),
					emptyList(),
					false
			),
			new TreeClassDescriptor(name("decl"), name("EmptyTypeDecl"), "empty type declaration",
					listOf(
							(QualifiedType) type("TypeDecl").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					emptyList(),
					emptyList(),
					false
			),
			new TreeClassDescriptor(name("decl"), name("EnumConstantDecl"), "enum constant declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build(),
							(QualifiedType) type("Documentable<EnumConstantDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, org.jlato.internal.bu.decl.SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(ARGS, when(some(), element(org.jlato.internal.bu.expr.SExpr.argumentsShape))),\n" +
									"\t\t\tchild(CLASS_BODY, when(some(),\n" +
									"\t\t\t\t\telement(org.jlato.internal.bu.decl.SMemberDecl.bodyShape).withSpacingAfter(spacing(EnumConstant_AfterBody))\n" +
									"\t\t\t))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(\n" +
									"\t\t\tnone().withSpacingAfter(spacing(EnumBody_BeforeConstants)),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(spacing(EnumBody_BetweenConstants)),\n" +
									"\t\t\tnull\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Expr>> args").build(),
							param("NodeOption<NodeList<MemberDecl>> classBody").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("EnumDecl"), "enum declaration",
					listOf(
							(QualifiedType) type("TypeDecl").build(),
							(QualifiedType) type("Documentable<EnumDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Enum),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(IMPLEMENTS_CLAUSE, org.jlato.internal.bu.type.SQualifiedType.implementsClauseShape),\n" +
									"\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t.withSpacingBefore(space())\n" +
									"\t\t\t\t\t.withIndentationAfter(indent(TypeBody)),\n" +
									"\t\t\tchild(ENUM_CONSTANTS, SEnumConstantDecl.listShape),\n" +
									"\t\t\twhen(data(TRAILING_COMMA), token(LToken.Comma).withSpacingAfter(spacing(EnumBody_BetweenConstants))),\n" +
									"\t\t\talternative(childIs(MEMBERS, empty()),\n" +
									"\t\t\t\t\talternative(childIs(ENUM_CONSTANTS, empty()),\n" +
									"\t\t\t\t\t\t\tnone().withSpacingAfter(newLine()),\n" +
									"\t\t\t\t\t\t\tnone().withSpacingAfter(spacing(EnumBody_AfterConstants))\n" +
									"\t\t\t\t\t),\n" +
									"\t\t\t\t\talternative(childIs(ENUM_CONSTANTS, empty()),\n" +
									"\t\t\t\t\t\t\tnone(),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.SemiColon).withSpacingAfter(spacing(EnumBody_AfterConstants))\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(MEMBERS, SMemberDecl.membersShape),\n" +
									"\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t.withIndentationBefore(unIndent(TypeBody))\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<QualifiedType> implementsClause").build(),
							param("NodeList<EnumConstantDecl> enumConstants").build(),
							param("boolean trailingComma").build(),
							param("NodeList<MemberDecl> members").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("FieldDecl"), "field declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build(),
							(QualifiedType) type("Documentable<FieldDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(VARIABLES, SVariableDeclarator.listShape).withSpacingBefore(space()),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("FormalParameter"), "formal parameter",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.singleLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\twhen(data(VAR_ARGS),\n" +
									"\t\t\t\t\talternative(childIs(ELLIPSIS_ANNOTATIONS, not(empty())),\n" +
									"\t\t\t\t\t\t\tcomposite(\n" +
									"\t\t\t\t\t\t\t\t\tchild(ELLIPSIS_ANNOTATIONS, org.jlato.internal.bu.expr.SAnnotationExpr.singleLineAnnotationsShapeWithSpaceBefore),\n" +
									"\t\t\t\t\t\t\t\t\ttoken(LToken.Ellipsis)\n" +
									"\t\t\t\t\t\t\t),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.Ellipsis)\n" +
									"\t\t\t\t\t)\n" +
									"\t\t\t),\n" +
									"\t\t\twhen(not(childIs(TYPE, withKind(Kind.UnknownType))), none().withSpacingAfter(space())),\n" +
									"\t\t\talternative(data(RECEIVER),\n" +
									"\t\t\t\t\tcomposite(\n" +
									"\t\t\t\t\t\t\tchild(RECEIVER_TYPE_NAME, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\t\t\t\t\ttoken(LToken.This)\n" +
									"\t\t\t\t\t),\n" +
									"\t\t\t\t\tchild(ID, element())\n" +
									"\t\t\t)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(true,\n" +
									"\t\t\tnone(),\n" +
									"\t\t\ttoken(LToken.Comma).withSpacingAfter(space()),\n" +
									"\t\t\tnone()\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("boolean isVarArgs").build(),
							param("NodeList<AnnotationExpr> ellipsisAnnotations").build(),
							param("NodeOption<VariableDeclaratorId> id").build(),
							param("boolean isReceiver").build(),
							param("NodeOption<Name> receiverTypeName").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) literalExpr(false),
							(Expr) null,
							(Expr) null,
							(Expr) literalExpr(false),
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("ImportDecl"), "import declaration",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Import),\n" +
									"\t\t\twhen(data(STATIC), keyword(LToken.Static)),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\twhen(data(ON_DEMAND), token(LToken.Dot)),\n" +
									"\t\t\twhen(data(ON_DEMAND), token(LToken.Times)),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(\n" +
									"\t\t\tnone(),\n" +
									"\t\t\tnone().withSpacingAfter(newLine()),\n" +
									"\t\t\tnone().withSpacingAfter(spacing(CompilationUnit_AfterImports))\n" +
									"\t);").build()
					),
					listOf(
							param("QualifiedName name").build(),
							param("boolean isStatic").build(),
							param("boolean isOnDemand").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("InitializerDecl"), "initializer declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("BlockStmt body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("InterfaceDecl"), "interface declaration",
					listOf(
							(QualifiedType) type("TypeDecl").build(),
							(QualifiedType) type("Documentable<InterfaceDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tkeyword(LToken.Interface),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_PARAMS, STypeParameter.listShape),\n" +
									"\t\t\tchild(EXTENDS_CLAUSE, org.jlato.internal.bu.type.SQualifiedType.extendsClauseShape),\n" +
									"\t\t\tchild(MEMBERS, SMemberDecl.bodyShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Name name").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("NodeList<QualifiedType> extendsClause").build(),
							param("NodeList<MemberDecl> members").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("LocalVariableDecl"), "local variable declaration",
					listOf(
							(QualifiedType) type("Decl").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.singleLineShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(VARIABLES, SVariableDeclarator.listShape).withSpacingBefore(space())\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("Type type").build(),
							param("NodeList<VariableDeclarator> variables").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("MethodDecl"), "method declaration",
					listOf(
							(QualifiedType) type("MemberDecl").build(),
							(QualifiedType) type("Documentable<MethodDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(MODIFIERS, SExtendedModifier.multiLineShape),\n" +
									"\t\t\tchild(TYPE_PARAMS, STypeParameter.listShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(NAME).withSpacingBefore(space()),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PARAMS, SFormalParameter.listShape),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight),\n" +
									"\t\t\tchild(DIMS, SArrayDim.listShape),\n" +
									"\t\t\tchild(THROWS_CLAUSE, org.jlato.internal.bu.type.SQualifiedType.throwsClauseShape),\n" +
									"\t\t\tchild(BODY, alternative(some(),\n" +
									"\t\t\t\t\telement().withSpacingBefore(space()),\n" +
									"\t\t\t\t\ttoken(LToken.SemiColon)\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<ExtendedModifier> modifiers").build(),
							param("NodeList<TypeParameter> typeParams").build(),
							param("Type type").build(),
							param("Name name").build(),
							param("NodeList<FormalParameter> params").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeList<QualifiedType> throwsClause").build(),
							param("NodeOption<BlockStmt> body").build()
					),
					listOf(
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
			new TreeClassDescriptor(name("decl"), name("Modifier"), "modifier",
					listOf(
							(QualifiedType) type("ExtendedModifier").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\tfinal ModifierKeyword keyword = ((SModifier) tree.state).keyword;\n" +
									"\t\t\tswitch (keyword) {\n" +
									"\t\t\t\tcase Public:\n" +
									"\t\t\t\t\treturn LToken.Public;\n" +
									"\t\t\t\tcase Protected:\n" +
									"\t\t\t\t\treturn LToken.Protected;\n" +
									"\t\t\t\tcase Private:\n" +
									"\t\t\t\t\treturn LToken.Private;\n" +
									"\t\t\t\tcase Abstract:\n" +
									"\t\t\t\t\treturn LToken.Abstract;\n" +
									"\t\t\t\tcase Default:\n" +
									"\t\t\t\t\treturn LToken.Default;\n" +
									"\t\t\t\tcase Static:\n" +
									"\t\t\t\t\treturn LToken.Static;\n" +
									"\t\t\t\tcase Final:\n" +
									"\t\t\t\t\treturn LToken.Final;\n" +
									"\t\t\t\tcase Transient:\n" +
									"\t\t\t\t\treturn LToken.Transient;\n" +
									"\t\t\t\tcase Volatile:\n" +
									"\t\t\t\t\treturn LToken.Volatile;\n" +
									"\t\t\t\tcase Synchronized:\n" +
									"\t\t\t\t\treturn LToken.Synchronized;\n" +
									"\t\t\t\tcase Native:\n" +
									"\t\t\t\t\treturn LToken.Native;\n" +
									"\t\t\t\tcase StrictFP:\n" +
									"\t\t\t\t\treturn LToken.StrictFP;\n" +
									"\t\t\t\tdefault:\n" +
									"\t\t\t\t\t// Can't happen by definition of enum\n" +
									"\t\t\t\t\tthrow new IllegalStateException();\n" +
									"\t\t\t}\n" +
									"\t\t}\n" +
									"\t});").build()
					),
					listOf(
							param("ModifierKeyword keyword").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("PackageDecl"), "package declaration",
					listOf(
							(QualifiedType) type("Node").build(),
							(QualifiedType) type("Documentable<PackageDecl>").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, list()),\n" +
									"\t\t\tkeyword(LToken.Package),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("QualifiedName name").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("TypeParameter"), "type parameter",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
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
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Name name").build(),
							param("NodeList<Type> bounds").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("VariableDeclarator"), "variable declarator",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
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
					listOf(
							param("VariableDeclaratorId id").build(),
							param("NodeOption<Expr> init").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("decl"), name("VariableDeclaratorId"), "variable declarator identifier",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(DIMS, list())\n" +
									"\t);").build()
					),
					listOf(
							param("Name name").build(),
							param("NodeList<ArrayDim> dims").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ArrayAccessExpr"), "array access expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.BracketLeft), child(INDEX), token(LToken.BracketRight)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr name").build(),
							param("Expr index").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ArrayCreationExpr"), "array creation expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
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
					listOf(
							param("Type type").build(),
							param("NodeList<ArrayDimExpr> dimExprs").build(),
							param("NodeList<ArrayDim> dims").build(),
							param("NodeOption<ArrayInitializerExpr> init").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ArrayDimExpr"), "array dimension expression",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, org.jlato.internal.bu.expr.SAnnotationExpr.singleLineAnnotationsShapeWithSpaceBefore),\n" +
									"\t\t\ttoken(LToken.BracketLeft), child(EXPR), token(LToken.BracketRight)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Expr expr").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ArrayInitializerExpr"), "array initializer expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\talternative(childIs(VALUES, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft).withSpacingAfter(space()),\n" +
									"\t\t\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(VALUES, SExpr.listShape),\n" +
									"\t\t\twhen(data(TRAILING_COMMA), token(LToken.Comma)),\n" +
									"\t\t\talternative(childIs(VALUES, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight).withSpacingBefore(space()),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<Expr> values").build(),
							param("boolean trailingComma").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("AssignExpr"), "assignment expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TARGET),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\t\t\tfinal AssignOp op = ((SAssignExpr) tree.state).op;\n" +
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
					listOf(
							param("Expr target").build(),
							param("AssignOp op").build(),
							param("Expr value").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("BinaryExpr"), "binary expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(LEFT),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\t\t\tfinal BinaryOp op = ((SBinaryExpr) tree.state).op;\n" +
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
					listOf(
							param("Expr left").build(),
							param("BinaryOp op").build(),
							param("Expr right").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("CastExpr"), "cast expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), child(TYPE), token(LToken.ParenthesisRight).withSpacingAfter(space()), child(EXPR)\n" +
									"\t);").build()
					),
					listOf(
							param("Type type").build(),
							param("Expr expr").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ClassExpr"), "'class' expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\ttoken(LToken.Dot), token(LToken.Class)\n" +
									"\t);").build()
					),
					listOf(
							param("Type type").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ConditionalExpr"), "conditional expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.QuestionMark).withSpacing(space(), space()),\n" +
									"\t\t\tchild(THEN_EXPR),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacing(space(), space()),\n" +
									"\t\t\tchild(ELSE_EXPR)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr condition").build(),
							param("Expr thenExpr").build(),
							param("Expr elseExpr").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("FieldAccessExpr"), "field access expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> scope").build(),
							param("Name name").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("InstanceOfExpr"), "'instanceof' expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR),\n" +
									"\t\t\tkeyword(LToken.InstanceOf),\n" +
									"\t\t\tchild(TYPE)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr expr").build(),
							param("Type type").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("LambdaExpr"), "lambda expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\twhen(data(PARENS), token(LToken.ParenthesisLeft)),\n" +
									"\t\t\tchild(PARAMS, SExpr.listShape),\n" +
									"\t\t\twhen(data(PARENS), token(LToken.ParenthesisRight)),\n" +
									"\t\t\ttoken(LToken.Arrow).withSpacing(space(), space()),\n" +
									"\t\t\tchild(BODY, leftOrRight())\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<FormalParameter> params").build(),
							param("boolean hasParens").build(),
							param("NodeEither<Expr, BlockStmt> body").build()
					),
					listOf(
							(Expr) null,
							(Expr) expr("true").build(),
							(Expr) expr("Trees.<Expr, BlockStmt>right(blockStmt())").build()
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("LiteralExpr"), "literal expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\tfinal String literalString = ((SLiteralExpr) tree.state).literalString;\n" +
									"\t\t\treturn new LToken(0, literalString); // TODO Fix\n" +
									"\t\t}\n" +
									"\t});").build()
					),
					listOf(
							param("Class<?> literalClass").build(),
							param("String literalString").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					true
			),
			new TreeClassDescriptor(name("expr"), name("MarkerAnnotationExpr"), "marker annotation expression",
					listOf(
							(QualifiedType) type("AnnotationExpr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME)\n" +
									"\t);").build()
					),
					listOf(
							param("QualifiedName name").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("MemberValuePair"), "annotation member value pair",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\ttoken(LToken.Assign).withSpacing(space(), space()),\n" +
									"\t\t\tchild(VALUE)\n" +
									"\t);").build()
					),
					listOf(
							param("Name name").build(),
							param("Expr value").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("MethodInvocationExpr"), "method invocation expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(TYPE_ARGS, org.jlato.internal.bu.type.SType.typeArgumentsShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(ARGS, SExpr.argumentsShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build(),
							param("NodeList<Expr> args").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("MethodReferenceExpr"), "method reference expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE),\n" +
									"\t\t\ttoken(LToken.DoubleColon),\n" +
									"\t\t\tchild(TYPE_ARGS, org.jlato.internal.bu.type.SType.typeArgumentsShape),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("Name name").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("NormalAnnotationExpr"), "normal annotation expression",
					listOf(
							(QualifiedType) type("AnnotationExpr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(PAIRS, list(token(LToken.Comma).withSpacingAfter(space()))),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					listOf(
							param("QualifiedName name").build(),
							param("NodeList<MemberValuePair> pairs").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ObjectCreationExpr"), "object creation expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.New).withSpacingAfter(space()),\n" +
									"\t\t\tchild(TYPE_ARGS, org.jlato.internal.bu.type.SType.typeArgumentsShape),\n" +
									"\t\t\tchild(TYPE),\n" +
									"\t\t\tchild(ARGS, SExpr.argumentsShape),\n" +
									"\t\t\tchild(BODY, when(some(), element(org.jlato.internal.bu.decl.SMemberDecl.bodyShape)))\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> scope").build(),
							param("NodeList<Type> typeArgs").build(),
							param("QualifiedType type").build(),
							param("NodeList<Expr> args").build(),
							param("NodeOption<NodeList<MemberDecl>> body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ParenthesizedExpr"), "parenthesized expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft), child(INNER), token(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr inner").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("SingleMemberAnnotationExpr"), "single member annotation expression",
					listOf(
							(QualifiedType) type("AnnotationExpr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.At), child(NAME),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft),\n" +
									"\t\t\tchild(MEMBER_VALUE),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t);").build()
					),
					listOf(
							param("QualifiedName name").build(),
							param("Expr memberValue").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("SuperExpr"), "'super' expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CLASS_EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.Super)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> classExpr").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("ThisExpr"), "'this' expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(CLASS_EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\ttoken(LToken.This)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> classExpr").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("TypeExpr"), "type expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = child(TYPE);").build()
					),
					listOf(
							param("Type type").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("UnaryExpr"), "unary expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape opShape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\tfinal UnaryOp op = ((SUnaryExpr) tree.state).op;\n" +
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
									"\t\tpublic boolean test(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\tfinal UnaryOp op = ((SUnaryExpr) tree.state).op;\n" +
									"\t\t\treturn op.isPrefix();\n" +
									"\t\t}\n" +
									"\t}, composite(opShape, child(EXPR)), composite(child(EXPR), opShape));").build()
					),
					listOf(
							param("UnaryOp op").build(),
							param("Expr expr").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("expr"), name("VariableDeclarationExpr"), "variable declaration expression",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = child(DECLARATION);").build()
					),
					listOf(
							param("LocalVariableDecl declaration").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("name"), name("Name"), "name",
					listOf(
							(QualifiedType) type("Expr").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(new LSToken.Provider() {\n" +
									"\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\treturn new LToken(TokenType.IDENTIFIER, ((SName) tree.state).id);\n" +
									"\t\t}\n" +
									"\t});").build()
					),
					listOf(
							param("String id").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("name"), name("QualifiedName"), "qualified name",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(QUALIFIER, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(NAME)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<QualifiedName> qualifier").build(),
							param("Name name").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("AssertStmt"), "'assert' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Assert),\n" +
									"\t\t\tchild(CHECK),\n" +
									"\t\t\tchild(MSG, when(some(),\n" +
									"\t\t\t\t\tcomposite(token(LToken.Colon).withSpacing(space(), space()), element())\n" +
									"\t\t\t)),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr check").build(),
							param("NodeOption<Expr> msg").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("BlockStmt"), "block statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t.withIndentationAfter(indent(Block)),\n" +
									"\t\t\tchild(STMTS, listShape),\n" +
									"\t\t\talternative(childIs(STMTS, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(Block))\n" +
									"\t\t\t\t\t\t\t.withSpacingBefore(newLine()),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(Block))\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<Stmt> stmts").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("BreakStmt"), "'break' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Break),\n" +
									"\t\t\tchild(ID, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Name> id").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("CatchClause"), "'catch' clause",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Catch),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(PARAM),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(CATCH_BLOCK)\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list();").build()
					),
					listOf(
							param("FormalParameter param").build(),
							param("BlockStmt catchBlock").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ContinueStmt"), "'continue' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Continue),\n" +
									"\t\t\tchild(ID, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Name> id").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("DoStmt"), "'do-while' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
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
					listOf(
							param("Stmt body").build(),
							param("Expr condition").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("EmptyStmt"), "empty statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(LToken.SemiColon);").build()
					),
					emptyList(),
					emptyList(),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ExplicitConstructorInvocationStmt"), "explicit constructor invocation statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR, when(some(), composite(element(), token(LToken.Dot)))),\n" +
									"\t\t\tchild(TYPE_ARGS, org.jlato.internal.bu.type.SType.typeArgumentsShape),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\t\t\treturn ((SExplicitConstructorInvocationStmt) tree.state).isThis ? LToken.This : LToken.Super;\n" +
									"\t\t\t\t}\n" +
									"\t\t\t}),\n" +
									"\t\t\tchild(ARGS, org.jlato.internal.bu.expr.SExpr.argumentsShape),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<Type> typeArgs").build(),
							param("boolean isThis").build(),
							param("NodeOption<Expr> expr").build(),
							param("NodeList<Expr> args").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ExpressionStmt"), "expression statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(EXPR), token(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr expr").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ForStmt"), "'for' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
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
					listOf(
							param("NodeList<Expr> init").build(),
							param("Expr compare").build(),
							param("NodeList<Expr> update").build(),
							param("Stmt body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ForeachStmt"), "\"enhanced\" 'for' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.For), token(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(VAR),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacing(space(), space()),\n" +
									"\t\t\tchild(ITERABLE),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					listOf(
							param("VariableDeclarationExpr var").build(),
							param("Expr iterable").build(),
							param("Stmt body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("IfStmt"), "'if' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
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
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine()),\n" +
									"\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ThenOtherStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(IfElse))\n" +
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
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(IfElse)),\n" +
									"\t\t\t\t\t\t\t\t\t\t\tdefaultShape()\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withSpacingBefore(spacing(IfStmt_ElseOtherStmt))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationBefore(indent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withIndentationAfter(unIndent(IfElse))\n" +
									"\t\t\t\t\t\t\t\t\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t\t\t\t\t)\n" +
									"\t\t\t\t\t\t\t)\n" +
									"\t\t\t\t\t))\n" +
									"\t\t\t)))\n" +
									"\t);").build()
					),
					listOf(
							param("Expr condition").build(),
							param("Stmt thenStmt").build(),
							param("NodeOption<Stmt> elseStmt").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("LabeledStmt"), "labeled statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tnone().withIndentationAfter(indent(Label)),\n" +
									"\t\t\tchild(LABEL),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacingAfter(spacing(LabeledStmt_AfterLabel)),\n" +
									"\t\t\tnone().withIndentationBefore(unIndent(Label)),\n" +
									"\t\t\tchild(STMT)\n" +
									"\t);").build()
					),
					listOf(
							param("Name label").build(),
							param("Stmt stmt").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ReturnStmt"), "'return' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Return),\n" +
									"\t\t\tchild(EXPR, when(some(), element().withSpacingBefore(space()))),\n" +
									"\t\t\ttoken(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeOption<Expr> expr").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("SwitchCase"), "'switch' case",
					listOf(
							(QualifiedType) type("Node").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(LABEL, alternative(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Case), element()),\n" +
									"\t\t\t\t\ttoken(LToken.Default)\n" +
									"\t\t\t)),\n" +
									"\t\t\ttoken(LToken.Colon).withSpacingAfter(newLine()),\n" +
									"\t\t\tnone().withIndentationAfter(indent(SwitchCase)),\n" +
									"\t\t\tchild(STMTS, SStmt.listShape),\n" +
									"\t\t\tnone().withIndentationBefore(unIndent(SwitchCase))\n" +
									"\t);").build(),
							memberDecl("public static final LexicalShape listShape = list(none().withSpacingAfter(newLine()));").build()
					),
					listOf(
							param("NodeOption<Expr> label").build(),
							param("NodeList<Stmt> stmts").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("SwitchStmt"), "'switch' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Switch),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(spacing(SwitchStmt_AfterSwitchKeyword)),\n" +
									"\t\t\tchild(SELECTOR),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\ttoken(LToken.BraceLeft)\n" +
									"\t\t\t\t\t.withSpacingAfter(newLine())\n" +
									"\t\t\t\t\t.withIndentationAfter(indent(Switch)),\n" +
									"\t\t\tchild(CASES, SSwitchCase.listShape),\n" +
									"\t\t\talternative(childIs(CASES, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(Switch))\n" +
									"\t\t\t\t\t\t\t.withSpacingBefore(newLine()),\n" +
									"\t\t\t\t\ttoken(LToken.BraceRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(Switch))\n" +
									"\t\t\t)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr selector").build(),
							param("NodeList<SwitchCase> cases").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("SynchronizedStmt"), "'synchronized' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\ttoken(LToken.Synchronized),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(EXPR),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BLOCK)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr expr").build(),
							param("BlockStmt block").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("ThrowStmt"), "'throw' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Throw), child(EXPR), token(LToken.SemiColon)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr expr").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("TryStmt"), "'try' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.Try),\n" +
									"\t\t\twhen(childIs(RESOURCES, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.ParenthesisLeft)\n" +
									"\t\t\t\t\t\t\t.withIndentationAfter(indent(TryResources))\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(RESOURCES, list(token(LToken.SemiColon).withSpacingAfter(newLine()))),\n" +
									"\t\t\twhen(childIs(RESOURCES, not(empty())), when(data(TRAILING_SEMI_COLON), token(LToken.SemiColon))),\n" +
									"\t\t\twhen(childIs(RESOURCES, not(empty())),\n" +
									"\t\t\t\t\ttoken(LToken.ParenthesisRight)\n" +
									"\t\t\t\t\t\t\t.withIndentationBefore(unIndent(TryResources))\n" +
									"\t\t\t\t\t\t\t.withSpacingAfter(space())\n" +
									"\t\t\t),\n" +
									"\t\t\tchild(TRY_BLOCK),\n" +
									"\t\t\tchild(CATCHS, SCatchClause.listShape),\n" +
									"\t\t\tchild(FINALLY_BLOCK, when(some(),\n" +
									"\t\t\t\t\tcomposite(keyword(LToken.Finally), element())\n" +
									"\t\t\t))\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<VariableDeclarationExpr> resources").build(),
							param("boolean trailingSemiColon").build(),
							param("BlockStmt tryBlock").build(),
							param("NodeList<CatchClause> catchs").build(),
							param("NodeOption<BlockStmt> finallyBlock").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("TypeDeclarationStmt"), "type declaration statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPE_DECL)\n" +
									"\t);").build()
					),
					listOf(
							param("TypeDecl typeDecl").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("stmt"), name("WhileStmt"), "'while' statement",
					listOf(
							(QualifiedType) type("Stmt").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tkeyword(LToken.While),\n" +
									"\t\t\ttoken(LToken.ParenthesisLeft).withSpacingBefore(space()),\n" +
									"\t\t\tchild(CONDITION),\n" +
									"\t\t\ttoken(LToken.ParenthesisRight).withSpacingAfter(space()),\n" +
									"\t\t\tchild(BODY)\n" +
									"\t);").build()
					),
					listOf(
							param("Expr condition").build(),
							param("Stmt body").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("ArrayType"), "array type",
					listOf(
							(QualifiedType) type("ReferenceType").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(COMPONENT_TYPE),\n" +
									"\t\t\tchild(DIMS, list())\n" +
									"\t);").build()
					),
					listOf(
							param("Type componentType").build(),
							param("NodeList<ArrayDim> dims").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("IntersectionType"), "intersection type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPES, org.jlato.internal.bu.type.SType.intersectionShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<Type> types").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("PrimitiveType"), "primitive type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, list()),\n" +
									"\t\t\ttoken(new LSToken.Provider() {\n" +
									"\t\t\t\tpublic LToken tokenFor(" + AllDescriptors.BU_TREE + " tree) {\n" +
									"\t\t\t\t\tfinal Primitive primitive = ((SPrimitiveType) tree.state).primitive;\n" +
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
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("Primitive primitive").build()
					),
					listOf(
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("QualifiedType"), "qualified type",
					listOf(
							(QualifiedType) type("ReferenceType").build()
					),
					listOf(
							memberDecl("public static final LexicalShape scopeShape = composite(element(), token(LToken.Dot));").build(),
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(SCOPE, when(some(), scopeShape)),\n" +
									"\t\t\tchild(ANNOTATIONS, org.jlato.internal.bu.expr.SAnnotationExpr.singleLineAnnotationsShape),\n" +
									"\t\t\tchild(NAME),\n" +
									"\t\t\tchild(TYPE_ARGS, when(some(), element(org.jlato.internal.bu.type.SType.typeArgumentsOrDiamondShape)))\n" +
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
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<QualifiedType> scope").build(),
							param("Name name").build(),
							param("NodeOption<NodeList<Type>> typeArgs").build()
					),
					listOf(
							(Expr) null,
							(Expr) null,
							(Expr) null,
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("UnionType"), "union type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(TYPES, org.jlato.internal.bu.type.SType.unionShape)\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<Type> types").build()
					),
					listOf(
							(Expr) null
					),
					false
			),
			new TreeClassDescriptor(name("type"), name("UnknownType"), "unknown type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = none();").build()
					),
					emptyList(),
					emptyList(),
					false
			),
			new TreeClassDescriptor(name("type"), name("VoidType"), "void type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = token(LToken.Void);").build()
					),
					emptyList(),
					emptyList(),
					false
			),
			new TreeClassDescriptor(name("type"), name("WildcardType"), "wildcard type",
					listOf(
							(QualifiedType) type("Type").build()
					),
					listOf(
							memberDecl("public static final LexicalShape shape = composite(\n" +
									"\t\t\tchild(ANNOTATIONS, org.jlato.internal.bu.expr.SAnnotationExpr.singleLineAnnotationsShape),\n" +
									"\t\t\ttoken(LToken.QuestionMark),\n" +
									"\t\t\tchild(EXT, when(some(), composite(keyword(LToken.Extends), element()))),\n" +
									"\t\t\tchild(SUP, when(some(), composite(keyword(LToken.Super), element())))\n" +
									"\t);").build()
					),
					listOf(
							param("NodeList<AnnotationExpr> annotations").build(),
							param("NodeOption<ReferenceType> ext").build(),
							param("NodeOption<ReferenceType> sup").build()
					),
					listOf(
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
