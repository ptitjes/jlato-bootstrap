package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;
import org.jlato.tree.stmt.*;

import static org.jlato.tree.Trees.*;
import static org.jlato.tree.expr.AssignOp.*;

/**
 * @author Didier Villevalois
 */
public class TreesEqualsHashCodeTest extends TestPattern {

	@Override
	protected String testName() {
		return "TreesEqualsHashCodeTest";
	}

	@Override
	protected void contributeImports(ImportManager importManager) {
		importManager.addImportByName(ARBITRARY_QUALIFIED);
	}

	@Override
	protected boolean excluded(TreeClassDescriptor descriptor) {
		return descriptor.name.id().equals("LiteralExpr");
	}

	@Override
	protected NodeList<Stmt> testStatementsFor(TreeClassDescriptor descriptor, ImportManager importManager) {
		NodeList<FormalParameter> params = descriptor.parameters;

		AllDescriptors.addImports(importManager, descriptor.interfaceType());
		AllDescriptors.addImports(importManager, params.map(p -> p.type()));

		NodeList<Stmt> stmts = emptyList();
		NodeList<Stmt> loopStmts = emptyList();

		final Name expected = name("expected");
		final Name actual = name("actual");

		stmts = stmts.append(newVarStmt(ARBITRARY_TYPE, ARBITRARY_VAR, objectCreationExpr(ARBITRARY_TYPE)));

		loopStmts = loopStmts.appendAll(
				params.map(p -> newVarStmt(p.type(), p.id().name(), arbitraryCall(ARBITRARY_VAR, p.type())))
		);
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), expected,
				params.foldLeft(factoryCall(descriptor, importManager),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
								.withScope(some(e)).withArgs(listOf(p.id().name()))
				)
		));

		loopStmts = loopStmts.append(junitAssert("assertEquals", expected, expected));
		loopStmts = loopStmts.append(junitAssert("assertNotEquals", expected, nullLiteralExpr()));

		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), actual,
				factoryCall(descriptor, importManager)
		));

		for (FormalParameter param : params) {

			loopStmts = loopStmts.append(junitAssert("assertNotEquals", expected, actual));
			loopStmts = loopStmts.append(junitAssert("assertNotEquals", hashCode(expected), hashCode(actual)));

			loopStmts = loopStmts.append(
					expressionStmt(
							assignExpr(actual, Normal,
									methodInvocationExpr(name(propertySetterName(param)))
											.withScope(some(actual)).withArgs(listOf(param.id().name()))
							)
					)
			);
		}

		loopStmts = loopStmts.append(junitAssert("assertEquals", expected, actual));
		loopStmts = loopStmts.append(junitAssert("assertEquals", hashCode(expected), hashCode(actual)));

		if (params.size() == 0) stmts = loopStmts;
		else stmts = stmts.append(loopFor(10, loopStmts));

		return stmts;
	}
}
