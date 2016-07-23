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
import org.jlato.printer.Printer;
import org.jlato.tree.NodeMap;
import org.jlato.tree.NodeOption;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
		NodeMap<CompilationUnit> nodeMap = parser.parseAll(rootDirectory, "UTF-8");

		TreeTypeHierarchy hierarchy = new TreeTypeHierarchy();
		hierarchy.initialize(nodeMap);

		nodeMap = refactoring.initialize(nodeMap, hierarchy);

		for (String path : sorted(nodeMap.keys())) {
			CompilationUnit cu = nodeMap.get(path);

			final TypeDecl typeDecl = cu.types().get(0);
			TypeDecl newDecl = null;
			if (typeDecl instanceof InterfaceDecl) {
				final InterfaceDecl interfaceDecl = (InterfaceDecl) typeDecl;
				final Name name = interfaceDecl.name();

				if (hierarchy.isInterface(name) && !hierarchy.isTreeInterface(name)) {
					newDecl = refactoring.refactorTreeInterface(nodeMap, path, interfaceDecl, hierarchy);
				}
			} else if (typeDecl instanceof ClassDecl) {
				final ClassDecl classDecl = (ClassDecl) typeDecl;
				final Name name = classDecl.name();

				if (hierarchy.isClass(name)) {
					newDecl = refactoring.refactorTreeClass(nodeMap, path, classDecl, hierarchy);
				}
			}
			if (newDecl != null && newDecl != typeDecl) {
				CompilationUnit newCU = (CompilationUnit) newDecl.parent().parent();
				newCU = newCU.withImports(refactoring.addImports(newCU.imports()));
				nodeMap = nodeMap.put(path, newCU);
			}
		}

		nodeMap = refactoring.finish(nodeMap, hierarchy);

		Printer printer = new Printer(false, FormattingSettings.Default);
		printer.printAll(nodeMap, rootDirectory, "UTF-8");
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

	private static final List<String> EXCLUDED_CLASSES = Arrays.asList("NodeList", "NodeOption", "NodeEither", "NodeMap");

	public static boolean filterTreeClass(String name, NodeOption<QualifiedType> superclass) {
		if (superclass.isNone()) return false;
		final String superclassName = superclass.get().name().id();
		return !EXCLUDED_CLASSES.contains(name) && TREE_SUPERCLASSES.contains(superclassName);
	}
}
