package org.jlato.bootstrap;

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
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.ReferenceType;
import org.jlato.tree.type.Type;
import org.jlato.util.Function1;

import java.util.Iterator;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class Utils {

	public NodeList<FormalParameter> collectConstructorParams(ClassDecl decl) {
		Iterator<ConstructorDecl> iterator = decl.findAll(publicConstructorMatcher).iterator();
		if (!iterator.hasNext()) return null;
		final ConstructorDecl treeConstructor = iterator.next();
		return treeConstructor.params();
	}

	protected boolean nullable(FormalParameter p) {
		final Type type = p.type();
		if (type instanceof PrimitiveType) return false;
		final String name = ((QualifiedType) type).name().id();
		return !(name.equals("NodeOption") || name.equals("NodeList") || name.equals("NodeEither"));
	}

	public String constantName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == PrimitiveType.Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(2)));
			} else if (propertyName.startsWith("has")) {
				return camelToConstant(lowerCaseFirst(propertyName.substring(3)));
			}
		}
		return camelToConstant(propertyName);
	}

	public String propertySetterName(String propertyName, Type propertyType) {
		if (propertyType instanceof PrimitiveType &&
				((PrimitiveType) propertyType).primitive() == PrimitiveType.Primitive.Boolean) {
			if (propertyName.startsWith("is")) {
				return "set" + upperCaseFirst(propertyName.substring(2));
			} else if (propertyName.startsWith("has")) {
				return "set" + upperCaseFirst(propertyName.substring(3));
			}
		}
		return "with" + upperCaseFirst(propertyName);
	}

	public Type boxedType(Type type) {
		if (type instanceof QualifiedType) return type;
		else if (type instanceof PrimitiveType) {
			PrimitiveType.Primitive primitive = ((PrimitiveType) type).primitive();
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

	public AnnotationExpr overrideAnn() {
		return markerAnnotationExpr().withName(QualifiedName.of("Override"));
	}

	public NodeList<FormalParameter> deriveStateParams(NodeList<FormalParameter> treeConstructorParams, TreeTypeHierarchy hierarchy) {
		NodeList<FormalParameter> stateConstructorParams = NodeList.empty();
		for (FormalParameter param : treeConstructorParams) {
			Type treeType = param.type();

			Type stateParamType = treeTypeToSTreeType(treeType, hierarchy);
			stateConstructorParams = stateConstructorParams.append(
					formalParameter().withType(stateParamType).withId(param.id())
			);
		}
		return stateConstructorParams;
	}

	public Type treeTypeToSTreeType(Type treeType, TreeTypeHierarchy hierarchy) {
		Type stateParamType;
		if (propertyFieldType(treeType)) {
			stateParamType = treeType;
		} else {
			Type stateType = treeTypeToStateType((QualifiedType) treeType);
			if (hierarchy.isInterface(((QualifiedType) treeType).name())) {
				stateType = wildcardType().withExt(some((ReferenceType) stateType));
			}
			stateParamType = qType("STree", stateType);
		}
		return stateParamType;
	}

	public static boolean propertyFieldType(Type treeType) {
		if (treeType instanceof PrimitiveType) return true;
		if (treeType instanceof QualifiedType) {
			String name = ((QualifiedType) treeType).name().id();
			return name.equals("String") ||
					name.equals("Class") ||
					name.equals("LToken") ||
					name.equals("IndexedList") ||
					name.equals("Primitive") ||
					name.endsWith("Op");
		}
		return false;
	}

	public static QualifiedType treeTypeToStateType(QualifiedType treeQType) {
		String treeClassName = treeQType.name().id();

		QualifiedType stateType = null;
		if (treeClassName.equals("NodeList")) {
			stateType = qualifiedType().withName(new Name("SNodeListState"));
		} else if (treeClassName.equals("NodeOption")) {
			stateType = qualifiedType().withName(new Name("SNodeOptionState"));
		} else if (treeClassName.equals("NodeEither")) {
			stateType = qualifiedType().withName(new Name("SNodeEitherState"));
		} else {
			stateType = qualifiedType()
					.withScope(some(treeQType))
					.withName(NodeStatesRemoval.STATE_NAME);
		}
		return stateType;
	}

	public static <T extends Tree> NodeList<T> safeList(NodeList<T> list) {
		return list == null ? NodeList.<T>empty() : list;
	}

	public static QualifiedType qType(String typeName) {
		return TreeFactory.qualifiedType().withName(new Name(typeName));
	}

	public static QualifiedType qType(String typeName, Type typeArg) {
		return TreeFactory.qualifiedType().withName(new Name(typeName))
				.withTypeArgs(some(NodeList.of(typeArg)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2) {
		return TreeFactory.qualifiedType().withName(new Name(typeName))
				.withTypeArgs(some(NodeList.of(typeArg1, typeArg2)));
	}

	public static QualifiedType qType(String typeName, Type typeArg1, Type typeArg2, Type typeArg3) {
		return TreeFactory.qualifiedType().withName(new Name(typeName))
				.withTypeArgs(some(NodeList.of(typeArg1, typeArg2, typeArg3)));
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

	public static QualifiedType stateType(ClassDecl classDecl) {
		return qualifiedType().withScope(some(qualifiedType().withName(classDecl.name()))).withName(new Name("State"));
	}

	public static NodeList<MemberDecl> insertBeforeShapes(NodeList<MemberDecl> ts, NodeList<MemberDecl> ls) {
		NodeList<MemberDecl> newTs = NodeList.empty();
		boolean inserted = false;
		for (MemberDecl t : ts) {
			if (!inserted) {
				if (constantShapeMatcher.match(t) != null) {
					newTs = newTs.appendAll(ls);
					inserted = true;
				}
			}
			newTs = newTs.append(t);
		}

		if (!inserted) {
			newTs = newTs.appendAll(ls);
		}

		return newTs;
	}

	public static String genDoc(MethodDecl decl, String description, String[] paramDescription, String returnDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * " + description + "\n");
		builder.append(" *\n");
		int index = 0;
		for (FormalParameter param : decl.params()) {
			builder.append(" * @param " + param.id() + " " + paramDescription[index] + "\n");
			index++;
		}
		if (returnDescription != null)
			builder.append(" * @return " + returnDescription + "\n");
		builder.append(" */");
		return builder.toString();
	}

	public static String genDoc(ConstructorDecl decl, String description, String[] paramDescription) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * " + description + "\n");
		builder.append(" *\n");
		int index = 0;
		for (FormalParameter param : decl.params()) {
			builder.append(" * @param " + param.id() + " " + paramDescription[index] + "\n");
			index++;
		}
		builder.append(" */");
		return builder.toString();
	}

	// Matchers

	public static TypeSafeMatcher<FieldDecl> traversalConstantMatcher() {
		return new MatcherImpl<FieldDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof FieldDecl)) return null;
				FieldDecl fieldDecl = (FieldDecl) o;
				Type type = fieldDecl.type();
				if (type instanceof PrimitiveType) return null;
				QualifiedType qualifiedType = (QualifiedType) type;
				String typeName = qualifiedType.name().id();
				if (!(typeName.equals("STraversal") || typeName.equals("STypeSafeTraversal"))) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<FieldDecl> propertyConstantMatcher() {
		return new MatcherImpl<FieldDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof FieldDecl)) return null;
				FieldDecl fieldDecl = (FieldDecl) o;
				Type type = fieldDecl.type();
				if (type instanceof PrimitiveType) return null;
				QualifiedType qualifiedType = (QualifiedType) type;
				String typeName = qualifiedType.name().id();
				if (!(typeName.equals("SProperty") || typeName.equals("STypeSafeProperty"))) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<ClassDecl> stateClassMatcher() {
		return new MatcherImpl<ClassDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof ClassDecl)) return null;
				ClassDecl decl = (ClassDecl) o;
				NodeOption<QualifiedType> extendsClause = decl.extendsClause();
				if (extendsClause.isNone()) return null;
				QualifiedType type = extendsClause.get();
				if (!type.name().id().equals("SNodeState")) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<InterfaceDecl> stateInterfaceMatcher() {
		return new MatcherImpl<InterfaceDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof InterfaceDecl)) return null;
				InterfaceDecl decl = (InterfaceDecl) o;
				NodeList<QualifiedType> extendsClause = decl.extendsClause();
				if (extendsClause.isEmpty()) return null;
				QualifiedType type = extendsClause.get(0);
				String typeName = type.name().id();
				if (!(typeName.equals("STreeState") || typeName.equals("State"))) return null;
				return substitution;
			}
		};
	}

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

	public static TypeSafeMatcher<MethodDecl> methods(final Function1<MethodDecl, Boolean> predicate) {
		return new MatcherImpl<MethodDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof MethodDecl)) return null;
				MethodDecl decl = (MethodDecl) o;
				if (!predicate.apply(decl)) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<FieldDecl> fields(final Function1<FieldDecl, Boolean> predicate) {
		return new MatcherImpl<FieldDecl>() {
			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof FieldDecl)) return null;
				FieldDecl decl = (FieldDecl) o;
				if (!predicate.apply(decl)) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<Expr> objectCreation(Function1<ObjectCreationExpr, Boolean> predicate) {
		return new TypeSafeMatcher<Expr>() {
			@Override
			public Substitution match(Object o) {
				return match(o, Substitution.empty());
			}

			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof ObjectCreationExpr)) return null;
				ObjectCreationExpr expr = (ObjectCreationExpr) o;
				if (!predicate.apply(expr)) return null;
				return substitution;
			}
		};
	}

	public static TypeSafeMatcher<Expr> methodCall(java.util.function.Predicate<MethodInvocationExpr> predicate) {
		return new TypeSafeMatcher<Expr>() {
			@Override
			public Substitution match(Object o) {
				return match(o, Substitution.empty());
			}

			@Override
			public Substitution match(Object o, Substitution substitution) {
				if (!(o instanceof MethodInvocationExpr)) return null;
				MethodInvocationExpr expr = (MethodInvocationExpr) o;
				if (!predicate.test(expr)) return null;
				return substitution;
			}
		};
	}

	public static final TypeSafeMatcher<FieldDecl> constantShapeMatcher = fields(f -> {
		Type type = f.type();
		return type instanceof QualifiedType && ((QualifiedType) type).name().id().equals("LexicalShape");
	});

	public static final TypeSafeMatcher<MethodDecl> makeMethodMatcher = methods(m -> m.name().id().equals("make"));
	public static final TypeSafeMatcher<ConstructorDecl> publicConstructorMatcher = constructors(c -> c.modifiers().contains(Modifier.Public));

	public static final TypeSafeMatcher<Expr> sTreeCreationMatcher = objectCreation(oc -> oc.type().name().id().equals("STree"));
	public static final TypeSafeMatcher<Expr> stateCreationMatcher = objectCreation(oc -> oc.type().name().id().equals("State"));
	public static final TypeSafeMatcher<Expr> makeCallMatcher = methodCall(mc -> mc.name().id().equals("make"));

	public static abstract class MatcherImpl<T> implements TypeSafeMatcher<T> {
		@Override
		public final Substitution match(Object o) {
			return match(o, Substitution.empty());
		}
	}
}
