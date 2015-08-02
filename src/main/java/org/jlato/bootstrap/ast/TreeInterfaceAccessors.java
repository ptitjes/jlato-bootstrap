package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.blockStmt;

/**
 * @author Didier Villevalois
 */
public class TreeInterfaceAccessors implements DeclContribution<TreeInterfaceDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeInterfaceDescriptor, ? extends MemberDecl>> declarations(TreeInterfaceDescriptor arg) {
		List<DeclPattern<TreeInterfaceDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();
		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
					new Accessor(parameter),
					new Mutator(parameter),
					new LambdaMutator(parameter)
			));
		}
		return decls;
	}

	public static class Accessor extends Utils implements DeclPattern<TreeInterfaceDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeInterfaceDescriptor arg) {
			return memberDecl(param.type() + " " + param.id().name() + "();");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeInterfaceDescriptor arg) {
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

	public static class Mutator extends Utils implements DeclPattern<TreeInterfaceDescriptor, MethodDecl> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeInterfaceDescriptor arg) {
			return memberDecl(arg.name + " " + propertySetterName(param) + "(" + param + ");");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeInterfaceDescriptor arg) {
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

	public static class LambdaMutator extends Utils implements DeclPattern<TreeInterfaceDescriptor, MethodDecl> {

		private final FormalParameter param;

		public LambdaMutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		public Pattern<? extends Decl> matcher(TreeInterfaceDescriptor arg) {
			return memberDecl(arg.name + " " + propertySetterName(param) + "(Mutation<" + boxedType(param.type()) + "> mutation);");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeInterfaceDescriptor arg) {

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
