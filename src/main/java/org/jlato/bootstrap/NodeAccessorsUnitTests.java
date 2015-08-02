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
import java.util.HashSet;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;
import static org.jlato.tree.expr.AssignOp.Normal;
import static org.jlato.tree.expr.BinaryOp.Less;
import static org.jlato.tree.expr.UnaryOp.PostIncrement;

/**
 * @author Didier Villevalois
 */
public class NodeAccessorsUnitTests extends TreeClassRefactoring {

	public static final Name STATE_NAME = new Name("State");
	public static final QualifiedType ARBITRARY_TYPE = qType("Arbitrary");
	public static final Name ARBITRARY_NAME = new Name("arbitrary");

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
			NodeList<FormalParameter> preprocessed = NodeList.empty();
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
		NodeList<Stmt> stmts = NodeList.empty();
		NodeList<Stmt> loopStmts = NodeList.empty();

		final Name tested = new Name("t");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr().withType(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr()
								.withScope(some(e))
								.withName(new Name(propertySetterName(p.id().name().id(), p.type())))
								.withArgs(NodeList.of(p.id().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> junitAssert("assertEquals", p.id().name(), methodInvocationExpr()
						.withScope(some(tested))
						.withName(p.id().name()))
		));

		stmts = stmts.append(loopFor(10, loopStmts));

		accessorsTestClass.generateTestMethod(treeName, stmts);
	}

	private void generateLambdaAccessorsTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = NodeList.empty();
		NodeList<Stmt> loopStmts = NodeList.empty();

		final Name tested = new Name("t");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr().withType(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr()
								.withScope(some(e))
								.withName(new Name(propertySetterName(p.id().name().id(), p.type())))
								.withArgs(NodeList.of(p.id().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> junitAssert("assertEquals", p.id().name(), methodInvocationExpr()
						.withScope(some(tested))
						.withName(p.id().name()))
		));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name().withId(s -> s + "2"), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, tested.withId(s -> s + "2"),
				params.foldLeft((Expr) tested,
						(e, p) -> methodInvocationExpr()
								.withScope(some(e))
								.withName(new Name(propertySetterName(p.id().name().id(), p.type())))
								.withArgs(NodeList.of(
										methodInvocationExpr()
												.withName(new Name("mutationBy"))
												.withArgs(NodeList.of(
														p.id().name(),
														p.id().name().withId(s -> s + "2")
												))
								))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> {
					Expr expected = p.id().name().withId(s -> s + "2");
					return junitAssert("assertEquals", expected, methodInvocationExpr()
							.withScope(some(tested.withId(s -> s + "2")))
							.withName(p.id().name()));
				}
		));

		stmts = stmts.append(loopFor(10, loopStmts));

