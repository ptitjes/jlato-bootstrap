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

import org.jlato.bootstrap.ast.KindEnum;
import org.jlato.bootstrap.ast.TreeClassDescriptor;
import org.jlato.bootstrap.ast.TreeClass;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.parser.ParseException;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.FormattingSettings;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.TypeDecl;

import java.io.File;
import java.io.IOException;

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

		final TreeClassDescriptor[] descriptors = TreeClassDescriptor.ALL;

		// Generate Tree classes
		final TreeClass treeClassPattern = new TreeClass();
		for (TreeClassDescriptor descriptor : descriptors) {
			treeSet = applyPattern(treeSet, descriptor.treeClassFileName(), treeClassPattern, descriptor);
		}

		// Generate Kind enum
		final KindEnum kindEnumPattern = new KindEnum();
		treeSet = applyPattern(treeSet, "org/jlato/tree/Kind.java", kindEnumPattern, descriptors);

		treeSet.updateOnDisk(false, FormattingSettings.Default);
	}

	private <A, T extends TypeDecl> TreeSet<CompilationUnit> applyPattern(TreeSet<CompilationUnit> treeSet, String path, DeclPattern<A, T> pattern, A descriptor) {
		final CompilationUnit cu = treeSet.get(path);
		final CompilationUnit newCU = cu.forAll(pattern.matcher(descriptor), (c, s) -> pattern.rewrite((T) c, descriptor));
		return treeSet.put(path, newCU);
	}
}
