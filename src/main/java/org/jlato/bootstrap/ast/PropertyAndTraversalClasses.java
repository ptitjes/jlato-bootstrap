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

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class PropertyAndTraversalClasses extends Utils implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();

		// Preprocess params to find properties and traversals
		NodeList<FormalParameter> propertyParams = NodeList.empty();
		NodeList<FormalParameter> traversalParams = NodeList.empty();
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
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STreeState"));
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STraversal"));
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STypeSafeTraversal"));
			AllDescriptors.addImports(importManager, param.type());

			Type treeType = param.type();
			String traversalName = param.id().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = new Name("state");

			final Name childParamName = new Name("child");

			final FormalParameter typedStateParam = formalParameter(stateType, variableDeclaratorId(stateParamName));
			final FormalParameter stateParam = formalParameter(
					qualifiedType(TreeClassDescriptor.STREE_STATE_NAME),
					variableDeclaratorId(stateParamName));

			QualifiedType childStateType = treeTypeToStateType((QualifiedType) treeType);
			final Type childType = qType("STree", childStateType);
			final Type childReturnType = qType("STree", wildcardType());
			final FormalParameter childParam = formalParameter(childType, variableDeclaratorId(childParamName));

			MethodDecl traverseMethod = methodDecl(childReturnType, new Name("doTraverse"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(typedStateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									fieldAccessExpr(new Name(traversalName)).withScope(some(stateParamName))
							))
					))));

			MethodDecl rebuildMethod = methodDecl(stateType, new Name("doRebuildParentState"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(typedStateParam, childParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									methodInvocationExpr(new Name(propertySetterName(traversalName, treeType)))
											.withScope(some(stateParamName))
											.withArgs(NodeList.of(childParamName))
							))
					))));

			MethodDecl leftSibling = methodDecl(qType("STraversal"), new Name("left" + "Sibling"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(stateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(before == null ? nullLiteralExpr() : new Name(constantName(before))))
					))));

			MethodDecl rightSibling = methodDecl(qType("STraversal"), new Name("right" + "Sibling"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(stateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(after == null ? nullLiteralExpr() : new Name(constantName(after))))
					))));

			QualifiedType traversalType = qType("STypeSafeTraversal", stateType, childStateType, treeType);
			FieldDecl traversal = fieldDecl(traversalType)
					.withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
					.withVariables(NodeList.of(
							variableDeclarator(variableDeclaratorId(new Name(constantName)))
									.withInit(some(
											objectCreationExpr(traversalType)
													.withBody(some(NodeList.of(traverseMethod, rebuildMethod, leftSibling, rightSibling)))
									))
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
			String traversalName = param.id().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = new Name("state");

			final Name childParamName = new Name("child");

			final FormalParameter typedStateParam = formalParameter(stateType, variableDeclaratorId(stateParamName));
			final FormalParameter stateParam = formalParameter(
					qualifiedType(TreeClassDescriptor.STREE_STATE_NAME),
					variableDeclaratorId(stateParamName));

			final Name valueParamName = new Name("value");
			final Type valueType = boxedType(param.type());
			final FormalParameter valueParam = formalParameter(valueType, variableDeclaratorId(valueParamName));

			MethodDecl retrieveMethod = methodDecl(valueType, new Name("doRetrieve"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(typedStateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									fieldAccessExpr(param.id().name()).withScope(some(stateParamName))
							))
					))));

			MethodDecl rebuildMethod = methodDecl(stateType, new Name("doRebuildParentState"))
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withParams(NodeList.of(typedStateParam, valueParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									methodInvocationExpr(new Name(propertySetterName(param)))
											.withScope(some(stateParamName))
											.withArgs(NodeList.of(valueParamName))
							))
					))));

			QualifiedType propertyType = qType("STypeSafeProperty", stateType, valueType);
			FieldDecl property = fieldDecl(propertyType)
					.withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
					.withVariables(NodeList.of(
							variableDeclarator(variableDeclaratorId(new Name(constantName)))
									.withInit(some(
											objectCreationExpr(propertyType)
													.withBody(some(NodeList.of(retrieveMethod, rebuildMethod)))

									))
					));
			return property;
		}

		@Override
		protected String makeDoc(FieldDecl decl, TreeClassDescriptor arg) {
			return null;
		}
	}
}
