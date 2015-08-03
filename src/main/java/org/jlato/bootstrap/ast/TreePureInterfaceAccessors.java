package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.memberDecl;

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

	public static class Accessor extends Utils implements DeclPattern<TreeTypeDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeTypeDescriptor arg) {
			return memberDecl(param.type() + " " + param.id().name() + "();");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
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

	public static class Mutator extends Utils implements DeclPattern<TreeTypeDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeTypeDescriptor arg) {
			return memberDecl(arg.name + " " + propertySetterName(param) + "(" + param + ");");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
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

	public static class LambdaMutator extends Utils implements DeclPattern<TreeTypeDescriptor, MethodDecl> {

		private final FormalParameter param;

		public LambdaMutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeTypeDescriptor arg) {
			return memberDecl(arg.name + " " + propertySetterName(param) + "(Mutation<" + boxedType(param.type()) + "> mutation);");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {

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
