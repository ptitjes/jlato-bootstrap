package org.jlato.cc.grammar;

import org.javacc.parser.*;
import org.jlato.parser.ParseContext;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.stmt.Stmt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.jlato.tree.Trees.emptyList;

/**
 * @author Didier Villevalois
 */
public class ProductionsExtractor {

	public static GProductions extractProductions(File grammarFile) throws ParseException, FileNotFoundException {
		return new ProductionsExtractor().emitProductions(grammarFile);
	}

	private Parser parser = new Parser(ParserConfiguration.Default.preserveWhitespaces(true));

	private GProductions emitProductions(File grammarFile) throws ParseException, FileNotFoundException {
		JavaCCParser javaCCParser = new JavaCCParser(new FileReader(grammarFile));
		Options.init();
		javaCCParser.javacc_input();

		List<GProduction> productions = new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<NormalProduction> bnfproductions = JavaCCGlobals.bnfproductions;
		for (NormalProduction production : bnfproductions) {
			if (production instanceof BNFProduction) {
				String symbol = production.getLhs();
				MethodDecl signature = tokensToMethodDecl(production.getReturnTypeTokens(), symbol, production.getParameterListTokens());
				NodeList<Stmt> declarations = tokensToStatements(((BNFProduction) production).getDeclarationTokens());
				GExpansion expansion = emitExpansionTree(production.getExpansion());

				productions.add(new GProduction(symbol, signature, declarations, expansion));
			}
		}
		return new GProductions(productions);
	}

	private GExpansion emitExpansionTree(Expansion exp) {
		GExpansion expansion = null;
		if (exp instanceof Action) {
			expansion = emitExpansionAction((Action) exp);
		} else if (exp instanceof Choice) {
			expansion = emitExpansionChoice((Choice) exp);
		} else if (exp instanceof Lookahead) {
			expansion = emitExpansionLookahead((Lookahead) exp);
		} else if (exp instanceof NonTerminal) {
			expansion = emitExpansionNonTerminal((NonTerminal) exp);
		} else if (exp instanceof OneOrMore) {
			expansion = emitExpansionOneOrMore((OneOrMore) exp);
		} else if (exp instanceof RegularExpression) {
			expansion = emitExpansionRegularExpression((RegularExpression) exp);
		} else if (exp instanceof Sequence) {
			expansion = emitExpansionSequence((Sequence) exp);
		} else if (exp instanceof TryBlock) {
			expansion = emitExpansionTryBlock((TryBlock) exp);
		} else if (exp instanceof ZeroOrMore) {
			expansion = emitExpansionZeroOrMore((ZeroOrMore) exp);
		} else if (exp instanceof ZeroOrOne) {
			expansion = emitExpansionZeroOrOne((ZeroOrOne) exp);
		} else {
			throw new IllegalArgumentException("Unknown expansion type: " + exp.getClass());
		}
		return expansion;
	}

	private GExpansion emitExpansionAction(Action e) {
		return GExpansion.action(tokensToStatements(e.getActionTokens()));
	}

