package org.jlato.bootstrap.descriptors;

import org.jlato.bootstrap.Utils;
import org.jlato.tree.NodeList;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.tree.Trees.name;
import static org.jlato.tree.Trees.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClassDescriptor extends TreeTypeDescriptor {

	public final NodeList<Expr> defaultValues;
	public final boolean customTailored;

	public TreeClassDescriptor(Name packageName, Name name, String description,
	                           NodeList<QualifiedType> superInterfaces,
	                           NodeList<MemberDecl> shapes,
	                           NodeList<FormalParameter> parameters,
	                           NodeList<Expr> defaultValues,
	                           boolean customTailored) {
		super(packageName, name, description, superInterfaces, shapes, parameters);
		this.defaultValues = defaultValues;
		this.customTailored = customTailored;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	public Name className() {
		return name("TD" + name);
	}

	public QualifiedType classType() {
		return qualifiedType(className());
	}

	public QualifiedName classPackageName() {
		return Trees.qualifiedName(packageName).withQualifier(AllDescriptors.TREE_CLASSES_ROOT);
	}

	public QualifiedName classQualifiedName() {
		return Trees.qualifiedName(className()).withQualifier(classPackageName());
	}

	public String classFilePath() {
		return AllDescriptors.TREE_CLASSES_PATH + "/" + packageName + "/" + className() + ".java";
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
			paramDescriptions[index] = parameter.id().get().name().id();
			index++;
		}
		return paramDescriptions;
	}

	public boolean hasKnownValueFor(FormalParameter parameter) {
		if (defaultValues.get(parameters.indexOf(parameter)) != null) return true;
		else {
			Type type = parameter.type();
			if (type instanceof QualifiedType) {
				String name = ((QualifiedType) type).name().id();
				return name.equals("NodeList") || name.equals("NodeOption");
			} else if (type instanceof PrimitiveType) {
				Primitive primitive = ((PrimitiveType) type).primitive();
				return primitive == Primitive.Boolean;
			}
			return false;
		}
	}

	public Expr defaultValueFor(FormalParameter parameter) {
		return defaultValues.get(parameters.indexOf(parameter));
	}
}
