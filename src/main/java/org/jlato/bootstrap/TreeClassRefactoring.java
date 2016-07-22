package org.jlato.bootstrap;

import org.jlato.parser.ParseException;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;

import java.util.function.Function;

/**
 * @author Didier Villevalois
 */
public abstract class TreeClassRefactoring extends Utils {

	public NodeMap<CompilationUnit> initialize(NodeMap<CompilationUnit> nodeMap, TreeTypeHierarchy hierarchy) {
		return nodeMap;
	}

	public InterfaceDecl refactorTreeInterface(NodeMap<CompilationUnit> nodeMap, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
		return decl;
	}

	public ClassDecl refactorTreeClass(NodeMap<CompilationUnit> nodeMap, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
		return decl;
	}

	public NodeList<ImportDecl> addImports(NodeList<ImportDecl> imports) {
		return imports;
	}

	public NodeMap<CompilationUnit> finish(NodeMap<CompilationUnit> nodeMap, TreeTypeHierarchy hierarchy) {
		return nodeMap;
	}
}