		lambdaAccessorsTestClass.generateTestMethod(treeName, stmts);
	}

	private void generateEqualsHashTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = NodeList.empty();
		NodeList<Stmt> loopStmts = NodeList.empty();

		final Name expected = new Name("expected");
		final Name actual = new Name("actual");

		stmts = stmts.append(newVar(ARBITRARY_TYPE, ARBITRARY_NAME, objectCreationExpr().withType(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVar(p.type(), p.id().name(), arbitraryCall(ARBITRARY_NAME, p.type()))));
		loopStmts = loopStmts.append(newVar(treeType, expected,
				params.foldLeft(factoryCall(treeName),
						(e, p) -> methodInvocationExpr()
								.withScope(some(e))
								.withName(new Name(propertySetterName(p.id().name().id(), p.type())))
								.withArgs(NodeList.of(p.id().name()))
				)
		));

		loopStmts = loopStmts.append(junitAssert("assertTrue", equals(expected, expected)));
		loopStmts = loopStmts.append(junitAssert("assertFalse", equals(expected, LiteralExpr.nullLiteral())));

		loopStmts = loopStmts.append(newVar(treeType, actual, factoryCall(treeName)));

		for (FormalParameter param : params) {

			loopStmts = loopStmts.append(junitAssert("assertFalse", equals(expected, actual)));
			loopStmts = loopStmts.append(junitAssert("assertNotEquals", hashCode(expected), hashCode(actual)));

			loopStmts = loopStmts.append(
					expressionStmt().withExpr(
							assignExpr().withTarget(actual).withOp(Normal)
									.withValue(
											methodInvocationExpr()
													.withScope(some(actual))
													.withName(new Name(propertySetterName(param.id().name().id(), param.type())))
													.withArgs(NodeList.of(param.id().name()))
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
		return methodInvocationExpr().withScope(some(e1)).withName(new Name("equals")).withArgs(NodeList.of(e2));
	}

	private MethodInvocationExpr hashCode(Expr e) {
		return methodInvocationExpr().withScope(some(e)).withName(new Name("hashCode"));
	}

	private void generateKindTestMethod(String treeName, QualifiedType treeType, NodeList<FormalParameter> params) {
		NodeList<Stmt> stmts = NodeList.empty();

		stmts = stmts.append(
				junitAssert("assertEquals", fieldAccessExpr().withScope(some(new Name("Kind"))).withName(new Name(treeName)), methodInvocationExpr()
						.withScope(some(factoryCall(treeName)))
						.withName(new Name("kind")))
		);

		kindTestClass.generateTestMethod(treeName, stmts);
	}

	private Stmt junitAssert(String assertName, Expr... arguments) {
		return expressionStmt().withExpr(
				methodInvocationExpr()
						.withScope(some(new Name("Assert")))
						.withName(new Name(assertName))
						.withArgs(new NodeList<Expr>(arguments))
		);
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		accessorsTestClass.write();

		final QualifiedType tType = qType("T");
		final QualifiedType mutationType = qType("Mutation", tType);

		final Name tName = new Name("t");
		final Name beforeName = new Name("before");
		final Name afterName = new Name("after");

		lambdaAccessorsTestClass.addAdditionalMethod(
				methodDecl()
						.withModifiers(NodeList.of(Modifier.Private))
						.withTypeParams(NodeList.of(typeParameter().withName(new Name("T"))))
						.withType(mutationType)
						.withName(new Name("mutationBy"))
						.withParams(NodeList.of(
								formalParameter().withModifiers(NodeList.of(Modifier.Final)).withType(tType).withId(variableDeclaratorId().withName(beforeName)),
								formalParameter().withModifiers(NodeList.of(Modifier.Final)).withType(tType).withId(variableDeclaratorId().withName(afterName))
						))
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										objectCreationExpr()
												.withType(mutationType)
												.withBody(some(NodeList.of(
														methodDecl()
																.withModifiers(NodeList.of(Modifier.Public))
																.withType(tType)
																.withName(new Name("mutate"))
																.withParams(NodeList.of(
																		formalParameter().withModifiers(NodeList.of(Modifier.Final)).withType(tType).withId(variableDeclaratorId().withName(tName))
																))
																.withBody(some(blockStmt().withStmts(NodeList.of(
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

		private NodeList<MethodDecl> collectedTestMethods = NodeList.empty();

		public NodeList<ImportDecl> imports = NodeList.of(
				importDecl().withName(QualifiedName.of("org.jlato.tree")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.decl")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.AssignExpr.AssignOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.BinaryExpr.BinaryOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.UnaryExpr.UnaryOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.name")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.stmt")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.type")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.type.PrimitiveType.Primitive")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.TreeFactory")).setStatic(true).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.unit.util.Arbitrary")),
				importDecl().withName(QualifiedName.of("org.junit")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.junit.runner.RunWith")),
				importDecl().withName(QualifiedName.of("org.junit.runners.JUnit4"))
		);

		public void generateTestMethod(String name, NodeList<Stmt> stmts) {
			MethodDecl testMethod = methodDecl()
					.withModifiers(NodeList.of(
							markerAnnotationExpr().withName(QualifiedName.of("Test")),
							Modifier.Public
					))
					.withType(voidType())
					.withName(new Name("test" + name))
					.withBody(some(blockStmt().withStmts(stmts)));
			collectedTestMethods = collectedTestMethods.append(testMethod);
		}

		public void addAdditionalMethod(MethodDecl methodDecl) {
			collectedTestMethods = collectedTestMethods.append(methodDecl);
		}

		public void write() {
			writeTestClass(packageName, className, imports,
					classDecl()
							.withModifiers(NodeList.of(
									singleMemberAnnotationExpr()
											.withName(QualifiedName.of("RunWith"))
											.withMemberValue(classExpr().withType(qType("JUnit4"))),
									Modifier.Public
							))
							.withName(new Name(className))
							.withMembers(ms -> ms.appendAll(collectedTestMethods))
			);
		}
	}

	private void writeTestClass(String packageName, String className, NodeList<ImportDecl> imports, ClassDecl classDecl) {
		CompilationUnit cu = compilationUnit()
				.withPackageDecl(packageDecl().withName(QualifiedName.of(packageName)))
				.withImports(imports)
				.withTypes(NodeList.of(
						classDecl
				));

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
		return expressionStmt().withExpr(
				variableDeclarationExpr().withDeclaration(
						localVariableDecl().withType(type)
								.withVariables(NodeList.of(
										variableDeclarator()
												.withId(variableDeclaratorId().withName(name))
												.withInit(some(init))
								))
				)
		);
	}

	private MethodInvocationExpr factoryCall(String name) {
		return methodInvocationExpr().withName(new Name(lowerCaseFirst(name)));
	}

	private Stmt loopFor(int count, NodeList<Stmt> loopStmts) {
		final Name i = new Name("i");
		return forStmt()
				.withInit(NodeList.of(
						variableDeclarationExpr().withDeclaration(
								localVariableDecl()
										.withType(primitiveType().withPrimitive(Primitive.Int))
										.withVariables(NodeList.of(
												variableDeclarator().withId(variableDeclaratorId().withName(i))
														.withInit(some(LiteralExpr.of(0)))
										))
						)
				))
				.withCompare(
						binaryExpr().withLeft(i).withOp(Less).withRight(LiteralExpr.of(count))
				)
				.withUpdate(NodeList.of(
						unaryExpr().withOp(PostIncrement).withExpr(i)
				))
				.withBody(blockStmt().withStmts(loopStmts));
	}

	private MethodInvocationExpr arbitraryCall(Name arbitrary, Type type) {
		arbitraryTypes.add(type);
		return methodInvocationExpr()
				.withScope(some(arbitrary))
				.withName(new Name(arbitraryGenMethodName(type)));
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
		final NodeList<ImportDecl> imports = NodeList.of(
				importDecl().withName(QualifiedName.of("org.jlato.tree")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.decl")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.AssignExpr.AssignOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.BinaryExpr.BinaryOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.expr.UnaryExpr.UnaryOp")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.name")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.stmt")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.type")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.tree.type.PrimitiveType.Primitive")),
				importDecl().withName(QualifiedName.of("org.jlato.tree.TreeFactory")).setStatic(true).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.jlato.unit.util.Arbitrary")),
				importDecl().withName(QualifiedName.of("org.junit")).setOnDemand(true),
				importDecl().withName(QualifiedName.of("org.junit.runner.RunWith")),
				importDecl().withName(QualifiedName.of("org.junit.runners.JUnit4"))
		);

		writeTestClass("org.jlato.unit.util", "Arbitrary", imports,
				classDecl()
						.withModifiers(NodeList.of(Modifier.Public))
						.withName(new Name("Arbitrary"))
						.withMembers(ms -> {
							NodeList<? extends MemberDecl> l = NodeList.of(arbitraryTypes).map(t ->
																	methodDecl()
																			.withModifiers(NodeList.of(Modifier.Public))
																			.withType(t)
																			.withName(new Name(arbitraryGenMethodName(t)))
																			.withBody(some(
																					blockStmt()
																			))
													);
							return ms.appendAll(l);
						})
		);
	}
}
