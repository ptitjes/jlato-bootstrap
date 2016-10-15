package org.jlato.cc;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.GProductions;
import org.jlato.parser.ParseException;
import org.jlato.printer.FormattingSettings;
import org.jlato.tree.decl.ClassDecl;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class GenParser {

	public static void main(String[] args) throws Exception {
		new GenParser().generate();
	}

	public void generate() throws IOException, ParseException {
		String pathToJLaTo = System.getProperty("path.to.jlato");
		String rootDirectory = pathToJLaTo + "src/main/java";

		generateParser(new ParserPattern(Grammar.productions, "ParserImplementation"), "ParserImplementation", rootDirectory);
		generateParser(new ParserPattern2(Grammar2.productions, "ParserImplementation2"), "ParserImplementation2", rootDirectory);
	}

	private void generateParser(TypePattern<TreeClassDescriptor[], ClassDecl> pattern, String implementationName, String rootDirectory) throws org.jlato.parser.ParseException, IOException {
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;
		CompilationUnitPattern.of(pattern)
				.withFormatting(true, FormattingSettings.Default.withCommentFormatting(true))
				.apply(rootDirectory, "org/jlato/internal/parser/" + implementationName + ".java", classDescriptors);
	}
}
