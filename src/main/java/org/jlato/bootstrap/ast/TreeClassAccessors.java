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
public class TreeClassAccessors implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();
		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
					new Accessor(parameter),
					new Mutator(parameter),
					new LambdaMutator(parameter)
			));
		}
		return decls;
	}

	public static class Accessor extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + param.type() + " " + param.id().name() + "() { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {

			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "(" + constantName(param) + ");").build()
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

	public static class Mutator extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.name + " " + propertySetterName(param) + "(" + param + ") { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {

			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "Replace(" + constantName(param) + ", " + param.id().name() + ");").build()
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

	public static class LambdaMutator extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {

		private final FormalParameter param;

		public LambdaMutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.name + " " + propertySetterName(param) + "(Mutation<" + boxedType(param.type()) + "> mutation) { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {

			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "Mutate(" + constantName(param) + ", mutation);").build()
			))));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Mutates the " + param.id().name() + " of this " + arg.description + ".",
								new String[]{"the mutation object"},
								"the mutated " + arg.description + "."
						)
				);

			return decl;
		}
	}
}
