package org.jlato.bootstrap;

import org.jlato.parser.ParseException;
import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.ConstructorDecl;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.type.QualifiedType;

import java.util.Iterator;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class NodeStatesAlternativeMake extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {

		Iterator<ClassDecl> iterator = decl.findAll(stateClassMatcher()).iterator();
		if (!iterator.hasNext()) return decl;


		QualifiedType stateType = stateType(decl);
		ClassDecl stateClass = iterator.next();

		ConstructorDecl stateConstructor = stateClass.findAll(constructors(c -> true)).iterator().next();

		MethodDecl makeMethod = decl.findAll(makeMethodMatcher).iterator().next();
		ObjectCreationExpr stateCreationExpr = (ObjectCreationExpr) makeMethod.findAll(stateCreationMatcher).iterator().next();
		NodeList<Expr> oldCreationArgs = stateCreationExpr.args();

		ConstructorDecl constructor = decl.findAll(publicConstructorMatcher).iterator().next();
		MethodInvocationExpr makeCall = (MethodInvocationExpr) constructor.findAll(makeCallMatcher).iterator().next();
		NodeList<Expr> oldMakeArgs = makeCall.args();

		decl = decl.forAll(makeMethodMatcher, (m, s) -> m
						.withParams(stateConstructor.params())
						.withBody(some(blockStmt().withStmts(NodeList.of(
								returnStmt().withExpr(some(
										objectCreationExpr().withType(qType("STree", stateType))
												.withArgs(NodeList.of(
														stateCreationExpr.withArgs(oldMakeArgs)
												))
								))
						))))
		).forAll(publicConstructorMatcher, (c, s) -> c.withBody(blockStmt().withStmts(NodeList.of(
						explicitConstructorInvocationStmt().setThis(false)
								.withArgs(NodeList.of(
										objectCreationExpr().withType(qType("SLocation", stateType))
												.withArgs(NodeList.of(
														makeCall.withArgs(oldCreationArgs)
												))
								))
				)))
		);

		return decl;
	}
}
