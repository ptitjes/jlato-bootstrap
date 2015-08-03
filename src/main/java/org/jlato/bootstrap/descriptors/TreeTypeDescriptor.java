package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
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
	public static final Name TREE_BASE_NAME = new Name("TreeBase");
	public static final Name STATE_NAME = new Name("State");
	public static final Name STREE_STATE_NAME = new Name("STreeState");
	public static final Name SNODE_STATE_NAME = new Name("SNodeState");

	public final Name packageName;
	public final Name name;
	public final String description;
	public final NodeList<QualifiedType> superInterfaces;
	public final NodeList<MemberDecl> shapes;
	public final NodeList<FormalParameter> parameters;

	public TreeTypeDescriptor(Name packageName, Name name, String description,
	                          NodeList<QualifiedType> superInterfaces,
	                          NodeList<MemberDecl> shapes,
	                          NodeList<FormalParameter> parameters) {
		this.packageName = packageName;
		this.name = name;
		this.description = description;
		this.superInterfaces = superInterfaces;
		this.shapes = shapes;
		this.parameters = parameters;
	}

	public abstract boolean isInterface();

	public abstract String treeFilePath();

	public QualifiedType type() {
		return qualifiedType(name);
	}

	public abstract QualifiedType stateType();

	public NodeList<QualifiedType> stateSuperTypes() {
		return superInterfaces.map(tt ->
						tt.name().equals(TREE_NAME) ? qualifiedType(STREE_STATE_NAME) :
								qualifiedType(STATE_NAME).withScope(some(qualifiedType(tt.name())))
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
