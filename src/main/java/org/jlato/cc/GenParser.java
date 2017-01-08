package org.jlato.cc;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.GProductions;
import org.jlato.parser.ParseException;
import org.jlato.printer.FormattingSettings;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.InterfaceDecl;

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

//		generateTokenType(new TokenTypePattern(), rootDirectory);

		GProductions productions = JavaGrammar.productions;
		productions = new LeftRecursionElimination().transform(productions);
		productions = new GrammarTransform().transform(productions);

		generateParser(new ParserPattern(productions, "ParserImplementation"), "ParserImplementation", rootDirectory);
	}

	private void generateTokenType(TypePattern<TreeClassDescriptor[], InterfaceDecl> pattern, String rootDirectory) throws ParseException, IOException {
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;
		CompilationUnitPattern.of(pattern)
				.withFormatting(true, FormattingSettings.Default.withCommentFormatting(true))
				.apply(rootDirectory, "org/jlato/internal/parser/TokenType.java", classDescriptors);
	}

	private void generateParser(TypePattern<TreeClassDescriptor[], ClassDecl> pattern, String implementationName, String rootDirectory) throws ParseException, IOException {
		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;
		CompilationUnitPattern.of(pattern)
				.withFormatting(true, FormattingSettings.Default.withCommentFormatting(true))
				.apply(rootDirectory, "org/jlato/internal/parser/" + implementationName + ".java", classDescriptors);
	}
}
