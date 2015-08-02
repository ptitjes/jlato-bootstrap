package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.AssignExpr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class StateBaseMembers implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();
		decls.add(new Constructor());
		decls.add(new KindMethod());
		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
					new Accessor(parameter),
					new Mutator(parameter)
			));
		}
		return decls;
	}

	public static class Constructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {

		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + TreeTypeDescriptor.STATE_NAME + "(..$_) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, TreeClassDescriptor arg) {

			final NodeList<FormalParameter> stateParameters = arg.stateParameters();
			decl = constructorDecl().withName(TreeTypeDescriptor.STATE_NAME)
					.withParams(stateParameters)
					.withBody(blockStmt().withStmts(
							stateParameters.map(p -> expressionStmt().withExpr(
									assignExpr().withTarget(fieldAccessExpr().withScope(some(thisExpr())).withName(p.id().name()))
											.withOp(AssignExpr.AssignOp.Normal).withValue(p.id().name())
							))
					));

			if (GenSettings.generateDocs) {
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Constructs " + arg.prefixedDescription() + " state.",
								arg.parameterDescriptions()
						)
				);
			}

			return decl;
		}
	}

	public static class KindMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public Kind kind() { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {
			// Add STree factory method
			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return Kind." + arg.name + ";").build()
			))));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Returns the kind of this " + arg.description + ".",
								new String[]{},
								"the kind of this " + arg.description + "."
						)
				);

			return decl;
		}
	}

	public static class Accessor extends Utils implements DeclPattern<TreeClassDescriptor, FieldDecl> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public final " + treeTypeToSTreeType(param.type()) + " " + param.id().name() + ";");
		}

		@Override
		public FieldDecl rewrite(FieldDecl decl, TreeClassDescriptor arg) {

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"The " + param.id().name() + " of this " + arg.description + " state."
						)
				);

			return decl;
		}
	}

	public static class Mutator extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.stateType() + " " + propertySetterName(param) + "(" + treeTypeToSTreeType(param.type()) + " " + param.id().name() + ") { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {

			// Make state creation expression from STrees
			final ObjectCreationExpr stateCreationExpr = objectCreationExpr().withType(arg.stateType())
					.withArgs(arg.parameters.map(p -> p.id().name()));

			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					returnStmt().withExpr(some(stateCreationExpr))
			))));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Returns the " + param.id().name() + " of this " + arg.description + ".",
								new String[]{},
								"the " + param.id().name() + " of this " + arg.description + "."
						)
				);

			return decl;
		}
	}
}
