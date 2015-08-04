package org.jlato.bootstrap;

import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.Name;

import static org.jlato.tree.TreeFactory.name;

/**
 * @author Didier Villevalois
 */
public class NodeStatesRemoval extends TreeClassRefactoring {

	public static final Name STATE_NAME = name("State");

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public InterfaceDecl refactorTreeInterface(TreeSet<CompilationUnit> treeSet, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) {

		NodeList<MemberDecl> newMembers = NodeList.empty();

		for (MemberDecl member : decl.members()) {
			if (stateInterfaceMatcher().match(member) != null) continue;

			newMembers = newMembers.append(member);
		}

		return decl.withMembers(newMembers);
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {

		NodeList<MemberDecl> newMembers = NodeList.empty();

		for (MemberDecl member : decl.members()) {
			if (traversalConstantMatcher().match(member) != null) continue;
			if (propertyConstantMatcher().match(member) != null) continue;
			if (stateClassMatcher().match(member) != null) continue;

			newMembers = newMembers.append(member);
		}

		return decl.withMembers(newMembers);
	}
}
