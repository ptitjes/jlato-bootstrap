package org.jlato.cc.grammar;

import org.javacc.parser.*;
import org.jlato.parser.ParseContext;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Type;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
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

				Type returnType = signature.type();
				NodeList<FormalParameter> dataParams = signature.params();
				productions.add(new GProduction(symbol, returnType.kind() == Kind.VoidType ? null : returnType,
						emptyList(), dataParams, declarations, expansion));
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

	@SuppressWarnings("unchecked")
	private Expr tokensToExpression(List actionTokens) {
		String string = dumpTokens(actionTokens);

		try {
			return parser.parse(ParseContext.Expression, string);
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
		if (e.getAmount() == 0) return GExpansion.lookAhead(tokensToExpression(e.getActionTokens()));
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
		if (eof) return "EOF";
		else if (hasLabel) return re.label;
		else if (isString) {
			switch (((RStringLiteral) re).image) {
				// Separators
				case "(":
					return "LPAREN";
				case ")":
					return "RPAREN";
				case "{":
					return "LBRACE";
				case "}":
					return "RBRACE";
				case "[":
					return "LBRACKET";
				case "]":
					return "RBRACKET";
				case ";":
					return "SEMICOLON";
				case ",":
					return "COMMA";
				case ".":
					return "DOT";
				case "@":
					return "AT";
				// Operators
				case "=":
					return "ASSIGN";
				case "<":
					return "LT";
				case ">":
					return "GT";
				case "!":
					return "BANG";
				case "~":
					return "TILDE";
				case "?":
					return "HOOK";
				case ":":
					return "COLON";
				case "==":
					return "EQ";
				case "<=":
					return "LE";
				case ">=":
					return "GE";
				case "!=":
					return "NE";
				case "||":
					return "SC_OR";
				case "&&":
					return "SC_AND";
				case "++":
					return "INCR";
				case "--":
					return "DECR";
				case "+":
					return "PLUS";
				case "-":
					return "MINUS";
				case "*":
					return "STAR";
				case "/":
					return "SLASH";
				case "&":
					return "BIT_AND";
				case "|":
					return "BIT_OR";
				case "^":
					return "XOR";
				case "%":
					return "REM";
				case "<<":
					return "LSHIFT";
				case "+=":
					return "PLUSASSIGN";
				case "-=":
					return "MINUSASSIGN";
				case "*=":
					return "STARASSIGN";
				case "/=":
					return "SLASHASSIGN";
				case "&=":
					return "ANDASSIGN";
				case "|=":
					return "ORASSIGN";
				case "^=":
					return "XORASSIGN";
				case "%=":
					return "REMASSIGN";
				case "<<=":
					return "LSHIFTASSIGN";
				case ">>=":
					return "RSIGNEDSHIFTASSIGN";
				case ">>>=":
					return "RUNSIGNEDSHIFTASSIGN";
				case "...":
					return "ELLIPSIS";
				case "->":
					return "ARROW";
				case "::":
					return "DOUBLECOLON";

				case "abstract":
					return "ABSTRACT";
				case "assert":
					return "ASSERT";
				case "boolean":
					return "BOOLEAN";
				case "break":
					return "BREAK";
				case "byte":
					return "BYTE";
				case "case":
					return "CASE";
				case "catch":
					return "CATCH";
				case "char":
					return "CHAR";
				case "class":
					return "CLASS";
				case "const":
					return "CONST";
				case "continue":
					return "CONTINUE";
				case "default":
					return "_DEFAULT";
				case "do":
					return "DO";
				case "double":
					return "DOUBLE";
				case "else":
					return "ELSE";
				case "enum":
					return "ENUM";
				case "extends":
					return "EXTENDS";
				case "false":
					return "FALSE";
				case "final":
					return "FINAL";
				case "finally":
					return "FINALLY";
				case "float":
					return "FLOAT";
				case "for":
					return "FOR";
				case "goto":
					return "GOTO";
				case "if":
					return "IF";
				case "implements":
					return "IMPLEMENTS";
				case "import":
					return "IMPORT";
				case "instanceof":
					return "INSTANCEOF";
				case "int":
					return "INT";
				case "interface":
					return "INTERFACE";
				case "long":
					return "LONG";
				case "native":
					return "NATIVE";
				case "new":
					return "NEW";
				case "null":
					return "NULL";
				case "package":
					return "PACKAGE";
				case "private":
					return "PRIVATE";
				case "protected":
					return "PROTECTED";
				case "public":
					return "PUBLIC";
				case "return":
					return "RETURN";
				case "short":
					return "SHORT";
				case "static":
					return "STATIC";
				case "strictfp":
					return "STRICTFP";
				case "super":
					return "SUPER";
				case "switch":
					return "SWITCH";
				case "synchronized":
					return "SYNCHRONIZED";
				case "this":
					return "THIS";
				case "throw":
					return "THROW";
				case "throws":
					return "THROWS";
				case "transient":
					return "TRANSIENT";
				case "true":
					return "TRUE";
				case "try":
					return "TRY";
				case "void":
					return "VOID";
				case "volatile":
					return "VOLATILE";
				case "while":
					return "WHILE";
				case "\u001A": // TODO Fix
					return "EOF";
				default:
			}
		}
		throw new IllegalArgumentException();
	}
}
