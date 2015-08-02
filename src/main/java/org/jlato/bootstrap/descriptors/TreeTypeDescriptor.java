package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public abstract class TreeTypeDescriptor {

	public static final Name TREE_NAME = new Name("Tree");
	public static final Name STATE_NAME = new Name("State");
	public static final Name STREE_STATE_NAME = new Name("STreeState");

	public final Name packageName;
	public final Name name;
	public final String description;
	public final NodeList<QualifiedType> superInterfaces;
	public final NodeList<MemberDecl> shapes;

	public TreeTypeDescriptor(Name packageName, Name name, String description,
	                          NodeList<QualifiedType> superInterfaces,
	                          NodeList<MemberDecl> shapes) {
		this.packageName = packageName;
		this.name = name;
		this.description = description;
		this.superInterfaces = superInterfaces;
		this.shapes = shapes;
	}

	public abstract boolean isInterface();

	public abstract String treeFilePath();

	public abstract QualifiedType stateType();

	public NodeList<QualifiedType> stateSuperTypes() {
		return superInterfaces.map(tt ->
						tt.name().equals(TREE_NAME) ? qualifiedType().withName(STREE_STATE_NAME) :
								qualifiedType().withScope(some(qualifiedType().withName(tt.name()))).withName(STATE_NAME)
		);
	}

	public String prefixedDescription() {
		char firstChar = description.startsWith("'") ? description.charAt(1) :
				description.startsWith("\\\"") ? description.charAt(2) :
						description.charAt(0);
		switch (firstChar) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return "an " + description;
			default:
				return "a " + description;
		}
	}
}
