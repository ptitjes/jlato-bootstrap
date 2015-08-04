package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
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

	public static class Accessor extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + param.type() + " " + param.id().name() + "() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "(" + arg.stateTypeName() + "." + constantName(param) + ");").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + "."
			);
		}
	}

	public static class Mutator extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + arg.name + " " + propertySetterName(param) + "(" + param + ") { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "Replace(" + arg.stateTypeName() + "." + constantName(param) + ", " + param.id().name() + ");").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + "."
			);
		}
	}

	public static class LambdaMutator extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final FormalParameter param;

		public LambdaMutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + arg.name + " " + propertySetterName(param) + "(Mutation<" + boxedType(param.type()) + "> mutation) { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());
			importManager.addImportByName(qualifiedName("org.jlato.util.Mutation"));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return location.safe" + (propertyFieldType(param.type()) ? "Property" : "Traversal") + "Mutate(" + arg.stateTypeName() + "." + constantName(param) + ", mutation);").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Mutates the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{"the mutation object"},
					"the mutated " + arg.description + "."
			);
		}
	}
}
