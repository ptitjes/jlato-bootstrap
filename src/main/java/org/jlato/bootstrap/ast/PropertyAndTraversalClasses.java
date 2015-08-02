package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.ArrayList;
import java.util.List;

import static org.jlato.rewrite.Quotes.typeDecl;
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
			return "private static STypeSafeTraversal<..$_> " + constantName(param) + " = $_;";
		}

		@Override
		protected FieldDecl makeDecl(FieldDecl decl, TreeClassDescriptor arg) {
			Type treeType = param.type();
			String traversalName = param.id().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = new Name("state");

			final Name childParamName = new Name("child");

			final FormalParameter typedStateParam = formalParameter()
					.withType(stateType)
					.withId(variableDeclaratorId().withName(stateParamName));
			final FormalParameter stateParam = formalParameter()
					.withType(qualifiedType().withName(TreeClassDescriptor.STREE_STATE_NAME))
					.withId(variableDeclaratorId().withName(stateParamName));

			QualifiedType childStateType = treeTypeToStateType((QualifiedType) treeType);
			final Type childType = qType("STree", childStateType);
			final Type childReturnType = qType("STree", wildcardType());
			final FormalParameter childParam = formalParameter()
					.withType(childType)
					.withId(variableDeclaratorId().withName(childParamName));

			MethodDecl traverseMethod = methodDecl()
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withType(childReturnType)
					.withName(new Name("doTraverse"))
					.withParams(NodeList.of(typedStateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									fieldAccessExpr().withScope(some(stateParamName)).withName(new Name(traversalName))
							))
					))));

			MethodDecl rebuildMethod = methodDecl()
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
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
							returnStmt().withExpr(some(before == null ? LiteralExpr.nullLiteral() : new Name(constantName(before))))
					))));

			MethodDecl rightSibling = methodDecl()
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withType(qType("STraversal"))
					.withName(new Name("right" + "Sibling"))
					.withParams(NodeList.of(stateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(after == null ? LiteralExpr.nullLiteral() : new Name(constantName(after))))
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
			return "private static STypeSafeProperty<..$_> " + constantName(param) + " = $_;";
		}

		@Override
		protected FieldDecl makeDecl(FieldDecl decl, TreeClassDescriptor arg) {
			Type treeType = param.type();
			String traversalName = param.id().name().id();
			String constantName = constantName(traversalName, treeType);

			final QualifiedType stateType = arg.stateType();
			final Name stateParamName = new Name("state");

			final Name childParamName = new Name("child");

			final FormalParameter typedStateParam = formalParameter()
					.withType(stateType)
					.withId(variableDeclaratorId().withName(stateParamName));
			final FormalParameter stateParam = formalParameter()
					.withType(qualifiedType().withName(TreeClassDescriptor.STREE_STATE_NAME))
					.withId(variableDeclaratorId().withName(stateParamName));

			final Name valueParamName = new Name("value");
			final Type valueType = boxedType(param.type());
			final FormalParameter valueParam = formalParameter()
					.withType(valueType)
					.withId(variableDeclaratorId().withName(valueParamName));

			MethodDecl retrieveMethod = methodDecl()
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withType(valueType)
					.withName(new Name("doRetrieve"))
					.withParams(NodeList.of(typedStateParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									fieldAccessExpr().withScope(some(stateParamName)).withName(param.id().name())
							))
					))));

			MethodDecl rebuildMethod = methodDecl()
					.withModifiers(NodeList.of(overrideAnn(), Modifier.Public))
					.withType(stateType)
					.withName(new Name("doRebuildParentState"))
					.withParams(NodeList.of(typedStateParam, valueParam))
					.withBody(some(blockStmt().withStmts(NodeList.of(
							returnStmt().withExpr(some(
									methodInvocationExpr()
											.withScope(some(stateParamName))
											.withName(new Name(propertySetterName(param)))
											.withArgs(NodeList.of(valueParamName))
							))
					))));

			QualifiedType propertyType = qType("STypeSafeProperty", stateType, valueType);
			FieldDecl property = fieldDecl()
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
			return property;
		}

		@Override
		protected String makeDoc(FieldDecl decl, TreeClassDescriptor arg) {
			return null;
		}
	}
}
