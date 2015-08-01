package org.jlato.bootstrap;

import org.jlato.parser.ParseException;
import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class NodeStatesPublicCons extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {

		QualifiedType treeType = qualifiedType().withName(decl.name());
		QualifiedType stateType = stateType(decl);

		decl = decl.forAll(stateClassMatcher(), (stateClass, s) ->
						stateClass.forAll(constructors(c -> true), (c, s2) ->
								c.withModifiers(NodeList.of(Modifier.Public))
						)
		);

		return decl;
	}
}
