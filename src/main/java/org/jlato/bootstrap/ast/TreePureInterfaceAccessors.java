package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.tree.TreeFactory.qualifiedName;

/**
 * @author Didier Villevalois
 */
public class TreePureInterfaceAccessors implements DeclContribution<TreeTypeDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeTypeDescriptor, ? extends MemberDecl>> declarations(TreeTypeDescriptor arg) {
		List<DeclPattern<TreeTypeDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();
		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
					new Accessor(parameter),
					new Mutator(parameter),
					new LambdaMutator(parameter)
			));
		}
		return decls;
	}

	public static class Accessor extends MemberPattern.OfMethod<TreeTypeDescriptor> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeTypeDescriptor arg) {
			return param.type() + " " + param.id().name() + "();";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());
			return decl;
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeTypeDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + "."
			);
		}
	}

	public static class Mutator extends MemberPattern.OfMethod<TreeTypeDescriptor> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeTypeDescriptor arg) {
			return arg.name + " " + propertySetterName(param) + "(" + param + ");";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());
			return decl;
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeTypeDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + "."
			);
		}
	}

	public static class LambdaMutator extends MemberPattern.OfMethod<TreeTypeDescriptor> {

		private final FormalParameter param;

		public LambdaMutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeTypeDescriptor arg) {
			return arg.name + " " + propertySetterName(param) + "(Mutation<" + boxedType(param.type()) + "> mutation);";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
			AllDescriptors.addImports(importManager, param.type());
			importManager.addImportByName(qualifiedName("org.jlato.util.Mutation"));
			return decl;
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeTypeDescriptor arg) {
			return genDoc(decl,
					"Mutates the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{"the mutation object"},
					"the mutated " + arg.description + "."
			);
		}
	}
}
