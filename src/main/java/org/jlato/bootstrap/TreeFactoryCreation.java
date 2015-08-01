package org.jlato.bootstrap;

import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.tree.NodeOption.none;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class TreeFactoryCreation extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	private NodeList<MemberDecl> factoryMethods = NodeList.empty();

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {

		int constructorIndex = 0;
		ConstructorDecl constructorDecl = null;
		for (MemberDecl memberDecl : safeList(decl.members())) {
			if (memberDecl instanceof ConstructorDecl) {
				constructorIndex++;
				if (constructorIndex == 2) {
					constructorDecl = (ConstructorDecl) memberDecl;
					break;
				}
			}
		}

		if (constructorDecl == null) return decl;

		NodeList<Type> typeArgs = null;
		if (constructorDecl.typeParams() != null) {
			typeArgs = NodeList.<Type>empty();
			for (TypeParameter typeParameter : safeList(constructorDecl.typeParams())) {
				typeArgs = typeArgs.append(qualifiedType().withName(typeParameter.name()));
			}
		}

		NodeList<Expr> args = NodeList.empty();
		for (FormalParameter parameter : safeList(constructorDecl.params())) {
			Type type = parameter.type();
			if (type instanceof QualifiedType && ((QualifiedType) type).name().id().equals("NodeList")) {
				QualifiedType qualifiedType = (QualifiedType) type;

				args = args.append(methodInvocationExpr()
								.withScope(some(qualifiedType.name()))
								.withTypeArgs(NodeList.<Type>empty().append(qualifiedType.typeArgs().get().get(0)))
								.withName(new Name("empty"))
				);
			} else if (type instanceof PrimitiveType) {
				PrimitiveType.Primitive primitive = ((PrimitiveType) type).primitive();
				switch (primitive) {
					case Boolean:
						args = args.append(LiteralExpr.of(false));
						break;
				}
			} else {
				args = args.append(LiteralExpr.nullLiteral());
			}
		}

		QualifiedType resultType = qualifiedType().withName(decl.name());
		Stmt creation = returnStmt().withExpr(
				some(objectCreationExpr().withTypeArgs(typeArgs).withType(resultType).withArgs(args).withBody(none()))
		);

		String name = decl.name().id();
		MethodDecl method = methodDecl()
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Static))
				.withTypeParams(constructorDecl.typeParams())
				.withType(resultType)
				.withName(new Name(lowerCaseFirst(name)))
				.withParams(NodeList.<FormalParameter>empty())
				.withBody(some(blockStmt().withStmts(s -> s.append(creation))));

		factoryMethods = factoryMethods.append(method);

		return decl;
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		ClassDecl classDecl = classDecl()
				.withModifiers(m -> m.append(Modifier.Public).append(Modifier.Abstract))
				.withName(new Name("TreeFactory"));

		classDecl = classDecl.withMembers(factoryMethods);

		CompilationUnit cu = compilationUnit()
				.withPackageDecl(new PackageDecl(null, QualifiedName.of("org.jlato.tree")))
				.withImports(
						NodeList.<ImportDecl>empty()
								.append(new ImportDecl(QualifiedName.of("org.jlato.tree.decl"), false, true))
								.append(new ImportDecl(QualifiedName.of("org.jlato.tree.expr"), false, true))
								.append(new ImportDecl(QualifiedName.of("org.jlato.tree.name"), false, true))
								.append(new ImportDecl(QualifiedName.of("org.jlato.tree.stmt"), false, true))
								.append(new ImportDecl(QualifiedName.of("org.jlato.tree.type"), false, true)))
				.withTypes(NodeList.<TypeDecl>empty().append(classDecl));
		return super.finish(treeSet.put("org/jlato/tree/TreeFactory.java", cu), hierarchy);
	}
}
