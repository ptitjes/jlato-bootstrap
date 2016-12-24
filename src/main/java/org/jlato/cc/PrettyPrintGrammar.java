package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.parser.ParseException;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class PrettyPrintGrammar {

	public static void main(String[] args) throws Exception {
		new PrettyPrintGrammar().prettyPrint();
	}

	public void prettyPrint() throws IOException, ParseException {
		prettyPrint(filter(Grammar.productions));
	}

	private GProductions filter(GProductions productions) {
		return productions.rewrite(p -> p.rewrite(e -> {
			switch (e.kind) {
				case LookAhead:
				case Action:
					return null;
			}
			return e;
		}));
	}

	private void prettyPrint(GProductions productions) {
		for (GProduction production : productions.getAll()) {
			prettyPrint(production);
		}
	}

	private void prettyPrint(GProduction production) {
		print(production.symbol + " ::=");
		printNewLine();
		indent(+1);
		prettyPrint(production.expansion);
		indent(-1);
		printNewLine();
		print(";");
		printNewLine();
		printNewLine();
	}

	private void prettyPrint(GExpansion expansion) {
		boolean uniqueChild = expansion.children != null && expansion.children.size() == 1;
		switch (expansion.kind) {
			case Choice:
				print("( ");
				prettyPrintChildren(expansion, " | ");
				print(" )");
				break;
			case Sequence:
				if (!uniqueChild) print("( ");
				prettyPrintChildren(expansion, " ");
				if (!uniqueChild) print(" )");
				break;
			case ZeroOrOne:
				if (!uniqueChild) print("( ");
				prettyPrintChildren(expansion, " ");
				if (!uniqueChild) print(" )");
				print("?");
				break;
			case ZeroOrMore:
				if (!uniqueChild) print("( ");
				prettyPrintChildren(expansion, " ");
				if (!uniqueChild) print(" )");
				print("*");
				break;
			case OneOrMore:
				if (!uniqueChild) print("( ");
				prettyPrintChildren(expansion, " ");
				if (!uniqueChild) print(" )");
				print("+");
				break;
			case NonTerminal:
				print(expansion.symbol);
				break;
			case Terminal:
				print(expansion.symbol);
				break;
			default:
				printNewLine();
				break;
		}
	}

	private void prettyPrintChildren(GExpansion expansion, String separator) {
		indent(+1);

		boolean first = true;
		for (GExpansion child : expansion.children) {
			if (first) first = false;
			else print(separator);
			prettyPrint(child);
		}

		indent(-1);
	}

	private boolean needsIndentation = false;
	private boolean wasSpace = false;
	private int indent = 0;

	private void indent(int delta) {
		indent += delta;
	}

	private void printNewLine() {
		System.out.println();
		needsIndentation = true;
	}

	private void print(String content) {
		if (content.equals(" ")) {
			wasSpace = true;
			return;
		}

		if (needsIndentation) {
			needsIndentation = false;
			for (int i = 0; i < indent; i++) {
				System.out.print("\t");
			}
		} else if (wasSpace) {
			System.out.print(" ");
		}
		System.out.print(content);
		wasSpace = false;
	}
}
