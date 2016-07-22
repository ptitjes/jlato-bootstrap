package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.stmt.*;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreesKindTest extends TestPattern {

	@Override
	protected String testName() {
		return "TreesKindTest";
	}

	@Override
	protected void contributeImports(ImportManager importManager) {
		importManager.addImportByName(qualifiedName("org.jlato.tree.Kind"));
		importManager.addImportByName(qualifiedName("org.jlato.internal.td.TDTree"));
	}

	@Override
	protected boolean excluded(TreeClassDescriptor descriptor) {
		return descriptor.name.id().equals("LiteralExpr");
	}

	@Override
	protected NodeList<Stmt> testStatementsFor(TreeClassDescriptor descriptor, ImportManager importManager) {
		return listOf(
				junitAssert("assertEquals",
						fieldAccessExpr(descriptor.name).withScope(name("Kind")),
						methodInvocationExpr(name("kind")).withScope(
								factoryCall(descriptor, importManager)
						)
				),
				junitAssert("assertEquals",
						fieldAccessExpr(descriptor.name).withScope(name("Kind")),
						methodInvocationExpr(name("kind")).withScope(
								methodInvocationExpr(name("stateOf")).withScope(
										name("TDTree")
								).withArgs(listOf(
										factoryCall(descriptor, importManager)
								))
						)
				)
		);
	}
}
