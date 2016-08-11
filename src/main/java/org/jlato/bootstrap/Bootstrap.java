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

import org.jlato.bootstrap.ast.*;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.tests.TreesAccessorsTest;
import org.jlato.bootstrap.tests.TreesEqualsHashCodeTest;
import org.jlato.bootstrap.tests.TreesKindTest;
import org.jlato.bootstrap.tests.TreesLambdaAccessorsTest;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.cc.GenParser;
import org.jlato.parser.ParseException;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.pattern.MatchVisitor;
import org.jlato.pattern.Pattern;
import org.jlato.printer.FormattingSettings;
import org.jlato.printer.Printer;
import org.jlato.tree.NodeMap;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.QualifiedName;

import java.io.File;
import java.io.IOException;

import static org.jlato.tree.Trees.qualifiedName;

/**
 * @author Didier Villevalois
 */
public class Bootstrap {

	public static void main(String[] args) throws IOException, ParseException {
		new Bootstrap().generate();
	}

	public void generate() throws IOException, ParseException {
		String pathToJLaTo = System.getProperty("path.to.jlato");
		String mainRootDirectory = pathToJLaTo + "src/main/java";
		String testRootDirectory = pathToJLaTo + "src/test/java";

		new GenParser().generate();

		generateAST(mainRootDirectory);
		generateUnitTests(testRootDirectory);
	}

	private void generateAST(String rootDirectory) throws ParseException, IOException {
		Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));
		NodeMap<CompilationUnit> nodeMap = parser.parseAll(new File(rootDirectory), "UTF-8");

		final TreeInterfaceDescriptor[] interfaceDescriptors = AllDescriptors.ALL_INTERFACES;
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;

		// Generate pure interfaces
		final TreePureInterface treeInterfacePattern = new TreePureInterface();
		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
			nodeMap = treeInterfacePattern.apply(nodeMap, descriptor.interfaceFilePath(), descriptor);
		}
		for (TreeClassDescriptor descriptor : classDescriptors) {
			if (descriptor.customTailored) continue;
			nodeMap = treeInterfacePattern.apply(nodeMap, descriptor.interfaceFilePath(), descriptor);
		}

		// Generate Tree classes
		final CompilationUnitPattern<TreeClassDescriptor> treeClassPattern = CompilationUnitPattern.of(new TreeClass());
		for (TreeClassDescriptor descriptor : classDescriptors) {
			if (descriptor.customTailored) continue;
			nodeMap = treeClassPattern.apply(nodeMap, descriptor.classFilePath(), descriptor);
		}

		// Generate State interfaces
		final CompilationUnitPattern<TreeInterfaceDescriptor> stateInterfacePattern = CompilationUnitPattern.of(new StateInterface());
		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
			nodeMap = stateInterfacePattern.apply(nodeMap, descriptor.stateTypeFilePath(), descriptor);
		}
		// Generate State classes
		final CompilationUnitPattern<TreeClassDescriptor> stateClassPattern = CompilationUnitPattern.of(new StateClass());
		for (TreeClassDescriptor descriptor : classDescriptors) {
			nodeMap = stateClassPattern.apply(nodeMap, descriptor.stateTypeFilePath(), descriptor);
		}

		// Generate Kind enum
		final KindEnum kindEnumPattern = new KindEnum();
		nodeMap = applyPattern(nodeMap, "org/jlato/tree/Kind.java", kindEnumPattern, classDescriptors);

		// Generate Trees
		final TreeFactoryClass treeFactoryClassPattern = new TreeFactoryClass();
		nodeMap = applyPattern(nodeMap, "org/jlato/tree/Trees.java", treeFactoryClassPattern, classDescriptors);

		Printer printer = new Printer(false, FormattingSettings.Default);
		printer.printAll(nodeMap, new File(rootDirectory), "UTF-8");
	}

	private void generateUnitTests(String rootDirectory) throws ParseException, IOException {
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;

		// Generate unit test classes

		CompilationUnitPattern.of(new TreesKindTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesKindTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesEqualsHashCodeTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesEqualsHashCodeTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesAccessorsTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesAccessorsTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesLambdaAccessorsTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesLambdaAccessorsTest.java", classDescriptors);
	}

	private <A, T extends TypeDecl> NodeMap<CompilationUnit> applyPattern(NodeMap<CompilationUnit> nodeMap, String path, DeclPattern<A, T> pattern, A descriptor) {
		final CompilationUnit cu = nodeMap.get(path);

		final Pattern<? extends Decl> matcher = pattern.matcher(descriptor);
		ImportManager importManager = new ImportManager(makePackageName(path), cu.imports());
		final MatchVisitor<Decl> visitor = (c, s) -> pattern.rewrite((T) (GenSettings.replace ? matcher.build() : c), importManager, descriptor);
		final CompilationUnit newCU = importManager.organiseAndSet(cu.forAll(matcher, visitor));
		return nodeMap.put(path, newCU);
	}

	private QualifiedName makePackageName(String path) {
		final String packageName = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
		return qualifiedName(packageName);
	}
}
