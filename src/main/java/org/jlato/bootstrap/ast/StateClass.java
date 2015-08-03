package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;

import java.util.Arrays;

import static org.jlato.tree.NodeOption.*;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
class StateClass extends TypePattern.OfClass<TreeClassDescriptor> {

	@Override
	protected String makeQuote(TreeClassDescriptor arg) {
		return "public static class State extends SNodeState<..$_> implements ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor arg) {
		return "/** A state object for " + arg.prefixedDescription() + ". */";
	}

	@Override
	protected ClassDecl contributeSignature(ClassDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
		decl = decl
				.withExtendsClause(some(
						qualifiedType(TreeTypeDescriptor.SNODE_STATE_NAME)
								.withTypeArgs(some(NodeList.of(
										qualifiedType(TreeTypeDescriptor.STATE_NAME)
								)))
				))
				.withImplementsClause(arg.stateSuperTypes());
		return decl;
	}

	@Override
	protected Iterable<DeclContribution<TreeClassDescriptor, MemberDecl>> contributions(TreeClassDescriptor arg) {
		return Arrays.asList(
				new StateBaseMembers(),
				new StateEqualsAndHashCode()
		);
	}
}
