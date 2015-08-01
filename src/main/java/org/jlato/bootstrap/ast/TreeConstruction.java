package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
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
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations() {
		return Arrays.asList(
				new TreePrivateConstructor(),
				new TreeMakeMethod(),
				new TreePublicConstructor()
		);
	}

	public static class TreePrivateConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("private " + arg.name + "(SLocation<" + arg.name + ".State> location) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, TreeClassDescriptor arg) {
			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qType("SLocation", stateType);

			final Name location = new Name("location");

			decl = constructorDecl()
					.withModifiers(NodeList.of(Modifier.Private))
					.withName(name)
					.withParams(NodeList.of(
							formalParameter().withId(variableDeclaratorId().withName(location)).withType(locationType)
					))
					.withBody(blockStmt().withStmts(NodeList.of(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(NodeList.of(location))
					)));

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
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public static STree<" + arg.name + ".State> make(..$_) { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> parameters = arg.parameters;
			final NodeList<FormalParameter> stateParams = deriveStateParams(parameters);
			final QualifiedType stateType = arg.stateType();
			final QualifiedType treeType = qType("STree", stateType);

			// Make STree creation expression from STrees
			final ObjectCreationExpr sTreeCreationExpr = objectCreationExpr()
					.withType(treeType)
					.withArgs(NodeList.of(
							objectCreationExpr().withType(stateType)
									.withArgs(parameters.map(p -> p.id().name()))
					));

			// Add STree factory method
			decl = methodDecl()
					.withModifiers(NodeList.of(Modifier.Public, Modifier.Static))
					.withType(treeType)
					.withName(new Name("make"))
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
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.name + "(..$_) { ..$_ }")
					// FIXME There is a bug here and TreePattern doesn't match the public modifier
					.suchThat(m -> ((ConstructorDecl) m).modifiers().contains(Modifier.Public));
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, TreeClassDescriptor arg) {
			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qType("SLocation", stateType);
			final NodeList<FormalParameter> parameters = arg.parameters;

			// Make SLocation creation expression from Trees
			final ObjectCreationExpr sLocationCreationExpr = objectCreationExpr()
					.withType(locationType)
					.withArgs(NodeList.of(
							methodInvocationExpr()
									.withName(new Name("make"))
									.withArgs(parameters.map(p -> {
										Type treeType = p.type();
										if (propertyFieldType(treeType)) return p.id().name();
										else return methodInvocationExpr()
												.withScope(some(new Name("TreeBase")))
												.withTypeArgs(NodeList.of(treeTypeToStateType((QualifiedType) treeType)))
												.withName(new Name("treeOf"))
												.withArgs(NodeList.of(p.id().name()));
									}))
					));

			decl = constructorDecl()
					.withModifiers(NodeList.of(Modifier.Public))
					.withName(name)
					.withParams(parameters)
					.withBody(blockStmt().withStmts(NodeList.of(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(NodeList.of(sLocationCreationExpr))
					)));

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
