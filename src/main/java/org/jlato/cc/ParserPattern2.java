package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.*;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.*;
import org.jlato.tree.stmt.ExpressionStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.stmt.SwitchCase;
import org.jlato.tree.type.Type;

import java.util.*;
import java.util.stream.Collectors;

import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ParserPattern2 extends TypePattern.OfClass<TreeClassDescriptor[]> {

	private final GProductions productions;
	private final String implementationName;

	public ParserPattern2(GProductions productions, String implementationName) {
		this.productions = productions;
		this.implementationName = implementationName;
	}

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "public class " + implementationName + " extends ParserNewBase2 { ..$_ }";
	}

	private Map<String, Integer> perSymbolLookaheadMethodCount = new HashMap<>();

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
				importDecl(qualifiedName("org.jlato.parser.ParseException")),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.decl.ModifierKeyword")),
				importDecl(qualifiedName("org.jlato.tree.type.Primitive"))
		));

		List<GProduction> allProductions = productions.getAll();

		NodeList<MemberDecl> members = Trees.emptyList();
		members = members.append(memberDecl("protected Grammar initializeGrammar() { return new JavaGrammar(); }").build());

		members = members.append(grammarClass(importManager, allProductions));

		for (GProduction production : allProductions) {
			if (excluded(production)) continue;
			members = members.append(parseMethod(importManager, production));
		}

		return decl.withMembers(members);
	}

	/* ALL(*) Grammar declaration */

	private MemberDecl grammarClass(ImportManager importManager, List<GProduction> allProductions) {
		importManager.addImports(listOf(
				importDecl(qualifiedName("org.jlato.internal.parser.all.Grammar")),
				importDecl(qualifiedName("org.jlato.internal.parser.TokenType"))
		));

		NodeList<MemberDecl> members = Trees.emptyList();

		Map<String, MemberDecl> constants = new TreeMap<>();
		for (GProduction production : allProductions) {
			grammarConstants(production, constants);
		}
		for (MemberDecl memberDecl : constants.values()) {
			members = members.append(memberDecl);
		}

		for (GProduction production : allProductions) {
			members = grammarElements(production.symbol, production.expansion, members);
		}

		// Make initializePreductions method
		NodeList<Stmt> stmts = emptyList();
		for (GProduction production : allProductions) {
			stmts = grammarDefStmts(production, stmts);
		}

		members = members.append(
				methodDecl(voidType(), name("initializeProductions"))
						.withModifiers(listOf(Modifier.Protected))
						.withBody(blockStmt().withStmts(stmts))
		);

		return classDecl(name("JavaGrammar")).withExtendsClause(qType("Grammar"))
				.withModifiers(listOf(Modifier.Static))
				.withMembers(members);
	}

	static int constantCount = 1;

	private void grammarConstants(GProduction production, Map<String, MemberDecl> members) {
		String name = camelToConstant(lowerCaseFirst(production.symbol));
		if (!members.containsKey(name)) {
			members.put(name, fieldDecl(qType("int"))
					.withModifiers(listOf(Modifier.Public, Modifier.Static, Modifier.Final))
					.withVariables(listOf(
							variableDeclarator(variableDeclaratorId(name(name)))
									.withInit(literalExpr(constantCount++))
					))
			);
		}

		grammarConstants(production.symbol, production.expansion, members);
	}

	private void grammarConstants(String namePrefix, GExpansion expansion, Map<String, MemberDecl> members) {
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore: {
				String name = camelToConstant(lowerCaseFirst(namePrefix));
				if (!members.containsKey(name)) {
					members.put(name, fieldDecl(qType("int"))
							.withModifiers(listOf(Modifier.Public, Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(name)))
											.withInit(literalExpr(constantCount++))
							))
					);
				}
			}
			case Sequence: {
				int count = 1;
				for (GExpansion child : expansion.children) {
					if (lookaheadOrAction(child)) continue;

					String name = namePrefix + '_' + count++;
					grammarConstants(name, child, members);
				}
				break;
			}
			case NonTerminal: {
				String name = camelToConstant(lowerCaseFirst(expansion.symbol));
				if (!members.containsKey(name)) {
					members.put(name, fieldDecl(qType("int"))
							.withModifiers(listOf(Modifier.Public, Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(name)))
											.withInit(literalExpr(constantCount++))
							))
					);
				}
				break;
			}
			default:
		}
	}

	private NodeList<MemberDecl> grammarElements(String namePrefix, GExpansion expansion, NodeList<MemberDecl> members) {
		switch (expansion.kind) {
			case Choice: {
				int count = 1;
				NodeList<Expr> childrenExprs = emptyList();
				for (GExpansion child : expansion.children) {
					if (lookaheadOrAction(child)) continue;

					String name = namePrefix + '_' + count++;
					childrenExprs = grammarElementExpression(child, name, childrenExprs);
					members = grammarElements(name, child, members);
				}

				members = members.append(fieldDecl(qType("Choice"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
										methodInvocationExpr(name("choice"))
												.withArgs(childrenExprs.prepend(literalExpr(namePrefix)))
								)
						))
				);
				break;
			}
			case Sequence: {
				int count = 1;
				NodeList<Expr> childrenExprs = emptyList();
				for (GExpansion child : expansion.children) {
					if (lookaheadOrAction(child)) continue;

					String name = namePrefix + '_' + count++;
					childrenExprs = grammarElementExpression(child, name, childrenExprs);
					members = grammarElements(name, child, members);
				}

				if (!namePrefix.contains("_")) {
					members = members.append(fieldDecl(qType("Sequence"))
							.withModifiers(listOf(Modifier.Static, Modifier.Final))
							.withVariables(listOf(
									variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
											methodInvocationExpr(name("sequence"))
													.withArgs(childrenExprs.prepend(literalExpr(namePrefix)))
									)
							))
					);
				}
				break;
			}
			case ZeroOrOne: {
				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = makeSequence(expansion);
				String name = namePrefix + (child.kind != GExpansion.Kind.Sequence && child != expansion ? "_1" : "");

				childrenExprs = grammarElementExpression(child, name, childrenExprs);
				members = grammarElements(name, child, members);

				members = members.append(fieldDecl(qType("ZeroOrOne"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
										methodInvocationExpr(name("zeroOrOne"))
												.withArgs(childrenExprs.prepend(literalExpr(namePrefix)))
								)
						))
				);
				break;
			}
			case ZeroOrMore: {
				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = makeSequence(expansion);
				String name = namePrefix + (child.kind != GExpansion.Kind.Sequence && child != expansion ? "_1" : "");

				childrenExprs = grammarElementExpression(child, name, childrenExprs);
				members = grammarElements(name, child, members);

				members = members.append(fieldDecl(qType("ZeroOrMore"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
										methodInvocationExpr(name("zeroOrMore"))
												.withArgs(childrenExprs.prepend(literalExpr(namePrefix)))
								)
						))
				);
				break;
			}
			case OneOrMore: {
				NodeList<Expr> childrenExprs = emptyList();
				GExpansion child = makeSequence(expansion);
				String name = namePrefix + (child.kind != GExpansion.Kind.Sequence && child != expansion ? "_1" : "");

				childrenExprs = grammarElementExpression(child, name, childrenExprs);
				members = grammarElements(name, child, members);

				members = members.append(fieldDecl(qType("OneOrMore"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
										methodInvocationExpr(name("oneOrMore"))
												.withArgs(childrenExprs.prepend(literalExpr(namePrefix)))
								)
						))
				);
				break;
			}
			case NonTerminal: {
				String ntConstantName = camelToConstant(lowerCaseFirst(expansion.symbol));
				members = members.append(fieldDecl(qType("NonTerminal"))
						.withModifiers(listOf(Modifier.Static, Modifier.Final))
						.withVariables(listOf(
								variableDeclarator(variableDeclaratorId(name(namePrefix))).withInit(
										methodInvocationExpr(name("nonTerminal"))
												.withArgs(listOf(literalExpr(namePrefix), name(ntConstantName)))
								)
						))
				);
				break;
			}
			default:
		}
		return members;
	}

	private boolean lookaheadOrAction(GExpansion expansion) {
		return expansion.kind == GExpansion.Kind.LookAhead || expansion.kind == GExpansion.Kind.Action;
	}

	private GExpansion makeSequence(GExpansion expansion) {
		GExpansion uniqueChild = null;
		for (GExpansion child : expansion.children) {
			switch (child.kind) {
				case LookAhead:
				case Action:
					break;
				default:
					if (uniqueChild == null) uniqueChild = child;
					else return GExpansion.sequence(expansion.children);
			}
		}
		return uniqueChild;
	}

	private List<GExpansion> filterLookaheadAndAction(List<GExpansion> children) {
		if (children == null) return null;

		List<GExpansion> filtered = new ArrayList<>();
		for (GExpansion child : children) {
			switch (child.kind) {
				case LookAhead:
				case Action:
					break;
				default:
					filtered.add(child);
			}
		}
		return filtered;
	}

	private NodeList<Expr> grammarElementExpression(GExpansion expansion, String namePrefix, NodeList<Expr> childrenExprs) {
		int count = 1;
		NodeList<Expr> localChildrenExprs = emptyList();
		switch (expansion.kind) {
			case Choice:
				childrenExprs = childrenExprs.append(name(namePrefix));
				break;
			case Sequence: {
				for (GExpansion child : expansion.children) {
					if (lookaheadOrAction(child)) continue;

					String name = namePrefix + '_' + count++;
					localChildrenExprs = grammarElementExpression(child, name, localChildrenExprs);
				}
				childrenExprs = childrenExprs.append(methodInvocationExpr(name("sequence"))
						.withArgs(localChildrenExprs.prepend(literalExpr(namePrefix))));
				break;
			}
			case ZeroOrOne:
				childrenExprs = childrenExprs.append(name(namePrefix));
				break;
			case ZeroOrMore:
				childrenExprs = childrenExprs.append(name(namePrefix));
				break;
			case OneOrMore:
				childrenExprs = childrenExprs.append(name(namePrefix));
				break;
			case NonTerminal: {
				childrenExprs = childrenExprs.append(name(namePrefix));
				break;
			}
			case Terminal: {
				String name = expansion.symbol;
				childrenExprs = childrenExprs.append(methodInvocationExpr(name("terminal"))
						.withArgs(listOf(literalExpr(namePrefix), fieldAccessExpr(name(name)).withScope(name("TokenType"))))
				);
				break;
			}
			default:
		}
		return childrenExprs;
	}

	private NodeList<Stmt> grammarDefStmts(GProduction production, NodeList<Stmt> stmts) {
		String symbol = production.symbol;
		String productionConstantName = camelToConstant(lowerCaseFirst(symbol));
		stmts = stmts.append(
				expressionStmt(
						methodInvocationExpr(name("addProduction")).withArgs(listOf(
								name(productionConstantName),
								name(symbol),
								literalExpr(symbol.endsWith("Entry") && !symbol.equals("SwitchEntry"))
						))
				)
		);

		stmts = grammarDefStmts(symbol, production.expansion, stmts);
		return stmts;
	}

	private NodeList<Stmt> grammarDefStmts(String namePrefix, GExpansion expansion, NodeList<Stmt> stmts) {
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore: {
				String constantName = camelToConstant(lowerCaseFirst(namePrefix));
				stmts = stmts.append(
						expressionStmt(
								methodInvocationExpr(name("addChoicePoint"))
										.withArgs(listOf(name(constantName), name(namePrefix)))
						)
				);
			}
			case Sequence: {
				int count = 1;
				for (GExpansion child : expansion.children) {
					if (lookaheadOrAction(child)) continue;

					String name = namePrefix + '_' + count++;
					stmts = grammarDefStmts(name, child, stmts);
				}
				break;
			}
			default:
		}
		return stmts;
	}

	/* Parse methods */

	private MethodDecl parseMethod(ImportManager importManager, GProduction production) {
		Type type = production.returnType;

		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(production.declarations);
		stmts = stmts.appendAll(parseStatementsFor(production.symbol, production.symbol, production.location(), production.hintParams, false));

		// Add push/pop callStack calls
		String ntName = camelToConstant(lowerCaseFirst(production.symbol));
		Expr constant = fieldAccessExpr(name(ntName)).withScope(name("JavaGrammar"));

		return methodDecl(type, name("parse" + upperCaseFirst(production.symbol)))
				.withModifiers(listOf(Modifier.Protected))
				.withParams(production.hintParams.appendAll(production.dataParams))
				.withThrowsClause(listOf(qualifiedType(name("ParseException"))))
				.withBody(blockStmt().withStmts(stmts))
				.appendLeadingComment(production.expansion.toString(), true);
	}

	private NodeList<Stmt> parseStatementsFor(String symbol, String namePrefix, GLocation location, NodeList<FormalParameter> hintParams, boolean optional) {
		NodeList<Stmt> stmts = emptyList();
		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(parseStatementsForChildren(symbol, namePrefix, location, hintParams, false));
				break;
			case ZeroOrOne: {
				if (expansion.children.size() == 1 && expansion.children.get(0).kind == GExpansion.Kind.Choice) {
					stmts = stmts.appendAll(parseStatementsForChildren(symbol, namePrefix, location, hintParams, true));
				} else {
					stmts = stmts.append(ifStmt(
							binaryExpr(predictChoice(namePrefix, hintParams, hintParams.map(p -> p.id().get().name())), BinaryOp.Equal, literalExpr(1)),
							blockStmt().withStmts(parseStatementsForChildren(symbol, namePrefix, location, hintParams, false))
					));
				}
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(whileStmt(
						binaryExpr(predictChoice(namePrefix, hintParams, hintParams.map(p -> p.id().get().name())), BinaryOp.Equal, literalExpr(1)),
						blockStmt().withStmts(parseStatementsForChildren(symbol, namePrefix, location, hintParams, false))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(parseStatementsForChildren(symbol, namePrefix, location, hintParams, false)),
						binaryExpr(predictChoice(namePrefix, hintParams, hintParams.map(p -> p.id().get().name())), BinaryOp.Equal, literalExpr(1))
				));
				break;
			}
			case Choice: {
				Expr selector = predictChoice(namePrefix, hintParams, hintParams.map(p -> p.id().get().name()));

				NodeList<SwitchCase> cases = emptyList();

				int count = 1;
				for (GLocation child : location.allChildren()) {
					LiteralExpr<Integer> label = literalExpr(count);
					String name = namePrefix + "_" + count++;

					NodeList<Stmt> caseStmts = parseStatementsFor(symbol, name, child, hintParams, optional);
					if (caseStmts.last().kind() != Kind.ReturnStmt) caseStmts = caseStmts.append(breakStmt());

					cases = cases.append(switchCase().withLabel(label).withStmts(caseStmts));
				}

				if (!optional) {
					cases = cases.append(switchCase().withStmts(listOf(throwStmt(
							methodInvocationExpr(name("produceParseException"))
									.withArgs(listOf(firstTerminalsOf(location).stream().map(this::prefixedConstant).collect(Collectors.toList())))
					))));
				}

				stmts = stmts.append(switchStmt(selector).withCases(cases));

				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("parse" + upperCaseFirst(expansion.symbol)))
						.withArgs(expansion.hints.appendAll(expansion.arguments));
				ExpressionStmt callStmt = expressionStmt(
						expansion.name == null ? call : assignExpr(name(expansion.name), AssignOp.Normal, call)
				);

				stmts = stmts.append(pushCallStack(namePrefix));
				stmts = stmts.append(callStmt);
				stmts = stmts.append(popCallStack());
				break;
			}
			case Terminal: {
				Expr argument = prefixedConstant(expansion.symbol);
				Expr call = methodInvocationExpr(name("parse")).withArgs(listOf(argument));
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

	private NodeList<Stmt> parseStatementsForChildren(String symbol, String namePrefix, GLocation location, NodeList<FormalParameter> hintParams, boolean optional) {
		int count = 1;
		NodeList<Stmt> stmts = emptyList();
		for (GLocation child : location.allChildren()) {
			boolean lookaheadOrAction = lookaheadOrAction(child.current);
			String name = namePrefix + (lookaheadOrAction ? "" : "_" + count++);

			stmts = stmts.appendAll(parseStatementsFor(symbol, name, child, hintParams, optional));
		}
		return stmts;
	}

	private Expr predictChoice(String namePrefix, NodeList<FormalParameter> params, NodeList<Expr> args) {
		String name = camelToConstant(lowerCaseFirst(namePrefix));
		Expr constant = fieldAccessExpr(name(name)).withScope(name("JavaGrammar"));

		return methodInvocationExpr(name("predict")).withArgs(listOf(constant));
	}

	private Stmt pushCallStack(String name) {
		Expr constant = fieldAccessExpr(name(name)).withScope(name("JavaGrammar"));

		return expressionStmt(methodInvocationExpr(name("pushCallStack")).withArgs(listOf(constant)));
	}

	private Stmt popCallStack() {
		return expressionStmt(methodInvocationExpr(name("popCallStack")));
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

	private List<String> firstTerminalsOf(GLocation location) {
		GContinuations c = new GContinuations(location, productions, false);
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
