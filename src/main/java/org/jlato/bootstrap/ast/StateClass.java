package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.TypeDecl;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
class StateClass extends TypePattern.OfClass<TreeClassDescriptor> {

	public StateClass() {
		super(
				new StateBaseMembers(),
				new StateEqualsAndHashCode()
		);
	}

	@Override
	public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
		return typeDecl("public static class State extends SNodeState<..$_> implements ..$_ { ..$_ }");
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeClassDescriptor arg) {
		ClassDecl classDecl = (ClassDecl) super.rewrite(decl, arg);

		classDecl = classDecl
				.withExtendsClause(some(
						qualifiedType()
								.withName(TreeTypeDescriptor.SNODE_STATE_NAME)
								.withTypeArgs(some(NodeList.of(
										qualifiedType().withName(TreeTypeDescriptor.STATE_NAME)
								)))
				))
				.withImplementsClause(arg.stateSuperTypes());

		if (GenSettings.generateDocs)
			classDecl = classDecl.insertLeadingComment("/** A state object for " + arg.prefixedDescription() + ". */");

		return classDecl;
	}
}
