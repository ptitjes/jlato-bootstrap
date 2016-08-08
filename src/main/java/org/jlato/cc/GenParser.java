package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.tests.TreesKindTest;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.printer.FormattingSettings;

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
		String pathToJLaTo = System.getProperty("path.to.jlato");
		String rootDirectory = pathToJLaTo + "src/main/java";

		generateParser(Grammar.productions, rootDirectory, "ParserImplementation");
		generateParser(Grammar2.productions, rootDirectory, "ParserImplementation2");
		generateParser(Grammar3.productions, rootDirectory, "ParserImplementation3");
	}

	private void generateParser(GProductions productions, String rootDirectory, String implementationName) throws org.jlato.parser.ParseException, IOException {
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;
		CompilationUnitPattern.of(new ParserPattern(productions))
				.withFormatting(true, FormattingSettings.Default.withCommentFormatting(true))
				.apply(rootDirectory, "org/jlato/internal/parser/" + implementationName + ".java", classDescriptors);
	}
}
