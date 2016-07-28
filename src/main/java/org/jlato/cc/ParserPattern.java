package org.jlato.cc;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.cc.grammar.GContinuations;
import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Matcher;
import org.jlato.rewrite.Quotes;
import org.jlato.rewrite.Substitution;
import org.jlato.tree.Kind;
import org.jlato.tree.NodeList;
import org.jlato.tree.Tree;
import org.jlato.tree.Trees;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.decl.Modifier;
import org.jlato.tree.expr.AssignOp;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.MethodInvocationExpr;
import org.jlato.tree.expr.UnaryOp;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.ExpressionStmt;
import org.jlato.tree.stmt.IfStmt;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.Primitive;
import org.jlato.tree.type.Type;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ParserPattern extends TypePattern.OfClass<TreeClassDescriptor[]> {

	public static final String NAME = "ParserImplementation";
	public static final Name MATCH_NEXT = name("matchNext");

	@Override
	protected String makeQuote(TreeClassDescriptor[] arg) {
		return "public class " + NAME + " extends ParserNewBase { ..$_ }";
	}

	private NodeList<MethodDecl> matchMethods = emptyList();
	private Set<String> symbolToMatchNames = new HashSet<>();

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
				importDecl(qualifiedName("org.jlato.tree.Problem.Severity")),
				importDecl(qualifiedName("org.jlato.parser.ParseException")),
				importDecl(qualifiedName("org.jlato.parser.ParserImplConstants")),
				importDecl(qualifiedName("org.jlato.parser.Token")),
				importDecl(qualifiedName("org.jlato.tree.expr.AssignOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.BinaryOp")),
				importDecl(qualifiedName("org.jlato.tree.expr.UnaryOp")),
				importDecl(qualifiedName("org.jlato.tree.decl.ModifierKeyword")),
				importDecl(qualifiedName("org.jlato.tree.type.Primitive"))
		));

		NodeList<MemberDecl> members = Trees.emptyList();

		for (GProduction production : Grammar.productions.getAll()) {
			if (excluded(production)) continue;

			members = members.append(parseMethod(importManager, production));
			members = members.appendAll(matchMethods);
			matchMethods = emptyList();
		}

		HashSet<String> done = new HashSet<>();

		while (!symbolToMatchNames.isEmpty()) {
			done.addAll(symbolToMatchNames);

			HashSet<String> copy = new HashSet<>(symbolToMatchNames);
			symbolToMatchNames.clear();
			for (String symbol : copy) {
				members = members.append(matchMethod(importManager, Grammar.productions.get(symbol)));
//				members = members.appendAll(matchMethods);
				matchMethods = emptyList();
			}

			symbolToMatchNames.removeAll(done);
		}

		return decl.withMembers(members);
	}

	private MethodDecl parseMethod(ImportManager importManager, GProduction production) {
		Type type = production.signature.type();

		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(production.declarations);
		stmts = stmts.appendAll(parseStatementsFor(production.symbol, production.expansion));

		return methodDecl(type, name("parse" + upperCaseFirst(production.symbol)))
				.withModifiers(listOf(Modifier.Public))
				.withParams(production.signature.params())
				.withThrowsClause(listOf(qualifiedType(name("ParseException"))))
				.withBody(blockStmt().withStmts(stmts));
	}

	private NodeList<Stmt> parseStatementsFor(String symbol, GExpansion expansion) {
		NodeList<Stmt> stmts = emptyList();
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(parseStatementsForChildren(symbol, expansion));
				break;
			case ZeroOrOne: {
				stmts = stmts.append(ifStmt(
						backupLookahead(matchCondition(symbol, expansion)),
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(whileStmt(
						backupLookahead(matchCondition(symbol, expansion)),
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(parseStatementsForChildren(symbol, expansion)),
						backupLookahead(matchCondition(symbol, expansion))
				));
				break;
			}
			case Choice: {
				List<IfStmt> expansionsIfStmt = expansion.children.stream()
						.map(e -> ifStmt(
								backupLookahead(matchCondition(symbol, e)),
								blockStmt().withStmts(parseStatementsFor(symbol, e))
						))
						.collect(Collectors.toList());
				Collections.reverse(expansionsIfStmt);

				stmts = stmts.append(listOf(expansionsIfStmt).foldRight(Trees.<Stmt>none(),
						(ifThenClause, elseClause) ->
								elseClause.isNone() ? some(ifThenClause.thenStmt()) : some(ifThenClause.withElseStmt(elseClause))
				).get());
				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("parse" + upperCaseFirst(expansion.symbol)))
						.withArgs(expansion.arguments);
				stmts = stmts.append(expressionStmt(
						expansion.name == null ? call :
								assignExpr(name(expansion.name), AssignOp.Normal, call)
				));
				break;
			}
			case Terminal: {
				Expr argument = fieldAccessExpr(name(expansion.symbol)).withScope(name("ParserImplConstants"));
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

	private Expr backupLookahead(Expr expr) {
		return methodInvocationExpr(name("backupLookahead")).withArgs(listOf(expr));
	}

	private Expr matchCondition(String symbol, GExpansion expansion) {
		if (expansion.children != null && !expansion.children.isEmpty()) {
			GExpansion firstChild = expansion.children.get(0);
			if (firstChild.kind == GExpansion.Kind.LookAhead) {
				if (firstChild.semanticLookahead != null) {
					return firstChild.semanticLookahead;
				} else if (firstChild.amount == -1) {
					String matchMethodName = "match" + symbol + (matchMethods.size() + 1);

					NodeList<Stmt> stmts =
							matchStatementsForChildren(symbol, firstChild)
									.append(returnStmt().withExpr(literalExpr(true)));
					matchMethods = matchMethods.append(
							methodDecl(primitiveType(Primitive.Boolean), name(matchMethodName))
									.withModifiers(listOf(Modifier.Private))
									.withBody(blockStmt().withStmts(stmts)));

					return methodInvocationExpr(name(matchMethodName));
				} else {
					// TODO Match for expansion.amount of tokens
					return methodInvocationExpr(MATCH_NEXT)
							.withArgs(listOf(firstTerminalsOf(expansion).stream()
									.filter(t -> t != null)
									.map(t -> fieldAccessExpr(name(t)).withScope(name("ParserImplConstants")))
									.collect(Collectors.toSet())));
				}
			}
		}
		return methodInvocationExpr(MATCH_NEXT)
				.withArgs(listOf(firstTerminalsOf(expansion).stream()
						.filter(t -> t != null)
						.map(t -> fieldAccessExpr(name(t)).withScope(name("ParserImplConstants")))
						.collect(Collectors.toSet())));
	}

	private MethodDecl matchMethod(ImportManager importManager, GProduction production) {
		NodeList<Stmt> stmts = emptyList();
		stmts = stmts.appendAll(matchStatementsFor(production.symbol, production.expansion));
		stmts = stmts.append(returnStmt().withExpr(literalExpr(true)));

//		stmts = stmts.append(returnStmt().withExpr(matchCondition(production.symbol, production.expansion)));

		return methodDecl(primitiveType(Primitive.Boolean), name("match" + upperCaseFirst(production.symbol)))
				.withModifiers(listOf(Modifier.Private))
				.withBody(blockStmt().withStmts(stmts));
	}

	private NodeList<Stmt> matchStatementsFor(String symbol, GExpansion expansion) {
		NodeList<Stmt> stmts = emptyList();
		switch (expansion.kind) {
			case Sequence:
				stmts = stmts.appendAll(matchStatementsForChildren(symbol, expansion));
				break;
			case ZeroOrOne: {
				stmts = stmts.append(ifStmt(
						matchCondition(symbol, expansion),
						blockStmt().withStmts(matchStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case ZeroOrMore: {
				stmts = stmts.append(whileStmt(
						matchCondition(symbol, expansion),
						blockStmt().withStmts(matchStatementsForChildren(symbol, expansion))
				));
				break;
			}
			case OneOrMore: {
				stmts = stmts.append(doStmt(
						blockStmt().withStmts(matchStatementsForChildren(symbol, expansion)),
						matchCondition(symbol, expansion)
				));
				break;
			}
			case Choice: {
				List<IfStmt> expansionsIfStmt = expansion.children.stream()
						.map(e -> ifStmt(
								matchCondition(symbol, e),
								blockStmt().withStmts(matchStatementsFor(symbol, e))
						))
						.collect(Collectors.toList());
				Collections.reverse(expansionsIfStmt);

				stmts = stmts.append(listOf(expansionsIfStmt).foldRight(Trees.<Stmt>none(),
						(ifThenClause, elseClause) -> some(ifThenClause.withElseStmt(elseClause))
				).get());
				break;
			}
			case NonTerminal: {
				Expr call = methodInvocationExpr(name("match" + upperCaseFirst(expansion.symbol)));
				stmts = stmts.append(ifStmt(unaryExpr(UnaryOp.Not, call), returnStmt().withExpr(literalExpr(false))));
				symbolToMatchNames.add(expansion.symbol);
				break;
			}
			case Terminal: {
				Expr argument = fieldAccessExpr(name(expansion.symbol)).withScope(name("ParserImplConstants"));
				Expr call = methodInvocationExpr(name("match")).withArgs(listOf(argument));
				stmts = stmts.append(ifStmt(unaryExpr(UnaryOp.Not, call), returnStmt().withExpr(literalExpr(false))));
				break;
			}
			case Action: {
				break;
			}
			case LookAhead: {
				if (expansion.semanticLookahead != null) {
					stmts = stmts.append(ifStmt(unaryExpr(UnaryOp.Not, expansion.semanticLookahead), returnStmt().withExpr(literalExpr(false))));
				}
				break;
			}
			default:
		}
		return stmts;
	}

	private NodeList<Stmt> matchStatementsForChildren(String symbol, GExpansion expansion) {
		NodeList<Stmt> stmts = emptyList();
		for (GExpansion child : expansion.children) {
			stmts = stmts.appendAll(matchStatementsFor(symbol, child));
		}
		return stmts;
	}

	private Set<String> firstTerminalsOf(GExpansion expansion) {
		GContinuations c = new GContinuations(expansion.location(), Grammar.productions);
		c.next();
		return c.terminals();
	}

	protected boolean excluded(GProduction production) {
		return false;
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor[] arg) {
		return null;
	}
}
