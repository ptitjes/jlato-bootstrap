package org.jlato.bootstrap;

import org.jlato.parser.ParseException;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;

import java.util.function.Function;

/**
 * @author Didier Villevalois
 */
public abstract class TreeClassRefactoring extends Utils {

	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return treeSet;
	}

	public InterfaceDecl refactorTreeInterface(TreeSet<CompilationUnit> treeSet, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
		return decl;
	}

	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
		return decl;
	}

	public NodeList<ImportDecl> addImports(NodeList<ImportDecl> imports) {
		return imports;
	}

	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return treeSet;
	}
}
