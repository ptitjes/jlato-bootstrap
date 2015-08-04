package org.jlato.bootstrap.descriptors;

import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.NodeList;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.tree.Trees.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeInterfaceDescriptor extends TreeTypeDescriptor {

	public TreeInterfaceDescriptor(Name packageName, Name name, String description,
	                               NodeList<QualifiedType> superInterfaces,
	                               NodeList<MemberDecl> shapes, NodeList<FormalParameter> parameters) {
		super(packageName, name, description, superInterfaces, shapes, parameters);
	}

	@Override
	public boolean isInterface() {
		return true;
	}
}
