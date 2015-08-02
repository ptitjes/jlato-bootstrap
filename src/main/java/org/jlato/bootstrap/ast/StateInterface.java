package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
class StateInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	public StateInterface() {
		super(
		);
	}

	@Override
	public Pattern<? extends Decl> matcher(TreeInterfaceDescriptor arg) {
		return typeDecl("interface State extends ..$_ { ..$_ }");
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeInterfaceDescriptor arg) {
		InterfaceDecl interfaceDecl = (InterfaceDecl) super.rewrite(decl, arg);

		NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && parentInterfaces.get(0).name().equals(TreeTypeDescriptor.TREE_NAME);

		interfaceDecl = interfaceDecl.withExtendsClause(
				treeInterfaceChild ? NodeList.of(qualifiedType().withName(TreeTypeDescriptor.STREE_STATE_NAME)) :
						parentInterfaces.map(t ->
								qualifiedType().withScope(some(t)).withName(TreeTypeDescriptor.STATE_NAME))
		);

		if (GenSettings.generateDocs)
			interfaceDecl = interfaceDecl.insertLeadingComment("/** A state object for " + arg.prefixedDescription() + ". */");

		return interfaceDecl;
	}
}
