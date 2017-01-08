package org.jlato.cc;

import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;

import static org.jlato.cc.JavaGrammar.stmts;
import static org.jlato.cc.grammar.GExpansion.*;
import static org.jlato.cc.grammar.GProduction.production;
import static org.jlato.tree.Trees.emptyList;

/**
 * @author Didier Villevalois
 */
public class LRETest {

	public static void main(String[] args) {
		eliminateTest("testGrammar1", testGrammar1);
		eliminateTest("testGrammar2", testGrammar2);
		eliminateTest("testGrammar3", testGrammar3);
	}

	private static void eliminateTest(String name, GProductions testGrammar) {
		System.out.println("Test on " + name);
		GProductions transformed = new LeftRecursionElimination().transform(testGrammar);
		for (GProduction production : transformed.getAll()) {
			System.out.println(production);
		}
		System.out.println();
		System.out.println();
	}

	static final GProductions grammar = new GProductions(
			production("Expr", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> lhs;",
							"BUTree<? extends SExpr> rhs;",
							"BUTree<? extends SExpr> name;"
					),
					choice(
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("*"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Times, rhs));")
							),
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("+"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Plus, rhs));")
							),
							sequence(
									nonTerminal("name", "Ident"),
									action("return name;")
							)
					)
			)
	);

	static final GProductions testGrammar1 = new GProductions(
			production("Expr", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> lhs;",
							"BUTree<? extends SExpr> rhs;",
							"BUTree<? extends SExpr> name;"
					),
					choice(
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("*"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Times, rhs));")
							),
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("+"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Plus, rhs));")
							),
							sequence(
									nonTerminal("name", "Ident"),
									action("return name;")
							)
					)
			)
	);

	static final GProductions testGrammar2 = new GProductions(
			production("Expr", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> lhs;",
							"BUTree<? extends SExpr> rhs;",
							"BUTree<? extends SExpr> name;"
					),
					choice(
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("*"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Times, rhs));")
							),
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("=").setRightAssociative(true),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Plus, rhs));")
							),
							sequence(
									nonTerminal("name", "Ident"),
									action("return name;")
							)
					)
			)
	);

	static final GProductions testGrammar3 = new GProductions(
			production("Expr", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> lhs;",
							"BUTree<? extends SExpr> rhs;",
							"BUTree<? extends SExpr> name;"
					),
					choice(
							sequence(
									action("run();"),
									terminal("-"),
									nonTerminal("expr", "Expr"),
									action("return dress(SUnaryExpr.make(UnaryOp.Negative, expr));")
							),
							sequence(
									action("run();"),
									nonTerminal("expr", "Expr"),
									terminal("!"),
									action("return dress(SUnaryExpr.make(UnaryOp.Not, expr));")
							),
							sequence(
									action("run();"),
									nonTerminal("lhs", "Expr"),
									terminal("*"),
									nonTerminal("rhs", "Expr"),
									action("return dress(SBinaryExpr.make(lhs, BinaryOp.Times, rhs));")
							),
							sequence(
									nonTerminal("name", "Ident"),
									action("return name;")
							)
					)
			)
	);
}
