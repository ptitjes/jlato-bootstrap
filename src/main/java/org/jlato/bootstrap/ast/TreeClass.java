package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.typeDecl;

/**
 * @author Didier Villevalois
 */
public class TreeClass extends TypePattern.OfClass<TreeClassDescriptor> {

	public TreeClass() {
		super(
				new TreeConstruction(),
				new StateClassContribution()
		);
	}

	@Override
	public Pattern<TypeDecl> matcher(TreeClassDescriptor arg) {
		return typeDecl(
				"public class " + arg.name + " extends TreeBase<..$_> implements ..$_ { ..$_ }"
		);
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeClassDescriptor arg) {
		ClassDecl classDecl = (ClassDecl) super.rewrite(decl, arg);

		if (GenSettings.generateDocs)
			classDecl = classDecl.insertLeadingComment("/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */");

		return classDecl;
	}

	public static class StateClassContribution implements DeclContribution<TreeClassDescriptor, MemberDecl> {
		@Override
		public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
			return Arrays.asList(new StateClass());
		}
	}
}
