package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.*;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class TreeConstruction implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		return Arrays.asList(
				new TreePrivateConstructor(),
				new TreeMakeMethod(),
				new TreePublicConstructor()
		);
	}

	public static class TreePrivateConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("private " + arg.name + "(SLocation<" + arg.name + ".State> location) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qType("SLocation", stateType);

			final Name location = name("location");

			decl = constructorDecl(name,
					blockStmt().withStmts(NodeList.of(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(NodeList.of(location))
					)))
					.withModifiers(NodeList.of(Modifier.Private))
					.withParams(NodeList.of(
							formalParameter(locationType, variableDeclaratorId(location))
					));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " for the specified tree location.",
								new String[]{"the tree location."}
						)
				);

			return decl;
		}
	}

	public static class TreeMakeMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public static STree<" + arg.name + ".State> make(..$_) { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> parameters = arg.parameters;
			final NodeList<FormalParameter> stateParams = arg.stateParameters();
			final QualifiedType stateType = arg.stateType();
			final QualifiedType treeType = qType("STree", stateType);

			// Make STree creation expression from STrees
			final ObjectCreationExpr sTreeCreationExpr = objectCreationExpr(treeType)
					.withArgs(NodeList.of(
							objectCreationExpr(stateType).withArgs(parameters.map(p -> p.id().name()))
					));

			// Add STree factory method
			decl = methodDecl(treeType, name("make"))
					.withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
					.withParams(stateParams)
					.withBody(some(blockStmt().withStmts(NodeList.<Stmt>of(
							returnStmt().withExpr(some(sTreeCreationExpr))
					))));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Compares this state object to the specified object.",
								new String[]{"the object to compare this state with."},
								"<code>true</code> if the specified object is equal to this state, <code>false</code> otherwise."
						)
				);

			return decl;
		}
	}

	public static class TreePublicConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.name + "(..$_) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qType("SLocation", stateType);
			final NodeList<FormalParameter> parameters = arg.parameters;

			// Make SLocation creation expression from Trees
			final ObjectCreationExpr sLocationCreationExpr = objectCreationExpr(locationType)
					.withArgs(NodeList.of(
							methodInvocationExpr(name("make"))
									.withArgs(parameters.map(p -> {
										Type treeType = p.type();
										if (propertyFieldType(treeType)) return p.id().name();
										else return methodInvocationExpr(name("treeOf"))
												.withScope(some(name("TreeBase")))
												.withTypeArgs(NodeList.of(treeTypeToStateType((QualifiedType) treeType)))
												.withArgs(NodeList.of(p.id().name()));
									}))
					));

			decl = constructorDecl(name,
					blockStmt().withStmts(NodeList.of(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(NodeList.of(sLocationCreationExpr))
					)))
					.withModifiers(NodeList.of(Modifier.Public))
					.withParams(parameters);

			if (GenSettings.generateDocs) {
				// TODO document the arguments

				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " with the specified content.",
								new String[]{"the object to compare this state with."}
						)
				);
			}

			return decl;
		}
	}
}
