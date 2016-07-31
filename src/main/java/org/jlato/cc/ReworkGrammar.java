package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.cc.grammar.ProductionsExtractor;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.Printer;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.Modifier;
import org.jlato.tree.expr.ObjectCreationExpr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static org.jlato.bootstrap.Utils.insertNewLineAfterLast;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ReworkGrammar {

	private final Parser parser = new Parser();

	public static void main(String[] args) throws Exception {
		new ReworkGrammar().generate();
	}

	private void generate() throws IOException, ParseException {
		GProductions productions = Grammar.productions;

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
						importDecl(qualifiedName("org.jlato.rewrite.Quotes.expr")).setStatic(true),
						importDecl(qualifiedName("org.jlato.rewrite.Quotes.memberDecl")).setStatic(true),
						importDecl(qualifiedName("org.jlato.rewrite.Quotes.stmt")).setStatic(true),
						importDecl(qualifiedName("org.jlato.tree.Trees.emptyList")).setStatic(true),
						importDecl(qualifiedName("org.jlato.tree.Trees.listOf")).setStatic(true),
						importDecl(qualifiedName("org.jlato.cc.grammar.GExpansion")).setOnDemand(true).setStatic(true)
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
}
