package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeInterfaceDescriptor extends TreeTypeDescriptor {

	public TreeInterfaceDescriptor(Name packageName, Name name, String description,
	                               NodeList<QualifiedType> superInterfaces) {
		super(packageName, name, description, superInterfaces);
	}

	@Override
	public boolean isInterface() {
		return true;
	}

	@Override
	public String treeFilePath() {
		return "org/jlato/tree/" + packageName + "/" + name + ".java";
	}

	@Override
	public QualifiedType stateType() {
		return qualifiedType().withScope(some(qualifiedType().withName(name))).withName(STATE_NAME);
	}
}
