package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.*;
import org.jlato.tree.stmt.*;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreesAccessorsTest extends TestPattern {

	@Override
	protected String testName() {
		return "TreesAccessorsTest";
	}

	@Override
	protected void contributeImports(ImportManager importManager) {
		importManager.addImportByName(ARBITRARY_QUALIFIED);
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

		loopStmts = loopStmts.appendAll(
				params.map(p -> newVarStmt(p.type(), p.id().name(), arbitraryCall(ARBITRARY_VAR, p.type())))
		);
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), tested,
				params.foldLeft(factoryCall(descriptor, importManager),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
								.withScope(some(e)).withArgs(listOf(p.id().name()))
				)
		));

		loopStmts = loopStmts.appendAll(params.map(p ->
				junitAssert("assertEquals",
						p.id().name(),
						methodInvocationExpr(p.id().name()).withScope(some(tested))
				)
		));

		if (params.exists(p -> nameFieldType(p.type()))) {
			NodeList<FormalParameter> nameParameters = params.filter(p -> nameFieldType(p.type()));

			loopStmts = loopStmts.append(assignVarStmt(descriptor.interfaceType(), tested,
					nameParameters.foldLeft((Expr) tested,
							(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
									.withScope(some(e)).withArgs(listOf(
											methodInvocationExpr(name("id")).withScope(some(p.id().name()))
									))
					)
			));

			loopStmts = loopStmts.appendAll(nameParameters.map(p ->
					junitAssert("assertEquals",
							p.id().name(),
							methodInvocationExpr(p.id().name()).withScope(some(tested))
					)
			));
		}

		stmts = stmts.append(loopFor(10, loopStmts));
		return stmts;
	}
}
