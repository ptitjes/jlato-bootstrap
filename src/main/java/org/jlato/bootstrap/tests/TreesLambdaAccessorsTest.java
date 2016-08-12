package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.decl.Modifier;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreesLambdaAccessorsTest extends TestPattern {

	@Override
	protected String testName() {
		return "TreesLambdaAccessorsTest";
	}

	@Override
	protected void contributeImports(ImportManager importManager) {
		importManager.addImportByName(ARBITRARY_QUALIFIED);
		importManager.addImportByName(qualifiedName("org.jlato.util.Mutation"));
	}

	@Override
	protected ClassDecl contributeBody(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		decl = super.contributeBody(decl, importManager, arg);

		final QualifiedType tType = qType("T");
		final QualifiedType mutationType = qType("Mutation", tType);

		final Name tName = name("t");
		final Name beforeName = name("before");
		final Name afterName = name("after");

		MethodDecl mutationBy = methodDecl(mutationType, name("mutationBy"))
				.withModifiers(listOf(Modifier.Private))
				.withTypeParams(listOf(typeParameter(name("T"))))
				.withParams(listOf(
						formalParameter(tType).withId(variableDeclaratorId(beforeName)).withModifiers(listOf(Modifier.Final)),
						formalParameter(tType).withId(variableDeclaratorId(afterName)).withModifiers(listOf(Modifier.Final))
				))
				.withBody(blockStmt().withStmts(listOf(
						returnStmt().withExpr(
								objectCreationExpr(mutationType)
										.withBody(listOf(
												methodDecl(tType, name("mutate"))
														.withModifiers(listOf(Modifier.Public))
														.withParams(listOf(
																formalParameter(tType).withId(variableDeclaratorId(tName))
																		.withModifiers(listOf(Modifier.Final))
														))
														.withBody(blockStmt().withStmts(listOf(
																junitAssert("assertEquals", beforeName, tName),
																returnStmt().withExpr(afterName)
														)))
										))
						)
				)));

		return decl.withMembers(ms -> ms.append(mutationBy));
	}

	@Override
	protected boolean excluded(TreeClassDescriptor descriptor) {
		return descriptor.name.id().equals("LiteralExpr") || descriptor.parameters.isEmpty();
	}

	@Override
	protected NodeList<Stmt> testStatementsFor(TreeClassDescriptor descriptor, ImportManager importManager) {
		NodeList<FormalParameter> params = descriptor.parameters;

		AllDescriptors.addImports(importManager, descriptor.interfaceType());
		AllDescriptors.addImports(importManager, params.map(p -> p.type()));

		NodeList<Stmt> stmts = emptyList();
		NodeList<Stmt> loopStmts = emptyList();

		final Name tested = name("t");

		stmts = stmts.append(newVarStmt(ARBITRARY_TYPE, ARBITRARY_VAR, objectCreationExpr(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(params.map(p -> newVarStmt(p.type(), p.id().get().name(), Utils.arbitraryCall(ARBITRARY_VAR, p.type()))));
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), tested,
				params.foldLeft(factoryCall(descriptor, importManager),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().get().name().id(), p.type())))
								.withScope(e).withArgs(listOf(p.id().get().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> junitAssert("assertEquals", p.id().get().name(),
						methodInvocationExpr(p.id().get().name()).withScope(tested))
		));

		loopStmts = loopStmts.appendAll(params.map(p -> newVarStmt(p.type(), p.id().get().name().withId(s -> s + "2"), Utils.arbitraryCall(ARBITRARY_VAR, p.type()))));
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), tested.withId(s -> s + "2"),
				params.<Expr>foldLeft(tested,
						(e, p) -> methodInvocationExpr(name(propertySetterName(p.id().get().name().id(), p.type())))
								.withScope(e).withArgs(listOf(
										methodInvocationExpr(name("mutationBy"))
												.withArgs(listOf(
														p.id().get().name(),
														p.id().get().name().withId(s -> s + "2")
												))
								))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p -> {
					Expr expected = p.id().get().name().withId(s -> s + "2");
					return junitAssert("assertEquals", expected,
							methodInvocationExpr(p.id().get().name()).withScope(tested.withId(s -> s + "2"))
					);
				}
		));

		stmts = stmts.append(loopFor(10, loopStmts));
		return stmts;
	}
}
