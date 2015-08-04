package org.jlato.bootstrap;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.rewrite.Substitution;
import org.jlato.rewrite.TypeSafeMatcher;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.AnnotationExpr;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.stmt.ForeachStmt;
import org.jlato.tree.type.*;
import org.jlato.util.Function1;

import java.util.Iterator;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class Utils {

	public static NodeList<FormalParameter> collectConstructorParams(ClassDecl decl) {
		Iterator<ConstructorDecl> iterator = decl.findAll(publicConstructorMatcher).iterator();
		if (!iterator.hasNext()) return null;
		final ConstructorDecl treeConstructor = iterator.next();
		return treeConstructor.params();
	}

	protected static boolean nullable(FormalParameter p) {
		final Type type = p.type();
		if (type instanceof PrimitiveType) return false;
		final String name = ((QualifiedType) type).name().id();
		return !(name.equals("NodeOption") || name.equals("NodeList") || name.equals("NodeEither"));
	}

	public static String constantName(FormalParameter parameter) {
		return constantName(parameter.id().name().id(), parameter.type());
	}

	public static String constantName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(2)));
			} else if (propertyName.startsWith("has")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(3)));
			}
		}
		return camelToConstant(propertyName);
	}

	public static String propertySetterName(FormalParameter parameter) {
		return propertySetterName(parameter.id().name().id(), parameter.type());
	}

	public static String propertySetterName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return "set" + upperCaseFirst(propertyName.substring(2));
			} else if (propertyName.startsWith("has")) {
				return "set" + upperCaseFirst(propertyName.substring(3));
			}
		}
		return "with" + upperCaseFirst(propertyName);
	}

	public static Type boxedType(Type type) {
		if (type instanceof QualifiedType) return type;
		else if (type instanceof PrimitiveType) {
			Primitive primitive = ((PrimitiveType) type).primitive();
			switch (primitive) {
				case Boolean:
					return qType("Boolean");
				case Byte:
					return qType("Byte");
				case Short:
					return qType("Short");
				case Int:
					return qType("Integer");
				case Long:
					return qType("Long");
				case Float:
					return qType("Float");
				case Double:
					return qType("Double");
				case Char:
					return qType("Character");
			}
		}
		return null;
	}

	public static AnnotationExpr overrideAnn() {
		return markerAnnotationExpr(qualifiedName("Override"));
	}

	public static AnnotationExpr deprecatedAnn() {
		return markerAnnotationExpr(qualifiedName("Deprecated"));
	}

	public static NodeList<FormalParameter> deriveStateParams(NodeList<FormalParameter> treeConstructorParams) {
		NodeList<FormalParameter> stateConstructorParams = NodeList.empty();
		for (FormalParameter param : treeConstructorParams) {
			Type treeType = param.type();

			Type stateParamType = treeTypeToSTreeType(treeType);
			stateConstructorParams = stateConstructorParams.append(
					formalParameter(stateParamType, param.id())
			);
		}
		return stateConstructorParams;
	}

	public static Type treeTypeToSTreeType(Type treeType) {
		if (propertyFieldType(treeType)) {
			return treeType;
		} else {
			final QualifiedType qualifiedType = (QualifiedType) treeType;

			final TreeTypeDescriptor descriptor = AllDescriptors.get(qualifiedType.name());
			boolean isInterface = descriptor != null && descriptor.isInterface();

			final QualifiedType stateType = treeTypeToStateType(qualifiedType);
			return qualifiedType(AllDescriptors.BU_TREE)
					.withTypeArgs(some(NodeList.of(isInterface ? wildcardType().withExt(some(stateType)) : stateType)));
		}
	}

	public static Type treeTypeToStateType(Type treeType) {
		if (propertyFieldType(treeType)) {
			return treeType;
		} else {
			return treeTypeToStateType((QualifiedType) treeType);
		}
	}

	public static QualifiedType treeTypeToStateType(QualifiedType treeType) {
		final Name name = treeType.name();
		final String id = name.id();
		switch (id) {
			case "NodeList":
				return qualifiedType(AllDescriptors.S_NODE_LIST);
			case "NodeOption":
				return qualifiedType(AllDescriptors.S_NODE_OPTION);
			case "NodeEither":
				return qualifiedType(AllDescriptors.S_NODE_EITHER);
			default:
				final TreeTypeDescriptor descriptor = AllDescriptors.get(name);
				return descriptor.stateType();
		}
	}

	public static boolean propertyFieldType(Type treeType) {
		if (treeType instanceof PrimitiveType) return true;
		if (treeType instanceof QualifiedType) {
			String name = ((QualifiedType) treeType).name().id();
			return name.equals("String") ||
					name.equals("Class") ||
					name.equals("ModifierKeyword") ||
					name.equals("Primitive") ||
					name.endsWith("Op");
		}
		return false;
	}

	public static <T extends Tree> NodeList<T> safeList(NodeList<T> list) {
		return list == null ? NodeList.<T>empty() : list;
	}

	public static QualifiedType qType(String typeName) {
		return TreeFactory.qualifiedType(name(typeName));
	}

	public static QualifiedType qType(String typeName, Type typeArg) {
		return TreeFactory.qualifiedType(name(typeName)).withTypeArgs(some(NodeList.of(typeArg)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2) {
		return TreeFactory.qualifiedType(name(typeName)).withTypeArgs(some(NodeList.of(typeArg1, typeArg2)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2, Type typeArg3) {
		return TreeFactory.qualifiedType(name(typeName)).withTypeArgs(some(NodeList.of(typeArg1, typeArg2, typeArg3)));
	}

	public static String constantToCamel(String constantName) {
		StringBuilder buffer = new StringBuilder();
		String[] split = constantName.split("_");
		boolean first = true;
		for (String s : split) {
			String part = s.toLowerCase();
			if (first) {
				first = false;
				buffer.append(part);
			} else buffer.append(upperCaseFirst(part));
		}
		return buffer.toString();
	}

	public static String camelToConstant(String name) {
		StringBuilder buffer = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				buffer.append("_");
			}
			buffer.append(Character.toUpperCase(c));
		}
		return buffer.toString();
	}

	public static String lowerCaseFirst(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String upperCaseFirst(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String genDoc(FieldDecl decl, String description) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * ").append(description).append("\n");
		builder.append(" */");
		return builder.toString();
	}

	public static String genDoc(MethodDecl decl, String description, String[] paramDescription, String returnDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * ").append(description).append("\n");
		builder.append(" *\n");
		int index = 0;
		for (FormalParameter param : decl.params()) {
			builder.append(" * @param ").append(param.id()).append(" ").append(paramDescription[index]).append("\n");
			index++;
		}
		if (returnDescription != null)
			builder.append(" * @return ").append(returnDescription).append("\n");
		builder.append(" */");
		return builder.toString();
	}

	public static String genDoc(ConstructorDecl decl, String description, String[] paramDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * ").append(description).append("\n");
		builder.append(" *\n");
		int index = 0;
		for (FormalParameter param : decl.params()) {
			builder.append(" * @param ").append(param.id()).append(" ").append(paramDescription[index]).append("\n");
			index++;
		}
		builder.append(" */");
		return builder.toString();
	}

	// Matchers

	public static TypeSafeMatcher<ConstructorDecl> constructors(final Function1<ConstructorDecl, Boolean> predicate) {
		return new MatcherImpl<ConstructorDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof ConstructorDecl)) return null;
				ConstructorDecl decl = (ConstructorDecl) o;
				if (!predicate.apply(decl)) return null;
				return substitution;
			}
		};
	}

	public static final TypeSafeMatcher<ConstructorDecl> publicConstructorMatcher = constructors(c -> c.modifiers().contains(Modifier.Public));

	public static abstract class MatcherImpl<T> implements TypeSafeMatcher<T> {
		@Override
		public final Substitution match(Object o) {
			return match(o, Substitution.empty());
		}
	}
}
