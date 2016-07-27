package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.tests.TreesKindTest;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;

import javax.print.attribute.standard.MediaSize;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.jlato.tree.Trees.listOf;

/**
 * @author Didier Villevalois
 */
public class GenParser {

	public static void main(String[] args) throws Exception {
		new GenParser().generate();
	}

	private void generate() throws IOException, ParseException, org.jlato.parser.ParseException {
		GProductions productions = Grammar.productions;

		String pathToJLaTo = System.getProperty("path.to.jlato");
		String rootDirectory = pathToJLaTo + "src/test/java";

		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;

		// Generate unit test classes

		CompilationUnitPattern.of(new ParserPattern())
				.apply(rootDirectory, "org/jlato/internal/parser/ParserImplementation.java", classDescriptors);
	}
}