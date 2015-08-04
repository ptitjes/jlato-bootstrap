package org.jlato.bootstrap;

import org.jlato.printer.FormattingSettings;
import org.jlato.printer.Printer;
import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

import static org.jlato.tree.TreeFactory.*;
import static org.jlato.tree.expr.AssignOp.Normal;
import static org.jlato.tree.expr.BinaryOp.Less;
import static org.jlato.tree.expr.UnaryOp.PostIncrement;

/**
 * @author Didier Villevalois
 */
public class NodeAccessorsUnitTests extends TreeClassRefactoring {

	public static final Name STATE_NAME = name("State");
	public static final QualifiedType ARBITRARY_TYPE = qType("Arbitrary");
	public static final Name ARBITRARY_NAME = name("arbitrary");

	private HashSet<Type> arbitraryTypes = new HashSet<>();

	private TestClass accessorsTestClass = new TestClass("org.jlato.unit", "TreesAccessorsTest");
	private TestClass lambdaAccessorsTestClass = new TestClass("org.jlato.unit", "TreesLambdaAccessorsTest");
	private TestClass equalsHashTestClass = new TestClass("org.jlato.unit", "TreesEqualsHashCodeTest");
	private TestClass kindTestClass = new TestClass("org.jlato.unit", "TreesKindTest");

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {
		if (decl.name().id().equals("LiteralExpr")) return decl;

		NodeList<FormalParameter> params = collectConstructorParams(decl);
		if (params == null) return decl;

		if (decl.name().id().equals("CompilationUnit")) {
			NodeList<FormalParameter> preprocessed = emptyList();
			for (FormalParameter param : params) {
				if (!param.id().name().id().equals("preamble"))
					preprocessed = preprocessed.append(param);
			}
			params = preprocessed;
		}

		final String treeName = decl.name().id();
		final QualifiedType treeType = qType(decl.name().id());

		if (!params.isEmpty()) {
			generateAccessorsTestMethod(treeName, treeType, params);
			generateLambdaAccessorsTestMethod(treeName, treeType, params);
		}

		generateEqualsHashTestMethod(treeName, treeType, params);

		generateKindTestMethod(treeName, treeType, params);

		return decl;
	}

	private void generateAccessorsTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = emptyList();
		NodeList<Stmt> loopStmts = emptyList();

