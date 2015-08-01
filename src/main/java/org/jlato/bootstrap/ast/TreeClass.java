package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.Name;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.typeDecl;

/**
 * @author Didier Villevalois
 */
public class TreeClass extends TypePattern.OfClass<TreeClassDescriptor> {

	public TreeClass() {
		super(
				new StateClassContribution()
		);
	}

	@Override
	public Pattern<TypeDecl> matcher(TreeClassDescriptor arg) {
		final Name name = arg.name;
		final Name superTypeName = arg.superTypeName;
		return typeDecl(
				"public class " + name +
						" extends TreeBase<" + name + ".State, " + superTypeName + ", " + name + ">" +
						" implements " + superTypeName +
						" { ..$_ }"
		);
	}

	public static class StateClassContribution implements DeclContribution<TreeClassDescriptor, MemberDecl> {
		@Override
		public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations() {
			return Arrays.asList(new StateClass());
		}
	}
}
