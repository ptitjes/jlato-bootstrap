package org.jlato.bootstrap.tests;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;
import org.jlato.tree.stmt.*;
import org.jlato.tree.type.*;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public abstract class TestPattern extends TypePattern.OfClass<TreeClassDescriptor[]> {

	public static final Name ARBITRARY = name("Arbitrary");
	public static final QualifiedName ARBITRARY_QUALIFIED = qualifiedName("org.jlato.unit.util." + ARBITRARY);
	public static final QualifiedType ARBITRARY_TYPE = qualifiedType(ARBITRARY);
	public static final Name ARBITRARY_VAR = name("arbitrary");

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "@RunWith(JUnit4.class)\npublic class " + testName() + " { ..$_ }";
	}

	protected abstract String testName();

	@Override
	protected ClassDecl contributeBody(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		importManager.addImports(listOf(
				importDecl(qualifiedName("org.junit")).setOnDemand(true),
				importDecl(qualifiedName("org.junit.runner.RunWith")),
				importDecl(qualifiedName("org.junit.runners.JUnit4"))
		));

		contributeImports(importManager);

		NodeList<MemberDecl> members = Trees.emptyList();
		for (TreeClassDescriptor descriptor : arg) {
			if (excluded(descriptor)) continue;

			members = members.append(methodDecl(voidType(), name("test" + descriptor.name))
					.withModifiers(listOf(
							markerAnnotationExpr(qualifiedName("Test")),
							Modifier.Public
					))
					.withBody(blockStmt().withStmts(
							testStatementsFor(descriptor, importManager)
					)));
		}

		return classDecl(name(testName()))
				.withModifiers(listOf(
						singleMemberAnnotationExpr(qualifiedName("RunWith"), classExpr(qType("JUnit4"))),
						Modifier.Public
				))
				.withMembers(members);
	}

	protected boolean excluded(TreeClassDescriptor descriptor) {
		return false;
	}

	protected abstract NodeList<Stmt> testStatementsFor(TreeClassDescriptor descriptor, ImportManager importManager);

	protected abstract void contributeImports(ImportManager importManager);

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor[] arg) {
		return null;
	}
}
