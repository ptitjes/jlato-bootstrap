package org.jlato.bootstrap.descriptors;

import org.jlato.tree.*;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.type.QualifiedType;

import java.util.Arrays;
import java.util.List;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.name;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public abstract class TreeTypeDescriptor {

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

	public Name interfaceName() {
		return name;
	}

	public QualifiedType interfaceType() {
		return qualifiedType(interfaceName());
	}

	public QualifiedName interfacePackageName() {
		return TreeFactory.qualifiedName(packageName).withQualifier(some(AllDescriptors.TREE_INTERFACES_ROOT));
	}

	public QualifiedName interfaceQualifiedName() {
		return TreeFactory.qualifiedName(interfaceName()).withQualifier(some(interfacePackageName()));
	}

	public String interfaceFilePath() {
		return AllDescriptors.TREE_INTERFACES_PATH + "/" + packageName + "/" + interfaceName() + ".java";
	}

	public Name stateTypeName() {
		return name("S" + name);
	}

	public QualifiedType stateType() {
		return qualifiedType(stateTypeName());
	}


	public QualifiedName stateTypePackageName() {
		return TreeFactory.qualifiedName(packageName).withQualifier(some(AllDescriptors.TREE_STATES_ROOT));
	}

	public QualifiedName stateTypeQualifiedName() {
		return TreeFactory.qualifiedName(stateTypeName()).withQualifier(some(stateTypePackageName()));
	}

	public String stateTypeFilePath() {
		return AllDescriptors.TREE_STATES_PATH + "/" + packageName + "/" + stateTypeName() + ".java";
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