		final Name tested = name("t");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().name().id(), p.type())))
								.withScope(some(e)).withArgs(listOf(p.id().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> junitAssert("assertEquals", p.id().name(),
						methodInvocationExpr(p.id().name()).withScope(some(tested)))
		));

		stmts = stmts.append(loopFor(10, loopStmts));

		accessorsTestClass.generateTestMethod(treeName, stmts);
	}

	private void generateLambdaAccessorsTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = emptyList();
		NodeList<Stmt> loopStmts = emptyList();

		final Name tested = name("t");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().name().id(), p.type())))
								.withScope(some(e)).withArgs(listOf(p.id().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> junitAssert("assertEquals", p.id().name(),
						methodInvocationExpr(p.id().name()).withScope(some(tested)))
		));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name().withId(s -> s + "2"), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested.withId(s -> s + "2"),
				params.foldLeft((Expr) tested,
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().name().id(), p.type())))
								.withScope(some(e)).withArgs(listOf(
										methodInvocationExpr(name("mutationBy"))
												.withArgs(listOf(
														p.id().name(),
														p.id().name().withId(s -> s + "2")
												))
								))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> {
					Expr expected = p.id().name().withId(s -> s + "2");
					return junitAssert("assertEquals", expected,
							methodInvocationExpr(p.id().name()).withScope(some(tested.withId(s -> s + "2")))
					);
				}
		));

		stmts = stmts.append(loopFor(10, loopStmts));

		lambdaAccessorsTestClass.generateTestMethod(treeName, stmts);
	}

	private void generateEqualsHashTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = emptyList();
		NodeList<Stmt> loopStmts = emptyList();

		final Name expected = name("expected");
		final Name actual = name("actual");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, expected,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().name().id(), p.type())))
								.withScope(some(e)).withArgs(listOf(p.id().name()))
				)
		));

		loopStmts = loopStmts.append(junitAssert("assertTrue", equals(expected, expected)));
		loopStmts = loopStmts.append(junitAssert("assertFalse", equals(expected, nullLiteralExpr())));

		loopStmts = loopStmts.append(newVar(treeType, actual, factoryCall(treeName)));

		for (FormalParameter param : params) {

			loopStmts = loopStmts.append(junitAssert("assertFalse", equals(expected, actual)));
			loopStmts = loopStmts.append(junitAssert("assertNotEquals", hashCode(expected), hashCode(actual)));

			loopStmts = loopStmts.append(
					expressionStmt(
							assignExpr(actual, Normal,
									methodInvocationExpr(name(propertySetterName(param.id().name().id(), param.type())))
											.withScope(some(actual)).withArgs(listOf(param.id().name()))
							)
					)
			);
		}

		loopStmts = loopStmts.append(junitAssert("assertTrue", equals(expected, actual)));
		loopStmts = loopStmts.append(junitAssert("assertEquals", hashCode(expected), hashCode(actual)));

		stmts = stmts.append(loopFor(10, loopStmts));

		equalsHashTestClass.generateTestMethod(treeName, stmts);
	}

	private MethodInvocationExpr equals(Expr e1, Expr e2) {
		return methodInvocationExpr(name("equals")).withScope(some(e1)).withArgs(listOf(e2));
	}

	private MethodInvocationExpr hashCode(Expr e) {
		return methodInvocationExpr(name("hashCode")).withScope(some(e));
	}

	private void generateKindTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = emptyList();

		stmts = stmts.append(
				junitAssert("assertEquals",
						fieldAccessExpr(name(treeName)).withScope(some(name("Kind"))),
						methodInvocationExpr(name("kind")).withScope(some(factoryCall(treeName))))
		);

		kindTestClass.generateTestMethod(treeName, stmts);
	}

	private Stmt junitAssert(String assertName, Expr... arguments) {
		return expressionStmt(
				methodInvocationExpr(name(assertName))
						.withScope(some(name("Assert")))
						.withArgs(listOf(Arrays.asList(arguments)))
		);
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		accessorsTestClass.write();

		final QualifiedType tType = qType("T");
		final QualifiedType mutationType = qType("Mutation", tType);

		final Name tName = name("t");
		final Name beforeName = name("before");
		final Name afterName = name("after");

		lambdaAccessorsTestClass.addAdditionalMethod(
				methodDecl(mutationType, name("mutationBy"))
						.withModifiers(listOf(Modifier.Private))
						.withTypeParams(listOf(typeParameter(name("T"))))
						.withParams(listOf(
								formalParameter(tType, variableDeclaratorId(beforeName)).withModifiers(listOf(Modifier.Final)),
								formalParameter(tType, variableDeclaratorId(afterName)).withModifiers(listOf(Modifier.Final))
						))
						.withBody(some(blockStmt().withStmts(listOf(
								returnStmt().withExpr(some(
										objectCreationExpr(mutationType)
												.withBody(some(listOf(
														methodDecl(tType, name("mutate"))
																.withModifiers(listOf(Modifier.Public))
																.withParams(listOf(
																		formalParameter(tType, variableDeclaratorId(tName)).withModifiers(listOf(Modifier.Final))
																))
																.withBody(some(blockStmt().withStmts(listOf(
																		junitAssert("assertEquals", beforeName, tName),
																		returnStmt().withExpr(some(afterName))
																))))
												)))
								))
						))))
		);
		lambdaAccessorsTestClass.write();

		equalsHashTestClass.write();

		kindTestClass.write();

		if (false) {
			generateArbitrary();
		}

		return treeSet;
	}


	class TestClass {
		private final String packageName;
		private final String className;

		public TestClass(String packageName, String className) {
			this.packageName = packageName;
			this.className = className;
		}

		private NodeList<MethodDecl> collectedTestMethods = emptyList();

		public NodeList<ImportDecl> imports = listOf(
				importDecl(qualifiedName("org.jlato.tree")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.decl")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.expr")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignExpr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryExpr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryExpr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.name")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.stmt")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.type")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.type.PrimitiveType.Primitive")),
				importDecl(qualifiedName("org.jlato.tree.TreeFactory")).setStatic(true).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.unit.util.Arbitrary")),
				importDecl(qualifiedName("org.junit")).setOnDemand(true),
				importDecl(qualifiedName("org.junit.runner.RunWith")),
				importDecl(qualifiedName("org.junit.runners.JUnit4"))
		);

		public void generateTestMethod(String name, NodeList<Stmt> stmts) {
			MethodDecl testMethod = methodDecl(voidType(), name("test" + name))
					.withModifiers(listOf(
							markerAnnotationExpr(qualifiedName("Test")),
							Modifier.Public
					))
					.withBody(some(blockStmt().withStmts(stmts)));
			collectedTestMethods = collectedTestMethods.append(testMethod);
		}

		public void addAdditionalMethod(MethodDecl methodDecl) {
			collectedTestMethods = collectedTestMethods.append(methodDecl);
		}

		public void write() {
			writeTestClass(packageName, className, imports,
					classDecl(name(className))
							.withModifiers(listOf(
									singleMemberAnnotationExpr(qualifiedName("RunWith"), classExpr(qType("JUnit4"))),
									Modifier.Public
							))
							.withMembers(ms -> ms.appendAll(collectedTestMethods))
			);
		}
	}

	private void writeTestClass(String packageName, String className, NodeList<ImportDecl> imports, ClassDecl classDecl) {
		CompilationUnit cu = compilationUnit(packageDecl(qualifiedName(packageName)))
				.withImports(imports)
				.withTypes(listOf(classDecl));

		try {
			final String path = "../jlato/src/test/java/" + packageName.replace('.', '/') + "/" + className + ".java";
			File file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			final PrintWriter writer = new PrintWriter(new FileWriter(file));
			final Printer printer = new Printer(writer, true, FormattingSettings.Default);
			printer.print(cu);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stmt newVar(Type type, Name name, Expr init) {
		return expressionStmt(
				variableDeclarationExpr(
						localVariableDecl(type)
								.withVariables(listOf(
										variableDeclarator(variableDeclaratorId(name))
												.withInit(some(init))
								))
				)
		);
	}

	private MethodInvocationExpr factoryCall(String name) {
		return methodInvocationExpr(name(lowerCaseFirst(name)));
	}

	private Stmt loopFor(int count, NodeList<Stmt> loopStmts) {
		final Name i = name("i");
		return forStmt(binaryExpr(i, Less, literalExpr(count)), blockStmt().withStmts(loopStmts))
				.withInit(listOf(
						variableDeclarationExpr(
								localVariableDecl(primitiveType(Primitive.Int))
										.withVariables(listOf(
												variableDeclarator(variableDeclaratorId(i))
														.withInit(some(literalExpr(0)))
										))
						)
				))
				.withUpdate(listOf(
						unaryExpr(PostIncrement, i)
				));
	}

	private MethodInvocationExpr arbitraryCall(Name arbitrary, Type type) {
		arbitraryTypes.add(type);
		return methodInvocationExpr(name(arbitraryGenMethodName(type))).withScope(some(arbitrary));
	}

	private String arbitraryGenMethodName(Type type) {
		return "arbitrary" + arbitraryDesc(type);
	}

	private String arbitraryDesc(Type type) {
		String arbitraryDesc;
		if (type instanceof PrimitiveType) {
			arbitraryDesc = upperCaseFirst(((PrimitiveType) type).primitive().toString());
		} else {
			final QualifiedType qualifiedType = (QualifiedType) type;
			String shortName = qualifiedType.name().id();

			if (shortName.startsWith("Node")) {
				shortName = shortName.substring(4);
			}

			if (qualifiedType.typeArgs().isNone()) {
				arbitraryDesc = shortName;
			} else {
				StringBuilder builder = new StringBuilder();
				for (Type ta : qualifiedType.typeArgs().get()) {
					builder.append(arbitraryDesc(ta));
				}
				arbitraryDesc = shortName + builder.toString();
			}
		}
		return arbitraryDesc;
	}

	private void generateArbitrary() {
		final NodeList<ImportDecl> imports = listOf(
				importDecl(qualifiedName("org.jlato.tree")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.decl")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.expr")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignExpr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryExpr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryExpr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.name")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.stmt")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.type")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.tree.type.PrimitiveType.Primitive")),
				importDecl(qualifiedName("org.jlato.tree.TreeFactory")).setStatic(true).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.unit.util.Arbitrary")),
				importDecl(qualifiedName("org.junit")).setOnDemand(true),
				importDecl(qualifiedName("org.junit.runner.RunWith")),
				importDecl(qualifiedName("org.junit.runners.JUnit4"))
		);

		writeTestClass("org.jlato.unit.util", "Arbitrary", imports,
				classDecl(name("Arbitrary"))
						.withModifiers(listOf(Modifier.Public))
						.withMembers(ms -> {
							NodeList<? extends MemberDecl> l = listOf(arbitraryTypes).map(t ->
											methodDecl(t, name(arbitraryGenMethodName(t)))
													.withModifiers(listOf(Modifier.Public))
													.withBody(some(
															blockStmt()
													))
							);
							return ms.appendAll(l);
						})
		);
	}
}
