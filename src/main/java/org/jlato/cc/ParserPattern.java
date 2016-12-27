package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.cc.grammar.Grammar;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.NodeOption;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.ExpressionStmt;
import org.jlato.tree.stmt.IfStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.stmt.SwitchCase;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ParserPattern extends TypePattern.OfClass<TreeClassDescriptor[]> {

	private GProductions productions;
	private final String implementationName;
	private GrammarAnalysis grammarAnalysis;

	public ParserPattern(GProductions productions, String implementationName) {
		this.productions = productions;
		this.implementationName = implementationName;
	}

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "class " + implementationName + " extends ParserBaseALL { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor[] arg) {
		return "Internal implementation of the Java parser as a recursive descent parser using ALL(*) predictions.";
	}

	@Override
	protected ClassDecl contributeBody(ClassDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		importManager.addImports(listOf(
				importDecl(qualifiedName("org.jlato.internal.bu")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.coll")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.decl")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.expr")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.name")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.stmt")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.bu.type")).setOnDemand(true),
				importDecl(qualifiedName("org.jlato.internal.parser.all.Grammar")),
				importDecl(qualifiedName("org.jlato.internal.parser.all.GrammarSerialization")),
				importDecl(qualifiedName("org.jlato.internal.parser.Token")),
				importDecl(qualifiedName("org.jlato.internal.parser.TokenType")),
				importDecl(qualifiedName("org.jlato.tree.Problem.Severity")),
				importDecl(qualifiedName("org.jlato.parser.ParseException")),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.decl.ModifierKeyword")),
				importDecl(qualifiedName("org.jlato.tree.type.Primitive")),
				importDecl(qualifiedName("java.io.IOException"))
		));

		productions.recomputeReferences();

		grammarAnalysis = new GrammarAnalysis(productions);
		grammarAnalysis.analysis();

		List<GProduction> allProductions = productions.getAll();

		NodeList<MemberDecl> members = Trees.emptyList();
		members = members.append(memberDecl("" +
				"@Override protected Grammar initializeGrammar() {" +
				"   try { return GrammarSerialization.VERSION_1.decode(serializedGrammar); }" +
				"   catch(IOException e) { throw new RuntimeException(\"Can't initialize grammar\", e); }" +
				"}"
		).build());

		members = members.appendAll(constants());

		for (GProduction production : allProductions) {
			members = members.append(parseMethod(importManager, production));
		}

		members = members.append(serializedGrammarField());

		return decl.withMembers(members);
	}

	private NodeList<MemberDecl> constants() {
		NodeList<MemberDecl> members = emptyList();

		// TODO Generate entry-point ids ?

		// Generate entry-point ids
		boolean first = true;
//		for (Map.Entry<String, Integer> entry : grammarAnalysis.entryPointIds.entrySet()) {
//			String id = camelToConstant(lowerCaseFirst(entry.getKey()));
//			FieldDecl constant = fieldDecl(qType("int"))
//					.withModifiers(listOf(Modifier.Static, Modifier.Final))
//					.withVariables(listOf(
//							variableDeclarator(variableDeclaratorId(name(id)))
//									.withInit(literalExpr(entry.getValue()))
//					));
//			if (first) {
//				constant = constant.appendLeadingComment("Identifiers for non-terminal start states", true);
//				first = false;
//			}
//			members = members.append(constant);
//		}

		// Generate non-terminal ids
		first = true;
		for (Map.Entry<String, Integer> entry : grammarAnalysis.nonTerminalIds.entrySet()) {
			String id = camelToConstant(lowerCaseFirst(entry.getKey()));
			FieldDecl constant = fieldDecl(qType("int"))
					.withModifiers(listOf(Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(id)))
									.withInit(literalExpr(entry.getValue()))
					));
			if (first) {
				constant = constant.appendLeadingComment("Identifiers for non-terminal start states", true);
				first = false;
			}
			members = members.append(constant);
		}

		// Generate choice-point ids
		first = true;
		for (Map.Entry<String, Integer> entry : grammarAnalysis.choicePointIds.entrySet()) {
			String id = camelToConstant(lowerCaseFirst(entry.getKey()));
			FieldDecl constant = fieldDecl(qType("int"))
					.withModifiers(listOf(Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(id)))
									.withInit(literalExpr(entry.getValue()))
					));
			if (first) {
				constant = constant.appendLeadingComment("Identifiers for (non-ll1) choice-point states", true);
				first = false;
			}
			members = members.append(constant);
		}

		// Generate non-terminal return ids
		first = true;
		for (Map.Entry<String, Integer> entry : grammarAnalysis.nonTerminalReturnIds.entrySet()) {
			String id = camelToConstant(lowerCaseFirst(entry.getKey()));
			FieldDecl constant = fieldDecl(qType("int"))
					.withModifiers(listOf(Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(id)))
									.withInit(literalExpr(entry.getValue()))
					));
			if (first) {
				constant = constant.appendLeadingComment("Identifiers for non-terminal return states", true);
				first = false;
			}
			members = members.append(constant);
		}
		return members;
	}

	/* Serialized ALL(*) Grammar declaration */

	private MemberDecl serializedGrammarField() {
		try {
			String data = encode(grammarAnalysis.grammar);
			System.out.println("Serialized grammar length: " + data.length());

			NodeList<Expr> stringPartExprs = splitString(data, 100);
			Expr stringExpr = stringPartExprs.<Expr>foldLeft(literalExpr(""), (e1, e2) -> binaryExpr(e1, BinaryOp.Plus, e2));

			return fieldDecl(qualifiedType(name("String")))
					.withModifiers(listOf(Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name("serializedGrammar"))).withInit(stringExpr)
					));
		} catch (IOException e) {
			throw new RuntimeException("Can't serialize grammar: ", e);
		}
	}

	public static String encode(Grammar grammar) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream oos = new DataOutputStream(baos);
		grammar.writeTo(oos);
		oos.close();
		byte[] bytes = baos.toByteArray();

		int length = bytes.length;
		char[] chars = new char[length / 2 + length % 2];
		for (int i = 0; i < length; i += 2) {
			byte byte1 = bytes[i];
			byte byte2 = i + 1 < length ? bytes[i + 1] : 0;
			chars[i / 2] = (char) (((int) byte1 << 8 | (int) byte2 & 0xff) + 2 & 0xffff);
		}

		return new String(chars);
	}

	private NodeList<Expr> splitString(String data, int chunkSize) {
		NodeList<Expr> stringPartExprs = emptyList();
		int length = data.length();
		for (int i = 0; i < length; ) {
			int split = chunkSize / 6;
			int delta = 0;
			String part;
			do {
				split -= delta;
				part = data.substring(i, i + split > length ? length : i + split);
				part = escape(part);
				delta = (int) Math.signum(part.length() - chunkSize);
			} while ((i + split <= length && part.length() < chunkSize - 5) || part.length() > chunkSize);
			i += split;

			stringPartExprs = stringPartExprs.append(literalExpr(part).appendLeadingNewLine());
		}
		return stringPartExprs;
	}

	String escape(String str) {
		StringBuilder b = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (c >= 256) b.append("\\u").append(String.format("%04X", (int) c));
			else if (c < 256) b.append("\\").append(String.format("%o", (int) c));
		}
		return b.toString();
	}

	/* Parse methods */

	private MethodDecl parseMethod(ImportManager importManager, GProduction production) {
		String symbol = production.symbol;
		Type type = production.returnType;

		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(production.declarations);
		stmts = stmts.append(tokenVarDeclaration());
		stmts = stmts.appendAll(parseStatementsFor(symbol, production.expansion, production.hintParams, false));

		return methodDecl(type, name("parse" + upperCaseFirst(symbol)))
				.withModifiers(listOf(symbol.endsWith("Entry") ? Modifier.Public : Modifier.Protected))
				.withParams(production.hintParams.appendAll(production.dataParams))
				.withThrowsClause(listOf(qualifiedType(name("ParseException"))))
				.withBody(blockStmt().withStmts(stmts))
				.appendLeadingComment(production.expansion.toString(), true);
	}

	private NodeList<Stmt> parseStatementsFor(String symbol, GExpansion expansion, NodeList<FormalParameter> hintParams, boolean optional) {
		NodeList<Stmt> stmts = emptyList();
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(parseStatementsForChildren(symbol, expansion, hintParams, false));
				break;
			case ZeroOrOne: {
				if (expansion.children.size() == 1 && expansion.children.get(0).kind == GExpansion.Kind.Choice) {
					stmts = stmts.appendAll(parseStatementsForChildren(symbol, expansion, hintParams, true));
				} else {
					stmts = stmts.append(tokenVarUpdate());
					stmts = stmts.append(ifStmt(
							makeKleeneCondition(expansion, hintParams),
							blockStmt().withStmts(parseStatementsForChildren(symbol, expansion, hintParams, false))
					));
				}
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(tokenVarUpdate());
				stmts = stmts.append(whileStmt(
						makeKleeneCondition(expansion, hintParams),
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion, hintParams, false).append(tokenVarUpdate()))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion, hintParams, false).append(tokenVarUpdate())),
						makeKleeneCondition(expansion, hintParams)
				));
				break;
			}
			case Choice: {
				List<Set<String>> ll1Decisions = expansion.ll1Decisions;
				if (expansion.canUseLL1) {
					NodeList<IfStmt> cases = emptyList();

					int count = 1;
					for (GExpansion child : expansion.children) {
						Set<String> terminals = ll1Decisions.get(count++ - 1);

						// Produce statements
						NodeList<Stmt> caseStmts = parseStatementsFor(symbol, child, hintParams, optional);

						// Produce case
						cases = cases.append(ifStmt(matchExpression(terminals), blockStmt().withStmts(caseStmts)));
					}

					NodeOption<Stmt> stmt = cases.foldRight(
							optional ? Trees.<Stmt>none() : some(throwStmt(
									methodInvocationExpr(name("produceParseException"))
											.withArgs(listOf(prefixedAndOrderedConstants(merge(ll1Decisions))))
							)),
							(ifStmt, elseStmt) -> some(ifStmt.withElseStmt(elseStmt))
					);

					stmts = stmts.appendAll(listOf(
							tokenVarUpdate(),
							stmt.get()
					));
				} else {
					Expr selector = predict(expansion.constantName, hintParams);

					NodeList<SwitchCase> cases = emptyList();

					int count = 1;
					for (GExpansion child : expansion.children) {
						LiteralExpr<Integer> label = literalExpr(count++);

						NodeList<Stmt> caseStmts = parseStatementsFor(symbol, child, hintParams, optional);
						if (caseStmts.last().kind() != Kind.ReturnStmt) caseStmts = caseStmts.append(breakStmt());

						cases = cases.append(switchCase().withLabel(label).withStmts(caseStmts));
					}

					if (!optional) {
						cases = cases.append(switchCase().withStmts(listOf(throwStmt(
								methodInvocationExpr(name("produceParseException"))
										.withArgs(listOf(prefixedAndOrderedConstants(merge(ll1Decisions))))
						))));
					}

					stmts = stmts.append(switchStmt(selector).withCases(cases));
				}
				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("parse" + upperCaseFirst(expansion.symbol)))
						.withArgs(expansion.hints.appendAll(expansion.arguments));
				ExpressionStmt callStmt = expressionStmt(
						expansion.name == null ? call : assignExpr(name(expansion.name), AssignOp.Normal, call)
				);

				stmts = stmts.append(pushCallStack(expansion.constantName));
				stmts = stmts.append(callStmt);
				stmts = stmts.append(popCallStack());
				break;
			}
			case Terminal: {
				Expr argument = prefixedConstant(expansion.symbol);
				Expr call = methodInvocationExpr(name("consume")).withArgs(listOf(argument));
				ExpressionStmt callStmt = expressionStmt(
						expansion.name == null ? call : assignExpr(name(expansion.name), AssignOp.Normal, call)
				);

				stmts = stmts.append(callStmt);
				break;
			}
			case Action: {
				stmts = stmts.appendAll(expansion.action);
				break;
			}
			default:
		}
		return stmts;
	}

	private NodeList<Stmt> parseStatementsForChildren(String symbol, GExpansion expansion, NodeList<FormalParameter> hintParams, boolean optional) {
		NodeList<Stmt> stmts = emptyList();
		for (GExpansion child : expansion.children) {
			stmts = stmts.appendAll(parseStatementsFor(symbol, child, hintParams, optional));
		}
		return stmts;
	}

	/* Decisions' condition generation */

	private Expr makeKleeneCondition(GExpansion expansion, NodeList<FormalParameter> hintParams) {
		List<Set<String>> ll1DecisionTerminals = expansion.ll1Decisions;
		// TODO Check for length of ll1DecisionTerminals[0] and ll1DecisionTerminals[1]
		// Because we could also negate a condition made from ll1DecisionTerminals[0]
		return expansion.canUseLL1 ?
				matchExpression(ll1DecisionTerminals.get(1)) :
				binaryExpr(predict(expansion.constantName, hintParams), BinaryOp.Equal, literalExpr(1));
	}

	private Set<String> merge(List<Set<String>> ll1Decisions) {
		HashSet<String> merged = new HashSet<>();
		for (Set<String> terminals : ll1Decisions) {
			merged.addAll(terminals);
		}
		return merged;
	}

	private static final Name TOKEN_VAR_NAME = name("__token");

	private Stmt tokenVarDeclaration() {
		return expressionStmt(variableDeclarationExpr(
				localVariableDecl(primitiveType(Primitive.Int)).withVariables(listOf(
						variableDeclarator(variableDeclaratorId(TOKEN_VAR_NAME))
				))
		));
	}

	private Stmt tokenVarUpdate() {
		return expressionStmt(assignExpr(TOKEN_VAR_NAME, AssignOp.Normal, getTokenKind(literalExpr(0))));
	}

	private Expr matchExpression(Collection<String> terminals) {
		return matchExpression(prefixedAndOrderedConstants(terminals));
	}

	private Expr matchExpression(List<Expr> terminalNames) {
		// TODO Have a better heuristic to choose between explicit matches and bit operations
		if (terminalNames.size() == 1)
			return matchExpression(terminalNames.get(0));
		else if (terminalNames.size() == 2)
			return binaryExpr(matchExpression(terminalNames.get(0)), BinaryOp.Or, matchExpression(terminalNames.get(1)));
		else {
			Expr test = null;
			Expr mask = null;
			int firstValue = -1;

			for (Expr terminal : terminalNames) {
				int value = terminalTable.get(terminal);

				if (firstValue == -1) firstValue = value;
				if (value >= firstValue + 64) {
					Expr thisTest = testFor(mask, firstValue);

					if (test == null) test = thisTest;
					else test = binaryExpr(test, BinaryOp.Or, thisTest);

					firstValue = value;
					mask = null;
				}

				Expr thisMask = maskFor(terminal, firstValue);
				if (mask == null) mask = thisMask;
				else mask = binaryExpr(mask, BinaryOp.BinOr, thisMask);
			}

			if (mask != null) {
				Expr thisTest = testFor(mask, firstValue);

				if (test == null) test = thisTest;
				else test = binaryExpr(parenthesizedExpr(test), BinaryOp.Or, parenthesizedExpr(thisTest));
			}

			return test;
		}
	}

	private Expr testFor(Expr mask, int firstValue) {
		return binaryExpr(
				binaryExpr(
						parenthesizedBinExpr(
								binaryExpr(TOKEN_VAR_NAME, BinaryOp.Minus, literalExpr(firstValue)),
								BinaryOp.BinAnd,
								unaryExpr(UnaryOp.Inverse, literalExpr(63))
						),
						BinaryOp.Equal,
						literalExpr(0)
				),
				BinaryOp.And,
				binaryExpr(
						parenthesizedBinExpr(maskFor(TOKEN_VAR_NAME, firstValue), BinaryOp.BinAnd, parenthesizedExpr(mask)),
						BinaryOp.NotEqual,
						literalExpr(0)
				)
		);
	}

	private Expr maskFor(Expr name, int firstValue) {
		return binaryExpr(literalExpr(1L), BinaryOp.LeftShift, binaryExpr(name, BinaryOp.Minus, literalExpr(firstValue)));
	}

	private Expr parenthesizedBinExpr(Expr left, BinaryOp op, Expr right) {
		return parenthesizedExpr(binaryExpr(left, op, right));
	}

	private Expr matchExpression(Expr terminalName) {
		return binaryExpr(TOKEN_VAR_NAME, BinaryOp.Equal, terminalName);
	}

	private Expr predict(String namePrefix, NodeList<FormalParameter> hintParams) {
		String name = camelToConstant(lowerCaseFirst(namePrefix));
		Expr constant = fieldAccessExpr(name(name))/*.withScope(name("JavaGrammar"))*/;

		return methodInvocationExpr(name("predict")).withArgs(listOf(constant));
	}

	/* Miscellaneous method call generation */

	private Expr getTokenKind(Expr lookahead) {
		return fieldAccessExpr(name("kind")).withScope(methodInvocationExpr(name("getToken")).withArgs(listOf(lookahead)));
	}

	private Stmt pushCallStack(String name) {
		String id = camelToConstant(lowerCaseFirst(name));
		Expr constant = fieldAccessExpr(name(id))/*.withScope(name("JavaGrammar"))*/;

		return expressionStmt(methodInvocationExpr(name("pushCallStack")).withArgs(listOf(constant)));
	}

	private Stmt popCallStack() {
		return expressionStmt(methodInvocationExpr(name("popCallStack")));
	}

	private List<Expr> prefixedAndOrderedConstants(Collection<String> tokens) {
		return tokens.stream()
				.filter(t -> t != null)
				.map(this::prefixedConstant)
				.sorted(terminalComparator)
				.collect(Collectors.toList());
	}

	private FieldAccessExpr prefixedConstant(String token) {
		return fieldAccessExpr(name(token)).withScope(name("TokenType"));
	}

	private Comparator<Expr> terminalComparator = new Comparator<Expr>() {
		@Override
		public int compare(Expr o1, Expr o2) {
			return terminalTable.get(o1).compareTo(terminalTable.get(o2));
		}
	};

	private Map<Expr, Integer> terminalTable = new HashMap<>();

	private void addTerminal(String name, int value) {
		terminalTable.put(prefixedConstant(name), value);
	}

	{
		for (Map.Entry<String, Integer> entry : JavaGrammar.terminals.entrySet()) {
			addTerminal(entry.getKey(), entry.getValue());
		}
	}
}
