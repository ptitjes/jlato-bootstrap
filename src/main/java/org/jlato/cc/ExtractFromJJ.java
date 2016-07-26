package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.cc.grammar.*;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.Printer;
import org.jlato.tree.expr.MethodInvocationExpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;

import static org.jlato.bootstrap.Utils.insertNewLineAfterLast;
import static org.jlato.tree.Trees.listOf;
import static org.jlato.tree.Trees.methodInvocationExpr;
import static org.jlato.tree.Trees.name;

/**
 * @author Didier Villevalois
 */
public class ExtractFromJJ {

	private final Parser parser = new Parser();

	public static void main(String[] args) throws Exception {
		new ExtractFromJJ().generate();
	}

	private void generate() throws FileNotFoundException, ParseException {
		Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));
		String pathToJLaTo = System.getProperty("path.to.jlato");
		File javaSourceDirectory = new File(pathToJLaTo + "src/main/java");
		File jjSourceDirectory = new File(pathToJLaTo + "src/main/javacc");

		File grammarFile = new File(jjSourceDirectory, "grammar.jj");

		GProductions productions = ProductionsExtractor.extractProductions(grammarFile);
		productions.rewrite(this::simplifyProduction);

		productions.recomputeReferences();

		MethodInvocationExpr productionListExpr =
				methodInvocationExpr(name("listOf")).withArgs(insertNewLineAfterLast(
						listOf(
								productions.getAll().stream()
										.map(c -> c.toExpr().insertNewLineBefore())
										.collect(Collectors.toList())
						)
				));

		System.out.println(productionListExpr);
	}

	private GProduction simplifyProduction(GProduction p) {
		return p.rewrite(e -> {
			switch (e.kind) {
				case Choice:
				case Sequence:
					if (e.children.size() == 1) return e.children.get(0);
				case LookAhead:
					if (e.amount != -1) {
						return e;
					}
				case ZeroOrOne:
				case ZeroOrMore:
				case OneOrMore:
					if (e.children.size() == 1) {
						GExpansion child = e.children.get(0);
						if (child.kind == GExpansion.Kind.Sequence)
							return new GExpansion(e.kind, child.children, null, null, null, -1);
					}
				default:
			}
			return e;
		});
	}
}
