package org.jlato.bootstrap.descriptors;

import org.jlato.bootstrap.Utils;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
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

	public TreeClassDescriptor(Name packageName, Name name, String description,
	                           NodeList<QualifiedType> superInterfaces,
	                           NodeList<MemberDecl> shapes,
	                           boolean customTailored,
	                           NodeList<FormalParameter> parameters) {
		super(packageName, name, description, superInterfaces, shapes, parameters);
		this.customTailored = customTailored;
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

	public NodeList<FormalParameter> stateParameters() {
		return Utils.deriveStateParams(parameters);
	}

	public String[] parameterDescriptions() {
		final NodeList<FormalParameter> stateParameters = stateParameters();
		String[] paramDescriptions = new String[stateParameters.size()];
		int index = 0;
		for (FormalParameter parameter : stateParameters) {
			// TODO Do that well...
			paramDescriptions[index] = parameter.id().name().id();
			index++;
		}
		return paramDescriptions;
	}
}
