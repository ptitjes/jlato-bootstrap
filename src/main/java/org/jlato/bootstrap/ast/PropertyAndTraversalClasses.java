package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.ArrayList;
import java.util.List;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class PropertyAndTraversalClasses extends Utils implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();

		// Preprocess params to find properties and traversals
		NodeList<FormalParameter> propertyParams = emptyList();
		NodeList<FormalParameter> traversalParams = emptyList();
		for (FormalParameter param : arg.parameters) {
			if (propertyFieldType(param.type()))
				propertyParams = propertyParams.append(param);
			else
				traversalParams = traversalParams.append(param);
		}

		int index = 0;
		for (FormalParameter parameter : traversalParams) {
			FormalParameter before = index == 0 ? null : traversalParams.get(index - 1);
			FormalParameter after = index == traversalParams.size() - 1 ? null : traversalParams.get(index + 1);

			decls.add(new TraversalClass(parameter, before, after));
			index++;
		}

		for (FormalParameter parameter : propertyParams) {
			decls.add(new PropertyClass(parameter));
		}

		return decls;
	}

	public static class TraversalClass extends MemberPattern.OfField<TreeClassDescriptor> {

		private final FormalParameter param;
		private final FormalParameter before;
		private final FormalParameter after;

		public TraversalClass(FormalParameter param, FormalParameter before, FormalParameter after) {
			this.param = param;
			this.before = before;
			this.after = after;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public static STypeSafeTraversal<..$_> " + constantName(param) + " = $_;";
		}

		@Override
		protected FieldDecl makeDecl(FieldDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.S_TREE_QUALIFIED);
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STraversal"));
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STypeSafeTraversal"));
			AllDescriptors.addImports(importManager, param.type());

			Type treeType = param.type();
			String traversalName = param.id().get().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = name("state");

			final Name childParamName = name("child");

			final FormalParameter typedStateParam = formalParameter(stateType).withId(variableDeclaratorId(stateParamName));
			final FormalParameter stateParam = formalParameter(qualifiedType(AllDescriptors.S_TREE))
					.withId(variableDeclaratorId(stateParamName));

			QualifiedType childStateType = treeTypeToStateType((QualifiedType) treeType);
			final Type childType = qualifiedType(AllDescriptors.BU_TREE)
					.withTypeArgs(listOf(childStateType));
			final Type childReturnType = qualifiedType(AllDescriptors.BU_TREE)
					.withTypeArgs(listOf(wildcardType()));
			final FormalParameter childParam = formalParameter(childType).withId(variableDeclaratorId(childParamName));

			MethodDecl traverseMethod = methodDecl(childReturnType, name("doTraverse"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(typedStateParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(
									fieldAccessExpr(name(traversalName)).withScope(stateParamName)
							)
					)));

			MethodDecl rebuildMethod = methodDecl(stateType, name("doRebuildParentState"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(typedStateParam, childParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(
									methodInvocationExpr(name(propertySetterName(traversalName, treeType)))
											.withScope(stateParamName)
											.withArgs(listOf(childParamName))
							)
					)));

			MethodDecl leftSibling = methodDecl(qType("STraversal"), name("left" + "Sibling"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(stateParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(before == null ? nullLiteralExpr() : name(constantName(before)))
					)));

			MethodDecl rightSibling = methodDecl(qType("STraversal"), name("right" + "Sibling"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(stateParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(after == null ? nullLiteralExpr() : name(constantName(after)))
					)));

			QualifiedType traversalType = qType("STypeSafeTraversal", stateType, childStateType, treeType);
			FieldDecl traversal = fieldDecl(traversalType)
					.withModifiers(listOf(Modifier.Public, Modifier.Static))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(constantName)))
									.withInit(
											objectCreationExpr(traversalType)
													.withBody(listOf(traverseMethod, rebuildMethod, leftSibling, rightSibling))
									)
					));
			return traversal;
		}

		@Override
		protected String makeDoc(FieldDecl decl, TreeClassDescriptor arg) {
			return null;
		}
	}

	public static class PropertyClass extends MemberPattern.OfField<TreeClassDescriptor> {

		private final FormalParameter param;

		public PropertyClass(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public static STypeSafeProperty<..$_> " + constantName(param) + " = $_;";
		}

		@Override
		protected FieldDecl makeDecl(FieldDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.SProperty"));
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STypeSafeProperty"));

			Type treeType = param.type();
			String traversalName = param.id().get().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = name("state");

			final Name childParamName = name("child");

			final FormalParameter typedStateParam = formalParameter(stateType).withId(variableDeclaratorId(stateParamName));
			final FormalParameter stateParam = formalParameter(qualifiedType(AllDescriptors.S_TREE))
					.withId(variableDeclaratorId(stateParamName));

			final Name valueParamName = name("value");
			final Type valueType = boxedType(param.type());
			final FormalParameter valueParam = formalParameter(valueType).withId(variableDeclaratorId(valueParamName));

			MethodDecl retrieveMethod = methodDecl(valueType, name("doRetrieve"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(typedStateParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(
									fieldAccessExpr(param.id().get().name()).withScope(stateParamName)
							)
					)));

			MethodDecl rebuildMethod = methodDecl(stateType, name("doRebuildParentState"))
					.withModifiers(listOf(overrideAnn(), Modifier.Public))
					.withParams(listOf(typedStateParam, valueParam))
					.withBody(blockStmt().withStmts(listOf(
							returnStmt().withExpr(
									methodInvocationExpr(name(propertySetterName(param)))
											.withScope(stateParamName)
											.withArgs(listOf(valueParamName))
							)
					)));

			QualifiedType propertyType = qType("STypeSafeProperty", stateType, valueType);
			FieldDecl property = fieldDecl(propertyType)
					.withModifiers(listOf(Modifier.Public, Modifier.Static))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(constantName)))
									.withInit(
											objectCreationExpr(propertyType)
													.withBody(listOf(retrieveMethod, rebuildMethod))

									)
					));
			return property;
		}

		@Override
		protected String makeDoc(FieldDecl decl, TreeClassDescriptor arg) {
			return null;
		}
	}
}
