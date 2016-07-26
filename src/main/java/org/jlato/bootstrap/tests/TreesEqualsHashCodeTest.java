package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.expr.UnaryOp;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.PrimitiveType;

import static org.jlato.tree.Trees.*;
import static org.jlato.tree.expr.AssignOp.Normal;

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
				params.map(p -> {
					MethodInvocationExpr arbitraryCall = arbitraryCall(ARBITRARY_VAR, p.type());
					boolean booleanType = p.type().kind() == Kind.PrimitiveType &&
							((PrimitiveType) p.type()).primitive() == Primitive.Boolean;
					Expr defaultValue = descriptor.defaultValueFor(p);
					boolean negated = booleanType && defaultValue != null && defaultValue.equals(literalExpr(true));

					return newVarStmt(p.type(), p.id().name(),
							negated ? unaryExpr(UnaryOp.Not, arbitraryCall) : arbitraryCall
					);
				})
		);
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), expected,
				params.foldLeft(factoryCall(descriptor, importManager),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
								.withScope(e).withArgs(listOf(p.id().name()))
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
											.withScope(actual).withArgs(listOf(param.id().name()))
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
