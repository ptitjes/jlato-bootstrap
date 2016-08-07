package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.pattern.Quotes;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

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

		stmts = stmts.append(newVarStmt(ARBITRARY_TYPE, ARBITRARY_VAR, objectCreationExpr(ARBITRARY_TYPE).withArgs(listOf(name("true")))));

		loopStmts = loopStmts.appendAll(
				params.map(p -> newVarStmt(p.type(), p.id().get().name(), arbitraryCall(ARBITRARY_VAR, p.type())))
		);

		// Using factory methods without argument
		loopStmts = loopStmts.append(newVarStmt(descriptor.interfaceType(), tested,
				params.foldLeft(factoryCall(descriptor, importManager),
						(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
								.withScope(e/*.insertNewLineAfter()*/).withArgs(listOf(p.id().get().name()))
				)
		).insertNewLineBefore().insertLeadingComment("Use factory method without argument"));

		loopStmts = loopStmts.appendAll(params.map(p ->
				junitAssert("assertEquals",
						p.id().get().name(),
						methodInvocationExpr(p.id().get().name()).withScope(tested)
				)
		));

		// Using factory methods with arguments
		if (!noNullsFormHasNoParams(descriptor)) {
			loopStmts = loopStmts.append(assignVarStmt(descriptor.interfaceType(), tested,
					params.filter(p -> descriptor.hasKnownValueFor(p)).foldLeft(
							factoryCall(descriptor, importManager).withArgs(
									params.filter(p -> !descriptor.hasKnownValueFor(p))
											.map(p -> p.id().get().name())
							),
							(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
									.withScope(e/*.insertNewLineAfter()*/).withArgs(listOf(p.id().get().name()))
					)
			).insertNewLineBefore().insertLeadingComment("Use factory method with arguments"));

			loopStmts = loopStmts.appendAll(params.map(p ->
					junitAssert("assertEquals",
							p.id().get().name(),
							methodInvocationExpr(p.id().get().name()).withScope(tested)
					)
			));
		}

		if (params.exists(p -> nameFieldType(p.type()))) {
			NodeList<FormalParameter> nameParameters = params.filter(p -> nameFieldType(p.type()));

			loopStmts = loopStmts.append(assignVarStmt(descriptor.interfaceType(), tested,
					nameParameters.foldLeft((Expr) tested,
							(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
									.withScope(e).withArgs(listOf(
											methodInvocationExpr(name("id")).withScope(p.id().get().name())
									))
					)
			).insertNewLineBefore().insertLeadingComment("Use specialized name mutators"));

			loopStmts = loopStmts.appendAll(nameParameters.map(p ->
					junitAssert("assertEquals",
							p.id().get().name(),
							methodInvocationExpr(p.id().get().name()).withScope(tested)
					)
			));
		}

		if (params.exists(p -> optionFieldType(p.type()))) {
			NodeList<FormalParameter> optionParameters = params.filter(p -> optionFieldType(p.type()));

			loopStmts = loopStmts.append(assignVarStmt(descriptor.interfaceType(), tested,
					optionParameters.foldLeft((Expr) tested,
							(e, p) -> methodInvocationExpr(name(propertySetterName(p)))
									.withScope(e).withArgs(listOf(
											methodInvocationExpr(name("get")).withScope(p.id().get().name())
									))
					)
			).insertNewLineBefore().insertLeadingComment("Use specialized NodeOption.some() mutators"));

			loopStmts = loopStmts.appendAll(optionParameters.map(p ->
					junitAssert("assertEquals",
							p.id().get().name(),
							methodInvocationExpr(p.id().get().name()).withScope(tested)
					)
			));

			loopStmts = loopStmts.append(assignVarStmt(descriptor.interfaceType(), tested,
					optionParameters.foldLeft((Expr) tested,
							(e, p) -> methodInvocationExpr(name(propertySetterName(p, "No")))
									.withScope(e).withArgs(emptyList())
					)
			).insertNewLineBefore().insertLeadingComment("Use specialized NodeOption.none() mutators"));

			loopStmts = loopStmts.appendAll(optionParameters.map(p ->
					junitAssert("assertEquals",
							Quotes.expr("Trees.<" + firstTypeArg(p) + ">none()").build(),
							methodInvocationExpr(p.id().get().name()).withScope(tested)
					)
			));
		}

		if (params.exists(p -> eitherFieldType(p.type()))) {
			NodeList<FormalParameter> optionParameters = params.filter(p -> eitherFieldType(p.type()));

			loopStmts = loopStmts.appendAll(optionParameters.map(p ->
					ifStmt(methodInvocationExpr(name("isLeft")).withScope(p.id().get().name()),
							assignVarStmt(descriptor.interfaceType(), tested,
									methodInvocationExpr(name(propertySetterName(p)))
											.withScope(tested).withArgs(listOf(
											methodInvocationExpr(name("left")).withScope(p.id().get().name())
									))
							)
					).withElseStmt(
							assignVarStmt(descriptor.interfaceType(), tested,
									methodInvocationExpr(name(propertySetterName(p)))
											.withScope(tested).withArgs(listOf(
											methodInvocationExpr(name("right")).withScope(p.id().get().name())
									))
							)
					).insertNewLineBefore().insertLeadingComment("Use specialized NodeEither mutators")

			));

			loopStmts = loopStmts.appendAll(optionParameters.map(p ->
					junitAssert("assertEquals",
							p.id().get().name(),
							methodInvocationExpr(p.id().get().name()).withScope(tested)
					)
			));
		}

		stmts = stmts.append(loopFor(10, loopStmts));
		return stmts;
	}

	private Type firstTypeArg(FormalParameter p) {
		return ((QualifiedType) p.type()).typeArgs().get().first();
	}
}
