/*
 * Copyright (C) 2015 Didier Villevalois.
 *
 * This file is part of JLaTo.
 *
 * JLaTo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JLaTo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JLaTo.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jlato.bootstrap;

import com.github.andrewoma.dexx.collection.Iterable;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.parser.ParseException;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.FormattingSettings;
import org.jlato.tree.*;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Refactoring {

	public static void main(String[] args) throws IOException, ParseException {
		new Refactoring().generate();
	}

	public void generate() throws IOException, ParseException {
		TreeClassRefactoring refactoring = new ExtractTreeDescriptors();
//		TreeClassRefactoring refactoring = new BootstrapUnitTests();

		refactorTreeClasses(refactoring);
	}

	public void refactorTreeClasses(TreeClassRefactoring refactoring) throws IOException, ParseException {
		Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));
		File rootDirectory = new File("../jlato/src/main/java");
		TreeSet<CompilationUnit> treeSet = parser.parseAll(rootDirectory, "UTF-8");

		TreeTypeHierarchy hierarchy = new TreeTypeHierarchy();
		hierarchy.initialize(treeSet);

		treeSet = refactoring.initialize(treeSet, hierarchy);

		for (String path : sorted(treeSet.paths())) {
			CompilationUnit cu = treeSet.get(path);

			final TypeDecl typeDecl = cu.types().get(0);
			TypeDecl newDecl = null;
			if (typeDecl instanceof InterfaceDecl) {
				final InterfaceDecl interfaceDecl = (InterfaceDecl) typeDecl;
				final Name name = interfaceDecl.name();

				if (hierarchy.isInterface(name) && !hierarchy.isTreeInterface(name)) {
					newDecl = refactoring.refactorTreeInterface(treeSet, path, interfaceDecl, hierarchy);
				}
			} else if (typeDecl instanceof ClassDecl) {
				final ClassDecl classDecl = (ClassDecl) typeDecl;
				final Name name = classDecl.name();

				if (hierarchy.isClass(name)) {
					newDecl = refactoring.refactorTreeClass(treeSet, path, classDecl, hierarchy);
				}
			}
			if (newDecl != null && newDecl != typeDecl) {
				CompilationUnit newCU = (CompilationUnit) newDecl.parent().parent();
				newCU = newCU.withImports(refactoring.addImports(newCU.imports()));
				treeSet = treeSet.put(path, newCU);
			}
		}

		treeSet = refactoring.finish(treeSet, hierarchy);

		treeSet.updateOnDisk(false, FormattingSettings.Default);
	}

	private java.lang.Iterable<String> sorted(Iterable<String> paths) {
		List<String> sorted = new java.util.ArrayList<>();
		for (String path : paths) {
			sorted.add(path);
		}
		Collections.sort(sorted);
		return sorted;
	}

	private static final List<String> TREE_SUPERCLASSES = Arrays.asList(AllDescriptors.TD_TREE.id());

	private static final List<String> EXCLUDED_CLASSES = Arrays.asList("NodeList", "NodeOption", "NodeEither", "TreeSet");

	public static boolean filterTreeClass(String name, NodeOption<QualifiedType> superclass) {
		if (superclass.isNone()) return false;
		final String superclassName = superclass.get().name().id();
		return !EXCLUDED_CLASSES.contains(name) && TREE_SUPERCLASSES.contains(superclassName);
	}
}
