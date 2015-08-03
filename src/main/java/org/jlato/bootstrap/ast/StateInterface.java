package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.*;

import java.util.Arrays;

import static org.jlato.tree.NodeOption.*;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
class StateInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	@Override
	protected String makeQuote(TreeInterfaceDescriptor arg) {
		return "interface State extends ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(InterfaceDecl decl, TreeInterfaceDescriptor arg) {
		return "/** A state object for " + arg.prefixedDescription() + ". */";
	}

	@Override
	protected InterfaceDecl contributeSignature(InterfaceDecl decl, ImportManager importManager, TreeInterfaceDescriptor arg) {
		NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && parentInterfaces.get(0).name().equals(TreeTypeDescriptor.TREE_NAME);

		return decl.withExtendsClause(
				treeInterfaceChild ? NodeList.of(qualifiedType(TreeTypeDescriptor.STREE_STATE_NAME)) :
						parentInterfaces.map(t ->
								qualifiedType(TreeTypeDescriptor.STATE_NAME).withScope(some(t)))
		);
	}

	@Override
	protected Iterable<DeclContribution<TreeInterfaceDescriptor, MemberDecl>> contributions(TreeInterfaceDescriptor arg) {
		return Arrays.asList();
	}
}
