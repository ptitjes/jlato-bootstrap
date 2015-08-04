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
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.parser.ParseException;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.FormattingSettings;
import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;

import java.io.File;
import java.io.IOException;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class Bootstrap {

	public static void main(String[] args) throws IOException, ParseException {
		new Bootstrap().generate();
	}

	public void generate() throws IOException, ParseException {
		Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));
		File rootDirectory = new File("../jlato/src/main/java");
		TreeSet<CompilationUnit> treeSet = parser.parseAll(rootDirectory, "UTF-8");

		final TreeInterfaceDescriptor[] interfaceDescriptors = AllDescriptors.ALL_INTERFACES;
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;

		// Generate Tree interfaces
//		final TreeInterface treeInterfacePattern = new TreeInterface();
//		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
//			treeSet = applyPattern(treeSet, descriptor.treeFilePath(), treeInterfacePattern, descriptor);
//		}

		// Generate pure interfaces
		final TreePureInterface treeInterfacePattern = new TreePureInterface();
		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
			treeSet = treeInterfacePattern.apply(treeSet, descriptor.interfaceFilePath(), descriptor);
		}
		for (TreeClassDescriptor descriptor : classDescriptors) {
			if (descriptor.customTailored) continue;
			treeSet = treeInterfacePattern.apply(treeSet, descriptor.interfaceFilePath(), descriptor);
		}

		// Generate Tree classes
		final CompilationUnitPattern<TreeClassDescriptor> treeClassPattern = CompilationUnitPattern.of(new TreeClass());
		for (TreeClassDescriptor descriptor : classDescriptors) {
			if (descriptor.customTailored) continue;
			treeSet = treeClassPattern.apply(treeSet, descriptor.classFilePath(), descriptor);
		}

		// Generate State interfaces
		final CompilationUnitPattern<TreeInterfaceDescriptor> stateInterfacePattern = CompilationUnitPattern.of(new StateInterface());
		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
			treeSet = stateInterfacePattern.apply(treeSet, descriptor.stateTypeFilePath(), descriptor);
		}
		// Generate State classes
		final CompilationUnitPattern<TreeClassDescriptor> stateClassPattern = CompilationUnitPattern.of(new StateClass());
		for (TreeClassDescriptor descriptor : classDescriptors) {
//			if (descriptor.customTailored) continue;
			treeSet = stateClassPattern.apply(treeSet, descriptor.stateTypeFilePath(), descriptor);
		}

		// Generate Kind enum
		final KindEnum kindEnumPattern = new KindEnum();
		treeSet = applyPattern(treeSet, "org/jlato/tree/Kind.java", kindEnumPattern, classDescriptors);

		// Generate Trees
		final TreeFactoryClass treeFactoryClassPattern = new TreeFactoryClass();
		treeSet = applyPattern(treeSet, "org/jlato/tree/Trees.java", treeFactoryClassPattern, classDescriptors);

		treeSet.updateOnDisk(false, FormattingSettings.Default);
	}

	private <A, T extends TypeDecl> TreeSet<CompilationUnit> applyPattern(TreeSet<CompilationUnit> treeSet, String path, DeclPattern<A, T> pattern, A descriptor) {
		final CompilationUnit cu = treeSet.get(path);

		final Pattern<? extends Decl> matcher = pattern.matcher(descriptor);
		ImportManager importManager = new ImportManager(makePackageName(path), cu.imports());
		final MatchVisitor<Decl> visitor = (c, s) -> pattern.rewrite((T) (GenSettings.replace ? matcher.build() : c), importManager, descriptor);
		final CompilationUnit newCU = cu.forAll(matcher, visitor)
				.withImports(importManager.imports());
		return treeSet.put(path, newCU);
	}

	private QualifiedName makePackageName(String path) {
		final String packageName = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
		return qualifiedName(packageName);
	}
}
