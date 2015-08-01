package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.type;
import static org.jlato.rewrite.Quotes.param;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClassDescriptor extends TreeTypeDescriptor {

	public final boolean customTailored;
	public final NodeList<FormalParameter> parameters;

	public TreeClassDescriptor(Name packageName, Name name, String description,
	                           NodeList<QualifiedType> superInterfaces,
	                           boolean customTailored,
	                           NodeList<FormalParameter> parameters) {
		super(packageName, name, description, superInterfaces);
		this.customTailored = customTailored;
		this.parameters = parameters;
	}

	@Override
	public boolean isInterface() {
		return false;
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
