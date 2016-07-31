package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.GContinuations;
import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GLocation;
import org.jlato.cc.grammar.GProduction;
import org.jlato.tree.NodeList;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.IfStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.Type;

import java.util.*;
import java.util.stream.Collectors;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ParserPattern extends TypePattern.OfClass<TreeClassDescriptor[]> {

	public static final String NAME = "ParserImplementation";
	public static final Name MATCH = name("match");
	public static final Name LOOKAHEAD = name("lookahead");
	public static final LiteralExpr<Integer> FAILED_LOOKAHEAD = literalExpr(-1);
	public static final Name LOOKAHEAD_NEW = name("newLookahead");

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "public class " + NAME + " extends ParserNewBase { ..$_ }";
	}

	private Set<String> symbolToMatchNames = new HashSet<>();

	private NodeList<MethodDecl> parseMethods = emptyList();
	private Map<String, Integer> perSymbolLookaheadMethodCount = new HashMap<>();
	private Map<String, List<MethodDecl>> perSymbolMatchMethods = new HashMap<>();

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
				importDecl(qualifiedName("org.jlato.internal.parser.Token")),
				importDecl(qualifiedName("org.jlato.internal.parser.TokenType")),
				importDecl(qualifiedName("org.jlato.tree.Problem.Severity")),
				importDecl(qualifiedName("org.jlato.parser.ParserInterface.TypeKind")),
				importDecl(qualifiedName("org.jlato.parser.ParseException")),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.decl.ModifierKeyword")),
				importDecl(qualifiedName("org.jlato.tree.type.Primitive"))
		));

		for (GProduction production : Grammar.productions.getAll()) {
			if (excluded(production)) continue;

			parseMethods = parseMethods.append(parseMethod(importManager, production));
		}

		NodeList<MemberDecl> members = Trees.emptyList();
		for (MethodDecl parseMethod : parseMethods) {
			members = members.append(parseMethod);

			String id = parseMethod.name().id();
			int indexOfUnderscore = id.indexOf('_');
			String symbol = id.substring("parse".length(), indexOfUnderscore == -1 ? id.length() : indexOfUnderscore);

			List<MethodDecl> methods = perSymbolMatchMethods.get(symbol);
			if (methods != null) {
				Collections.sort(methods, (m1, m2) -> m1.name().id().compareTo(m2.name().id()));
				members = members.appendAll(listOf(methods));
			}
		}

		return decl.withMembers(members);
	}

	private MethodDecl parseMethod(ImportManager importManager, GProduction production) {
		Type type = production.returnType;

		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(production.declarations);
		stmts = stmts.appendAll(parseStatementsFor(production.symbol, production.expansion));

		return methodDecl(type, name("parse" + upperCaseFirst(production.symbol)))
				.withModifiers(listOf(Modifier.Protected))
				.withParams(production.hintParams.appendAll(production.dataParams))
				.withThrowsClause(listOf(qualifiedType(name("ParseException"))))
				.withBody(blockStmt().withStmts(stmts))
				.insertLeadingComment(production.expansion.toString(), true);
	}

	private NodeList<Stmt> parseStatementsFor(String symbol, GExpansion expansion) {
		NodeList<Stmt> stmts = emptyList();
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(parseStatementsForChildren(symbol, expansion));
				break;
			case ZeroOrOne: {
				stmts = stmts.append(ifStmt(
						matchCondition(symbol, expansion),
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(whileStmt(
						matchCondition(symbol, expansion),
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion)),
						matchCondition(symbol, expansion)
				));
				break;
			}
			case Choice: {
				List<IfStmt> expansionsIfStmt = expansion.children.stream()
						.map(e -> ifStmt(
								matchCondition(symbol, e),
								blockStmt().withStmts(parseStatementsFor(symbol, e))
						))
						.collect(Collectors.toList());
				Collections.reverse(expansionsIfStmt);

				stmts = stmts.append(listOf(expansionsIfStmt).foldRight(
						(Stmt) blockStmt().withStmts(listOf(throwStmt(
								methodInvocationExpr(name("produceParseException"))
										.withArgs(listOf(firstTerminalsOf(expansion).stream().map(this::prefixedConstant).collect(Collectors.toList())))
						))),
						(ifThenClause, elseClause) -> ifThenClause.withElseStmt(elseClause)
				));
				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("parse" + upperCaseFirst(expansion.symbol)))
						.withArgs(expansion.hints.appendAll(expansion.arguments));
				stmts = stmts.append(expressionStmt(
						expansion.name == null ? call :
								assignExpr(name(expansion.name), AssignOp.Normal, call)
				));
				break;
			}
			case Terminal: {
				Expr argument = prefixedConstant(expansion.symbol);
				Expr call = methodInvocationExpr(name("parse")).withArgs(listOf(argument));
				stmts = stmts.append(expressionStmt(
						expansion.name == null ? call :
								assignExpr(name(expansion.name), AssignOp.Normal, call)
				));
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

	private NodeList<Stmt> parseStatementsForChildren(String symbol, GExpansion expansion) {
		NodeList<Stmt> stmts = emptyList();
		for (GExpansion child : expansion.children) {
			stmts = stmts.appendAll(parseStatementsFor(symbol, child));
		}
		return stmts;
	}

	private Expr matchCondition(String symbol, GExpansion expansion) {
		if (expansion.children != null && !expansion.children.isEmpty()) {
			GExpansion firstChild = expansion.children.get(0);
			if (firstChild.kind == GExpansion.Kind.LookAhead) {
				Expr semanticLookahead = firstChild.semanticLookahead;
				if (semanticLookahead != null) {
					if (semanticLookahead.equals(methodInvocationExpr(name("isLambda")))) {
						semanticLookahead = methodInvocationExpr(name("isLambda")).withArgs(listOf(literalExpr(0)));
					}
					return semanticLookahead;
				} else {
					int amount = firstChild.amount;
					if (amount == -1) {
						String matchMethodName = "match" + symbol + "_lookahead" + incrementCount(symbol);
						Expr call = createMatchMethodAndCallFor(symbol, matchMethodName, GExpansion.sequence(firstChild.children), literalExpr(0), emptyList(), emptyList());
						return binaryExpr(call, BinaryOp.NotEqual, literalExpr(-1));
					} else {
						return buildLookaheadWithAmountCondition(symbol, expansion, amount);
					}
				}
			}
		}
		return binaryExpr(matchCall(firstTerminalsOf(expansion), literalExpr(0)), BinaryOp.NotEqual, FAILED_LOOKAHEAD);
	}

	private int incrementCount(String symbol) {
		Integer count = perSymbolLookaheadMethodCount.get(symbol);
		int newCount = (count == null ? 0 : count) + 1;
		perSymbolLookaheadMethodCount.put(symbol, newCount);
		return newCount;
	}

	private Expr buildLookaheadWithAmountCondition(String symbol, GExpansion expansion, int amount) {
		String matchMethodName = "match" + symbol + "_lookahead" + incrementCount(symbol);
		NodeList<Stmt> stmts = buildLookaheadWithAmountCondition(Collections.singletonList(expansion.location()), 0, amount);
		stmts = stmts.append(returnStmt().withExpr(FAILED_LOOKAHEAD));
		createMatchMethod(symbol, matchMethodName, expansion, stmts, emptyList());
		return binaryExpr(matchMethodCall(matchMethodName, literalExpr(0), emptyList()), BinaryOp.NotEqual, FAILED_LOOKAHEAD);
	}

	private NodeList<Stmt> buildLookaheadWithAmountCondition(List<GLocation> location, int lookahead, int amount) {
		NodeList<Stmt> stmts = emptyList();
		if (amount == 0) {
			stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
		} else {
			GContinuations c = new GContinuations(location, Grammar.productions, lookahead > 0);
			c.next();

			Map<String, List<GLocation>> terminals = c.perTerminalLocations();
			if (terminals.isEmpty()) {
				stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
			} else {
				for (Map.Entry<String, List<GLocation>> entry : terminals.entrySet()) {
					String terminal = entry.getKey();
					List<GLocation> following = entry.getValue();

					stmts = stmts.append(ifStmt(
							binaryExpr(matchCall(terminal, literalExpr(lookahead)), BinaryOp.NotEqual, FAILED_LOOKAHEAD),
							blockStmt().withStmts(buildLookaheadWithAmountCondition(following, lookahead + 1, amount - 1))
					));
				}
			}
		}
		return stmts;
	}

	private Expr createMatchMethodAndCallFor(String symbol, Expr outerLookahead, NodeList<Expr> args) {
		if (!symbolToMatchNames.contains(symbol)) {
			symbolToMatchNames.add(symbol);
			GProduction production = Grammar.productions.get(symbol);
			GExpansion symbolExpansion = production.expansion;
			return createMatchMethodAndCallFor(symbol, matchMethodName(symbol), symbolExpansion, outerLookahead, production.hintParams, args);
		} else return matchMethodCall(matchMethodName(symbol), outerLookahead, args);
	}

	private Expr createMatchMethodAndCallFor(String symbol, String namePrefix, GExpansion expansion, Expr outerLookahead, NodeList<FormalParameter> params, NodeList<Expr> args) {
		switch (expansion.kind) {
			case LookAhead:
				Expr semanticLookahead = expansion.semanticLookahead;
				if (semanticLookahead != null) {
					if (semanticLookahead.equals(methodInvocationExpr(name("isLambda")))) {
						semanticLookahead = methodInvocationExpr(name("isLambda")).withArgs(listOf(outerLookahead));
					}
					return conditionalExpr(semanticLookahead, outerLookahead, FAILED_LOOKAHEAD);
				}
				return null;
			case Sequence: {
				NodeList<Stmt> stmts = emptyList();
				int count = 0;
				for (GExpansion child : expansion.children) {
					Expr childCall = createMatchMethodAndCallFor(symbol, namePrefix + "_" + ++count, child, LOOKAHEAD, params, params.map(p -> p.id().name()));
					if (childCall == null) continue;
					stmts = stmts.append(expressionStmt(assignExpr(LOOKAHEAD, AssignOp.Normal, childCall)));
					stmts = stmts.append(ifStmt(binaryExpr(LOOKAHEAD, BinaryOp.Equal, FAILED_LOOKAHEAD), returnStmt().withExpr(FAILED_LOOKAHEAD)));
				}
				stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
				createMatchMethod(symbol, namePrefix, expansion, stmts, params);

				return matchMethodCall(namePrefix, outerLookahead, args);
			}
			case Choice: {
				NodeList<Stmt> stmts = emptyList();
				stmts = stmts.append(expressionStmt(variableDeclarationExpr(
						localVariableDecl(primitiveType(Primitive.Int))
								.withVariables(listOf(variableDeclarator(variableDeclaratorId(LOOKAHEAD_NEW))))
				)));
				int count = 0;
				for (GExpansion child : expansion.children) {
					Expr childCall = createMatchMethodAndCallFor(symbol, namePrefix + "_" + ++count, child, LOOKAHEAD, params, params.map(p -> p.id().name()));
					if (childCall == null) continue;
					stmts = stmts.append(expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall)));
					stmts = stmts.append(ifStmt(binaryExpr(LOOKAHEAD_NEW, BinaryOp.NotEqual, FAILED_LOOKAHEAD), returnStmt().withExpr(LOOKAHEAD_NEW)));
				}
				stmts = stmts.append(returnStmt().withExpr(FAILED_LOOKAHEAD));
				createMatchMethod(symbol, namePrefix, expansion, stmts, params);

				return matchMethodCall(namePrefix, outerLookahead, args);
			}
			case ZeroOrOne: {
				NodeList<Stmt> stmts = emptyList();
				stmts = stmts.append(expressionStmt(variableDeclarationExpr(
						localVariableDecl(primitiveType(Primitive.Int))
								.withVariables(listOf(variableDeclarator(variableDeclaratorId(LOOKAHEAD_NEW))))
				)));

				Expr childCall = createMatchMethodAndCallFor(symbol, namePrefix + "_" + 1, GExpansion.sequence(expansion.children), LOOKAHEAD, params, params.map(p -> p.id().name()));
				stmts = stmts.append(expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall)));
				stmts = stmts.append(ifStmt(binaryExpr(LOOKAHEAD_NEW, BinaryOp.NotEqual, FAILED_LOOKAHEAD), returnStmt().withExpr(LOOKAHEAD_NEW)));

				stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
				createMatchMethod(symbol, namePrefix, expansion, stmts, params);

				return matchMethodCall(namePrefix, outerLookahead, args);
			}
			case ZeroOrMore: {
				NodeList<Stmt> stmts = emptyList();
				stmts = stmts.append(expressionStmt(variableDeclarationExpr(
						localVariableDecl(primitiveType(Primitive.Int))
								.withVariables(listOf(variableDeclarator(variableDeclaratorId(LOOKAHEAD_NEW))))
				)));

				Expr childCall = createMatchMethodAndCallFor(symbol, namePrefix + "_" + 1, GExpansion.sequence(expansion.children), LOOKAHEAD, params, params.map(p -> p.id().name()));
				stmts = stmts.append(expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall)));
				stmts = stmts.append(whileStmt(
						binaryExpr(LOOKAHEAD_NEW, BinaryOp.NotEqual, FAILED_LOOKAHEAD),
						blockStmt().withStmts(listOf(
								expressionStmt(assignExpr(LOOKAHEAD, AssignOp.Normal, LOOKAHEAD_NEW)),
								expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall))
						))
				));
				stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
				createMatchMethod(symbol, namePrefix, expansion, stmts, params);

				return matchMethodCall(namePrefix, outerLookahead, args);
			}
			case OneOrMore: {
				NodeList<Stmt> stmts = emptyList();
				stmts = stmts.append(expressionStmt(variableDeclarationExpr(
						localVariableDecl(primitiveType(Primitive.Int))
								.withVariables(listOf(variableDeclarator(variableDeclaratorId(LOOKAHEAD_NEW))))
				)));

				Expr childCall = createMatchMethodAndCallFor(symbol, namePrefix + "_" + 1, GExpansion.sequence(expansion.children), LOOKAHEAD, params, params.map(p -> p.id().name()));
				stmts = stmts.append(expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall)));
				stmts = stmts.append(ifStmt(binaryExpr(LOOKAHEAD_NEW, BinaryOp.Equal, FAILED_LOOKAHEAD), returnStmt().withExpr(FAILED_LOOKAHEAD)));
				stmts = stmts.append(whileStmt(
						binaryExpr(LOOKAHEAD_NEW, BinaryOp.NotEqual, FAILED_LOOKAHEAD),
						blockStmt().withStmts(listOf(
								expressionStmt(assignExpr(LOOKAHEAD, AssignOp.Normal, LOOKAHEAD_NEW)),
								expressionStmt(assignExpr(LOOKAHEAD_NEW, AssignOp.Normal, childCall))
						))
				));
				stmts = stmts.append(returnStmt().withExpr(LOOKAHEAD));
				createMatchMethod(symbol, namePrefix, expansion, stmts, params);

				return matchMethodCall(namePrefix, outerLookahead, args);
			}
			case NonTerminal: {
				return createMatchMethodAndCallFor(expansion.symbol, outerLookahead, expansion.hints);
			}
			case Terminal: {
				return matchCall(expansion.symbol, LOOKAHEAD);
			}
			case Action: {
				return null;
			}
			default:
		}
		return null;
	}

	private void createMatchMethod(String symbol, String namePrefix, GExpansion expansion, NodeList<Stmt> stmts, NodeList<FormalParameter> params) {
		List<MethodDecl> methods = perSymbolMatchMethods.get(symbol);
		if (methods == null) {
			methods = new ArrayList<>();
			perSymbolMatchMethods.put(symbol, methods);
		}
		methods.add(matchMethod(namePrefix, stmts, expansion, params));
	}

	private String matchMethodName(String symbol) {
		return "match" + upperCaseFirst(symbol);
	}

	private MethodDecl matchMethod(String methodName, NodeList<Stmt> stmts, GExpansion expansion, NodeList<FormalParameter> params) {
		return methodDecl(primitiveType(Primitive.Int), name(methodName))
				.withModifiers(listOf(Modifier.Private))
				.withParams(
						listOf(formalParameter(primitiveType(Primitive.Int), variableDeclaratorId(LOOKAHEAD)))
								.appendAll(params)
				)
				.withBody(blockStmt().withStmts(stmts))
				.insertLeadingComment(expansion.toString(e -> e.kind != GExpansion.Kind.Action), true);
	}

	private MethodInvocationExpr matchMethodCall(String methodName, Expr lookahead, NodeList<Expr> args) {
		return methodInvocationExpr(name(methodName)).withArgs(listOf(lookahead).appendAll(args));
	}

	private MethodInvocationExpr matchCall(List<String> tokens, Expr lookahead) {
		return methodInvocationExpr(MATCH).withArgs(listOf(prefixedConstants(tokens)).prepend(lookahead));
	}

	private MethodInvocationExpr matchCall(String token, Expr lookahead) {
		return methodInvocationExpr(MATCH).withArgs(listOf(lookahead, prefixedConstant(token)));
	}

	private List<Expr> prefixedConstants(List<String> tokens) {
		return tokens.stream()
				.filter(t -> t != null)
				.map(t -> prefixedConstant(t))
				.collect(Collectors.toList());
	}

	private FieldAccessExpr prefixedConstant(String token) {
		return fieldAccessExpr(name(token)).withScope(name("TokenType"));
	}

	private List<String> firstTerminalsOf(GExpansion expansion) {
		GContinuations c = new GContinuations(expansion.location(), Grammar.productions, false);
		c.next();
		return c.terminals();
	}

	protected boolean excluded(GProduction production) {
		return false;
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor[] arg) {
		return "Internal implementation of the Java parser as a recursive descent parser.";
	}
}