	@SuppressWarnings("unchecked")
	private MethodDecl tokensToMethodDecl(List returnTypeTokens, String name, List parameterListTokens) {
		String returnType = dumpTokens(returnTypeTokens);
		String parameterList = dumpTokens(parameterListTokens);

		String signature = returnType + " " + name + "(" + parameterList + ");";
		try {
			return parser.parse(ParseContext.MethodDecl, signature);
		} catch (org.jlato.parser.ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private NodeList<Stmt> tokensToStatements(List actionTokens) {
		String string = dumpTokens(actionTokens);

		try {
			return parser.parse(ParseContext.Statements, string);
		} catch (org.jlato.parser.ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private NodeList<Expr> tokensToArguments(List actionTokens) {
		if (actionTokens.isEmpty()) return emptyList();

		String string = dumpTokens(actionTokens);
		NodeList<Expr> arguments = emptyList();
		try {
			for (String argument : string.split(",")) {
				arguments = arguments.append(parser.parse(ParseContext.Expression, argument.trim()));
			}
			return arguments;
		} catch (org.jlato.parser.ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private String dumpTokens(List<Token> actionTokens) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Token actionToken : actionTokens) {
			if (first) first = false;
			else builder.append(" ");

			String image = actionToken.image;
			if (image.equals("{if (\"\" != null) return")) image = "return";
			else if (image.equals(";}")) image = ";";
			builder.append(image);
		}
		return builder.toString();
	}

	private GExpansion emitExpansionLookahead(Lookahead e) {
		if (!e.isExplicit()) return null;
		if (e.getAmount() != Integer.MAX_VALUE) return GExpansion.lookAhead(e.getAmount());
		return GExpansion.lookAhead(Collections.singletonList(emitExpansionTree(e.getLaExpansion())));
	}

	private GExpansion emitExpansionNonTerminal(NonTerminal nt) {
		String name = nt.getLhsTokens().isEmpty() ? null : nt.getLhsTokens().get(0).toString();
		String symbol = nt.getName();
		NodeList<Expr> arguments = tokensToArguments(nt.getArgumentTokens());
		return GExpansion.nonTerminal(name, symbol, arguments);
	}

	private GExpansion emitExpansionRegularExpression(RegularExpression r) {
		String name = r.lhsTokens.isEmpty() ? null : r.lhsTokens.get(0).toString();
		String symbol = emitRE(r);
		return GExpansion.terminal(name, symbol);
	}

	private GExpansion emitExpansionZeroOrOne(ZeroOrOne e) {
		return GExpansion.zeroOrOne(Collections.singletonList(emitExpansionTree(e.expansion)));
	}

	private GExpansion emitExpansionZeroOrMore(ZeroOrMore e) {
		return GExpansion.zeroOrMore(Collections.singletonList(emitExpansionTree(e.expansion)));
	}

	private GExpansion emitExpansionOneOrMore(OneOrMore e) {
		return GExpansion.oneOrMore(Collections.singletonList(emitExpansionTree(e.expansion)));
	}

	private GExpansion emitExpansionChoice(Choice e) {
		List<GExpansion> expansions = emitExpansions(e.getChoices());
		return GExpansion.choice(expansions);
	}

	private GExpansion emitExpansionSequence(Sequence e) {
		List<GExpansion> expansions = emitExpansions(e.units);
		return GExpansion.sequence(expansions);
	}

	private List<GExpansion> emitExpansions(List<Expansion> expansionList) {
		List<GExpansion> expansions = new ArrayList<>();
		for (Expansion ee : expansionList) {
			GExpansion expansion = emitExpansionTree(ee);
			if (expansion != null) expansions.add(expansion);
		}
		return expansions;
	}

	private GExpansion emitExpansionTryBlock(TryBlock e) {
		return emitExpansionTree(e.exp);
	}

	public static String emitRE(RegularExpression re) {
		String returnString = "";
		boolean hasLabel = !re.label.equals("");
		boolean justName = re instanceof RJustName;
		boolean eof = re instanceof REndOfFile;
		boolean isString = re instanceof RStringLiteral;
		boolean toplevelRE = (re.tpContext != null);
		boolean needBrackets
				= justName || eof || hasLabel || (!isString && toplevelRE);
		if (needBrackets) {
			returnString += "<";
			if (!justName) {
				if (re.private_rexp) {
					returnString += "#";
				}
				if (hasLabel) {
					returnString += re.label;
					returnString += ": ";
				}
			}
		}
		if (re instanceof RCharacterList) {
			RCharacterList cl = (RCharacterList) re;
			if (cl.negated_list) {
				returnString += "~";
			}
			returnString += "[";
			for (Iterator it = cl.descriptors.iterator(); it.hasNext(); ) {
				Object o = it.next();
				if (o instanceof SingleCharacter) {
					//returnString += "\"";
					char s[] = {((SingleCharacter) o).ch};
					returnString += add_escapes(new String(s));
					//returnString += "\"";
				} else if (o instanceof CharacterRange) {
					//returnString += "\"";
					char s[] = {((CharacterRange) o).getLeft()};
					returnString += add_escapes(new String(s));
					//returnString += "\"-\"";
					returnString += "-";
					s[0] = ((CharacterRange) o).getRight();
					returnString += add_escapes(new String(s));
					//returnString += "\"";
				} else {
					throw new IllegalArgumentException("Oops: unknown character list element type.");
				}
				if (it.hasNext()) {
					returnString += ",";
				}
			}
			returnString += "]";
		} else if (re instanceof RChoice) {
			RChoice c = (RChoice) re;
			for (Iterator it = c.getChoices().iterator(); it.hasNext(); ) {
				RegularExpression sub = (RegularExpression) (it.next());
				returnString += emitRE(sub);
				if (it.hasNext()) {
					returnString += " | ";
				}
			}
		} else if (re instanceof REndOfFile) {
			returnString += "EOF";
		} else if (re instanceof RJustName) {
			RJustName jn = (RJustName) re;
			returnString += jn.label;
		} else if (re instanceof ROneOrMore) {
			ROneOrMore om = (ROneOrMore) re;
			returnString += "(";
			returnString += emitRE(om.regexpr);
			returnString += ")+";
		} else if (re instanceof RSequence) {
			RSequence s = (RSequence) re;
			for (Iterator it = s.units.iterator(); it.hasNext(); ) {
				RegularExpression sub = (RegularExpression) (it.next());
				boolean needParens = false;
				if (sub instanceof RChoice) {
					needParens = true;
				}
				if (needParens) {
					returnString += "(";
				}
				returnString += emitRE(sub);
				if (needParens) {
					returnString += ")";
				}
				if (it.hasNext()) {
					returnString += " ";
				}
			}
		} else if (re instanceof RStringLiteral) {
			RStringLiteral sl = (RStringLiteral) re;
			returnString += (/*"\"" + */JavaCCParserInternals.add_escapes(sl.image)/* + "\""*/);
		} else if (re instanceof RZeroOrMore) {
			RZeroOrMore zm = (RZeroOrMore) re;
			returnString += "(";
			returnString += emitRE(zm.regexpr);
			returnString += ")*";
		} else if (re instanceof RZeroOrOne) {
			RZeroOrOne zo = (RZeroOrOne) re;
			returnString += "(";
			returnString += emitRE(zo.regexpr);
			returnString += ")?";
		} else if (re instanceof RRepetitionRange) {
			RRepetitionRange zo = (RRepetitionRange) re;
			returnString += "(";
			returnString += emitRE(zo.regexpr);
			returnString += ")";
			returnString += "{";
			if (zo.hasMax) {
				returnString += zo.min;
				returnString += ",";
				returnString += zo.max;
			} else {
				returnString += zo.min;
			}
			returnString += "}";
		} else {
			throw new IllegalArgumentException("Oops: Unknown regular expression type.");
		}
		if (needBrackets) {
			returnString += ">";
		}
		return returnString;
	}

	static public String add_escapes(String str) {
		String retval = "";
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\b') {
				retval += "\\b";
			} else if (ch == '\t') {
				retval += "\\t";
			} else if (ch == '\n') {
				retval += "\\n";
			} else if (ch == '\f') {
				retval += "\\f";
			} else if (ch == '\r') {
				retval += "\\r";
			} else if (ch == '\"') {
				retval += "\\\"";
			} else if (ch == '\'') {
				retval += "\\\'";
			} else if (ch == '\\') {
				retval += "\\\\";
			} else if (ch < 0x20 || ch > 0x7e) {
				String s = "0000" + Integer.toString(ch, 16);
				retval += "\\u" + s.substring(s.length() - 4, s.length());
			} else {
				retval += ch;
			}
		}
		return retval;
	}
}
