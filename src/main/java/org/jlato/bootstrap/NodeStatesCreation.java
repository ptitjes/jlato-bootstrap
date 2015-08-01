package org.jlato.bootstrap;

import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class NodeStatesCreation extends TreeClassRefactoring {

	public static final Name STATE_NAME = new Name("State");
	public static final Name STREE_STATE_NAME = new Name("STreeState");

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public InterfaceDecl refactorTreeInterface(TreeSet<CompilationUnit> treeSet, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) {
		Name name = decl.name();
		NodeList<Name> parentInterfaces = hierarchy.getParentInterfaces(name);
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && hierarchy.isTreeInterface(parentInterfaces.get(0));

		NodeList<MemberDecl> newMembers = NodeList.empty();
		newMembers = newMembers.append(
				interfaceDecl().withName(STATE_NAME)
						.withExtendsClause(
								treeInterfaceChild ? NodeList.of(qualifiedType().withName(STREE_STATE_NAME)) :
										parentInterfaces.map(n ->
												qualifiedType()
														.withScope(some(qualifiedType().withName(n)))
														.withName(STATE_NAME))
						)
		);

		NodeList<MemberDecl> members = newMembers;
		return decl.withMembers(ms -> insertBeforeShapes(ms, members));
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {
		Name treeName = decl.name();

		final NodeList<FormalParameter> treeParams = collectConstructorParams(decl);

		final QualifiedType stateType = qualifiedType().withScope(some(qualifiedType().withName(decl.name()))).withName(STATE_NAME);
		final NodeList<FormalParameter> stateParams = deriveStateParams(treeParams);

//		final MethodDecl streeFactoryMethod = buildMakeFactoryMethod(stateType, stateParams);
//		decl = decl.withMembers(ms -> ms.insert(0, streeFactoryMethod));

//		final ConstructorDecl treeConstructor = buildTreeConstructor(treeName, stateType, treeParams);
//		decl = decl.withMembers(ms -> ms.insert(0, treeConstructor));

		// Preprocess params to find properties and traversals
		NodeList<FormalParameter> propertyParams = NodeList.empty();
		NodeList<FormalParameter> traversalParams = NodeList.empty();
		for (FormalParameter treeParam : treeParams) {
			if (propertyFieldType(treeParam.type()))
				propertyParams = propertyParams.append(treeParam);
			else
				traversalParams = traversalParams.append(treeParam);
		}

		NodeList<MemberDecl> additionalMembers = NodeList.empty();

		additionalMembers = additionalMembers.append(
				buildStateClass(treeName, stateType, stateParams, traversalParams, propertyParams, hierarchy));

		additionalMembers = additionalMembers.appendAll(buildTraversals(stateType, traversalParams, hierarchy));

		additionalMembers = additionalMembers.appendAll(buildProperties(stateType, propertyParams));

		return decl.withMembers(insertBeforeShapes(decl.members(), additionalMembers));
	}

	private ConstructorDecl buildTreeConstructor(Name name, QualifiedType stateType, NodeList<FormalParameter> treeConstructorParams) {
		// Make SLocation creation expression from Trees
		final ObjectCreationExpr sLocationCreationExpr = objectCreationExpr()
				.withType(qType("SLocation", stateType))
				.withArgs(NodeList.of(
						methodInvocationExpr()
								.withName(new Name("make"))
								.withArgs(treeConstructorParams.map(p -> {
									Type treeType = p.type();
									if (propertyFieldType(treeType)) return p.id().name();
									else return methodInvocationExpr()
											.withScope(some(new Name("TreeBase")))
											.withTypeArgs(NodeList.of(treeTypeToStateType((QualifiedType) treeType)))
											.withName(new Name("nodeOf"))
											.withArgs(NodeList.of(p.id().name()));
								}))
				));

		return constructorDecl().withName(name)
				.withParams(treeConstructorParams)
				.withBody(blockStmt().withStmts(NodeList.of(
						explicitConstructorInvocationStmt()
								.setThis(false)
								.withArgs(NodeList.of(sLocationCreationExpr))
				)));
	}

	private MethodDecl buildMakeFactoryMethod(QualifiedType stateType, NodeList<FormalParameter> stateParams) {
		// Make STree creation expression from STrees
		final ObjectCreationExpr sTreeCreationExpr = objectCreationExpr()
				.withType(qType("STree", stateType))
				.withArgs(NodeList.of(
						objectCreationExpr().withType(stateType)
								.withArgs(stateParams.map(p -> {
									Type treeType = p.type();
									if (propertyFieldType(treeType)) return p.id().name();
									else return methodInvocationExpr()
											.withScope(some(new Name("TreeBase")))
											.withTypeArgs(((QualifiedType) treeType).typeArgs().get())
											.withName(new Name("nodeOf"))
											.withArgs(NodeList.of(p.id().name()));
								}))
				));

		// Add STree factory method
		return methodDecl().withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
				.withType(qType("STree", stateType))
				.withName(new Name("make"))
				.withParams(stateParams)
				.withBody(some(blockStmt().withStmts(NodeList.<Stmt>of(
						returnStmt().withExpr(some(sTreeCreationExpr))
				))));
	}

	private ClassDecl buildStateClass(Name treeName, QualifiedType stateType, NodeList<FormalParameter> stateParams,
	                                  NodeList<FormalParameter> traversalParams, NodeList<FormalParameter> propertyParams,
	                                  TreeTypeHierarchy hierarchy) {

		NodeList<MemberDecl> stateMembers = NodeList.empty();

		// Build state fields
		stateMembers = stateMembers.appendAll(stateParams.map(p ->
				fieldDecl()
						.withModifiers(NodeList.of(Modifier.Public, Modifier.Final))
						.withType(p.type())
						.withVariables(vs -> vs.append(variableDeclarator().withId(p.id())))));

		// Add constructor
		stateMembers = stateMembers.append(
				constructorDecl().withName(STATE_NAME)
						.withParams(stateParams)
						.withBody(blockStmt().withStmts(
								stateParams.map(p -> expressionStmt().withExpr(
										assignExpr().withTarget(fieldAccessExpr().withScope(some(thisExpr())).withName(p.id().name()))
												.withOp(AssignExpr.AssignOp.Normal).withValue(p.id().name())
								))
						))
		);

		// Make state creation expression from STrees
		final ObjectCreationExpr stateCreationExpr = objectCreationExpr().withType(stateType)
				.withArgs(stateParams.map(p -> p.id().name()));

		// Build state mutators
		stateMembers = stateMembers.appendAll(stateParams.map(p ->
						methodDecl()
								.withModifiers(NodeList.of(Modifier.Public))
								.withType(stateType)
								.withName(new Name(propertySetterName(p.id().name().id(), p.type())))
								.withParams(NodeList.of(p))
								.withBody(some(blockStmt().withStmts(NodeList.<Stmt>of(
										returnStmt().withExpr(some(stateCreationExpr))
								))))
		));

		stateMembers = stateMembers.append(
				methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("Kind"))
						.withName(new Name("kind"))
						.withBody(some(blockStmt().withStmts(
								NodeList.of(returnStmt().withExpr(some(
										fieldAccessExpr().withScope(some(new Name("Kind"))).withName(treeName)
								)))
						)))
		);

		stateMembers = stateMembers.append(
				methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Protected))
						.withType(qType("Tree"))
						.withName(new Name("doInstantiate"))
						.withParams(NodeList.of(
								formalParameter()
										.withId(variableDeclaratorId().withName(new Name("location")))
										.withType(qType("SLocation", stateType))
						))
						.withBody(some(blockStmt().withStmts(
								NodeList.of(returnStmt().withExpr(some(
										objectCreationExpr()
												.withType(qualifiedType().withName(treeName))
												.withArgs(NodeList.of(new Name("location")))
								)))
						)))
		);

		stateMembers = stateMembers.append(
				methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("LexicalShape"))
						.withName(new Name("shape"))
						.withBody(some(blockStmt().withStmts(
								NodeList.of(returnStmt().withExpr(some(
										new Name("shape")
								)))
						)))
		);

		FormalParameter first = traversalParams.isEmpty() ? null : traversalParams.get(0);
		FormalParameter last = traversalParams.isEmpty() ? null : traversalParams.get(traversalParams.size() - 1);
		Expr firstExpr = first == null ? LiteralExpr.nullLiteral() : new Name(constantName(first.id().name().id(), first.type()));
		Expr lastExpr = last == null ? LiteralExpr.nullLiteral() : new Name(constantName(last.id().name().id(), last.type()));

		stateMembers = stateMembers.append(
				methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("STraversal"))
						.withName(new Name("firstChild"))
						.withBody(some(blockStmt().withStmts(
								NodeList.of(returnStmt().withExpr(some(firstExpr)))
						)))
		);

		stateMembers = stateMembers.append(
				methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("STraversal"))
						.withName(new Name("lastChild"))
						.withBody(some(blockStmt().withStmts(
								NodeList.of(returnStmt().withExpr(some(lastExpr)))
						)))
		);

		NodeList<Name> parentInterfaces = hierarchy.getParentInterfaces(treeName);
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && hierarchy.isTreeInterface(parentInterfaces.get(0));
		NodeList<QualifiedType> implementsClause = parentInterfaces.isEmpty() || treeInterfaceChild ?
				NodeList.of(qualifiedType().withName(STREE_STATE_NAME)) :
				parentInterfaces.map(n -> qualifiedType().withScope(some(qualifiedType().withName(n))).withName(STATE_NAME));

		// Build state class
		return classDecl().withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
				.withName(STATE_NAME)
				.withExtendsClause(some(
						qualifiedType().withName(new Name("SNodeState"))
								.withTypeArgs(some(NodeList.<Type>of(qualifiedType().withName(STATE_NAME))))
				))
				.withImplementsClause(implementsClause)
				.withMembers(stateMembers);
	}

	private NodeList<MemberDecl> buildTraversals(QualifiedType stateType, NodeList<FormalParameter> traversalParams, TreeTypeHierarchy hierarchy) {
		NodeList<MemberDecl> traversals = NodeList.empty();
		if (!traversalParams.isEmpty()) {
			final Name stateParamName = new Name("state");
			final FormalParameter typedStateParam = formalParameter()
					.withType(stateType)
					.withId(variableDeclaratorId().withName(stateParamName));
			final FormalParameter stateParam = formalParameter()
					.withType(qType("STreeState"))
					.withId(variableDeclaratorId().withName(stateParamName));

			int index = 0;
			for (FormalParameter param : traversalParams) {
				Type treeType = param.type();

				final Name childParamName = new Name("child");
				QualifiedType childStateType = treeTypeToStateType((QualifiedType) treeType);
				final Type childType = qType("STree", childStateType);
				final Type childReturnType = qType("STree", wildcardType());
				final FormalParameter childParam = formalParameter()
						.withType(childType)
						.withId(variableDeclaratorId().withName(childParamName));

				String traversalName = param.id().name().id();
				String constantName = constantName(traversalName, treeType);

				FormalParameter before = index == 0 ? null : traversalParams.get(index - 1);
				String nameBefore = before == null ? null : constantName(before.id().name().id(), before.type());

				FormalParameter after = index == traversalParams.size() - 1 ? null : traversalParams.get(index + 1);
				String nameAfter = after == null ? null : constantName(after.id().name().id(), after.type());

				MethodDecl traverseMethod = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Protected))
						.withType(childReturnType)
						.withName(new Name("doTraverse"))
						.withParams(NodeList.of(typedStateParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										fieldAccessExpr().withScope(some(stateParamName)).withName(new Name(traversalName))
								))
						))));

				MethodDecl rebuildMethod = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Protected))
						.withType(stateType)
						.withName(new Name("doRebuildParentState"))
						.withParams(NodeList.of(typedStateParam, childParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										methodInvocationExpr()
												.withScope(some(stateParamName))
												.withName(new Name(propertySetterName(traversalName, treeType)))
												.withArgs(NodeList.of(childParamName))
								))
						))));

				MethodDecl leftSibling = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("STraversal"))
						.withName(new Name("left" + "Sibling"))
						.withParams(NodeList.of(stateParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(new Name(nameBefore)))
						))));

				MethodDecl rightSibling = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
						.withType(qType("STraversal"))
						.withName(new Name("right" + "Sibling"))
						.withParams(NodeList.of(stateParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(new Name(nameAfter)))
						))));

				QualifiedType traversalType = qType("STypeSafeTraversal", stateType, childStateType, treeType);
				FieldDecl traversal = fieldDecl()
						.withModifiers(NodeList.of(Modifier.Private, Modifier.Static))
						.withType(traversalType)
						.withVariables(NodeList.of(
								variableDeclarator()
										.withId(variableDeclaratorId().withName(new Name(constantName)))
										.withInit(some(
												objectCreationExpr()
														.withType(traversalType)
														.withBody(some(NodeList.of(traverseMethod, rebuildMethod, leftSibling, rightSibling)))

										))
						));

				traversals = traversals.append(traversal);
				index++;
			}
		}
		return traversals;
	}

	private NodeList<MemberDecl> buildProperties(QualifiedType stateType, NodeList<FormalParameter> propertyParams) {
		NodeList<MemberDecl> properties = NodeList.empty();
		if (!propertyParams.isEmpty()) {
			final Name stateParamName = new Name("state");
			final FormalParameter typedStateParam = formalParameter()
					.withType(stateType)
					.withId(variableDeclaratorId().withName(stateParamName));

			for (FormalParameter param : propertyParams) {

				final Name valueParamName = new Name("value");
				final Type valueType = boxedType(param.type());
				final FormalParameter valueParam = formalParameter()
						.withType(valueType)
						.withId(variableDeclaratorId().withName(valueParamName));

				String propertyName = param.id().name().id();
				String constantName = constantName(propertyName, param.type());

				MethodDecl retrieveMethod = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Protected))
						.withType(valueType)
						.withName(new Name("doRetrieve"))
						.withParams(NodeList.of(typedStateParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										fieldAccessExpr().withScope(some(stateParamName)).withName(new Name(propertyName))
								))
						))));

				MethodDecl rebuildMethod = methodDecl()
						.withModifiers(NodeList.of(overrideAnn(), Modifier.Protected))
						.withType(stateType)
						.withName(new Name("doRebuildParentState"))
						.withParams(NodeList.of(typedStateParam, valueParam))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										methodInvocationExpr()
												.withScope(some(stateParamName))
												.withName(new Name(propertySetterName(propertyName, param.type())))
												.withArgs(NodeList.of(valueParamName))
								))
						))));

				QualifiedType propertyType = qType("STypeSafeProperty", stateType, valueType);
				FieldDecl traversal = fieldDecl()
						.withModifiers(NodeList.of(Modifier.Private, Modifier.Static))
						.withType(propertyType)
						.withVariables(NodeList.of(
								variableDeclarator()
										.withId(variableDeclaratorId().withName(new Name(constantName)))
										.withInit(some(
												objectCreationExpr()
														.withType(propertyType)
														.withBody(some(NodeList.of(retrieveMethod, rebuildMethod)))

										))
						));

				properties = properties.append(traversal);
			}
		}
		return properties;
	}

	@Override
	public NodeList<ImportDecl> addImports(NodeList<ImportDecl> imports) {
		return imports.append(importDecl().withName(QualifiedName.of("org.jlato.internal.bu")).setOnDemand(true))
				.append(importDecl().withName(QualifiedName.of("org.jlato.internal.td")).setOnDemand(true));
	}
}
