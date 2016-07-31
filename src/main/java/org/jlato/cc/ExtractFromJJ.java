package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.cc.grammar.*;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.Printer;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.Modifier;
import org.jlato.tree.expr.ObjectCreationExpr;

import java.io.*;
import java.util.stream.Collectors;

import static org.jlato.bootstrap.Utils.insertNewLineAfterLast;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ExtractFromJJ {

	private final Parser parser = new Parser();

	public static void main(String[] args) throws Exception {
		new ExtractFromJJ().generate();
	}

	private void generate() throws IOException, ParseException {
		Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));
		String pathToJLaTo = System.getProperty("path.to.jlato");
		File javaSourceDirectory = new File(pathToJLaTo + "src/main/java");
		File jjSourceDirectory = new File(pathToJLaTo + "src/main/javacc");

		File grammarFile = new File(jjSourceDirectory, "grammar.jj");

		GProductions productions = ProductionsExtractor.extractProductions(grammarFile);
		productions.rewrite(this::simplifyProduction);

		productions.recomputeReferences();

		writeGrammar(productions);
	}

	private void writeGrammar(GProductions productions) throws IOException {
		ObjectCreationExpr productionListExpr =
				objectCreationExpr(qualifiedType(name("GProductions"))).withArgs(insertNewLineAfterLast(
						listOf(
								productions.getAll().stream()
										.map(c -> c.toExpr().insertNewLineBefore())
										.collect(Collectors.toList())
						)
				));

		CompilationUnit cu = compilationUnit(packageDecl(qualifiedName("org.jlato.cc")))
				.withImports(listOf(
						importDecl(qualifiedName("org.jlato.cc.grammar.GExpansion")),
						importDecl(qualifiedName("org.jlato.cc.grammar.GProduction")),
						importDecl(qualifiedName("org.jlato.cc.grammar.GProductions")),
						importDecl(qualifiedName("org.jlato.tree.decl.MethodDecl")).insertNewLineAfter(),
						importDecl(qualifiedName("org.jlato.pattern.Quotes.expr")).setStatic(true),
						importDecl(qualifiedName("org.jlato.pattern.Quotes.memberDecl")).setStatic(true),
						importDecl(qualifiedName("org.jlato.pattern.Quotes.stmt")).setStatic(true),
						importDecl(qualifiedName("org.jlato.tree.Trees.emptyList")).setStatic(true),
						importDecl(qualifiedName("org.jlato.tree.Trees.listOf")).setStatic(true)
				))
				.withTypes(listOf(
						classDecl(name("Grammar"))
								.withModifiers(listOf(Modifier.Public))
								.withMembers(listOf(
										fieldDecl(qualifiedType(name("GProductions")))
												.withModifiers(listOf(Modifier.Public, Modifier.Static))
												.withVariables(listOf(
														variableDeclarator(variableDeclaratorId(name("productions")))
																.withInit(productionListExpr)
												))
								))
				));

		PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/org/jlato/cc/Grammar.java"));
		Printer.printTo(cu, writer, true);
		writer.close();
	}

	private GProduction simplifyProduction(GProduction p) {
		return p.rewrite(e -> {
			switch (e.kind) {
				case Choice:
				case Sequence:
					if (e.children.size() == 1) return e.children.get(0);
				case LookAhead:
					if (e.semanticLookahead != null || e.amount != -1) {
						return e;
					}
				case ZeroOrOne:
				case ZeroOrMore:
				case OneOrMore:
					if (e.children.size() == 1) {
						GExpansion child = e.children.get(0);
						if (child.kind == GExpansion.Kind.Sequence)
							return new GExpansion(e.kind, child.children, null, null, null, null, null, -1, null, false);
					}
				default:
			}
			return e;
		});
	}
}
