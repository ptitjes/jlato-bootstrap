package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.rewrite.Substitution;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.FieldAccessExpr;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.Type;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.expr;
import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;
import static org.jlato.tree.TreeFactory.returnStmt;
import static org.jlato.tree.expr.BinaryExpr.BinaryOp.Equal;
import static org.jlato.tree.expr.BinaryExpr.BinaryOp.NotEqual;
import static org.jlato.tree.expr.UnaryExpr.UnaryOp.Not;

/**
 * @author Didier Villevalois
 */
public class StateEqualsAndHashCode implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations() {
		return Arrays.asList(
				new EqualsMethod(),
				new HashCodeMethod()
		);
	}

	public static class EqualsMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("@Override public boolean equals(Object o) { ..$_ }");
		}

		public static final Name EQUALS = new Name("equals");

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> params = arg.parameters;

			NodeList<Stmt> stmts = NodeList.empty();

			stmts = stmts.appendAll(NodeList.of(
					stmt("if (this == o)\nreturn true;").build(),
					stmt("if (o == null || getClass() != o.getClass())\nreturn false;").build()
			));

			if (!params.isEmpty()) {
				stmts = stmts.append(
						stmt("State state = (State) o;").build()
				);

				final Name state = new Name("state");

				stmts = stmts.appendAll(params.map(p -> {
					final Name thisField = p.id().name();
					final FieldAccessExpr otherField = fieldAccessExpr()
							.withScope(some(state))
							.withName(thisField);

					Expr equalTest = p.type() instanceof PrimitiveType ?
							binaryExpr().withLeft(thisField).withOp(NotEqual).withRight(otherField) :
							unaryExpr().withOp(Not).withExpr(
									methodInvocationExpr().withScope(some(thisField)).withName(EQUALS).withArgs(NodeList.of(otherField))
							);

					if (nullable(p)) {
						equalTest = conditionalExpr()
								.withCondition(binaryExpr().withLeft(thisField).withOp(Equal).withRight(LiteralExpr.nullLiteral()))
								.withThenExpr(binaryExpr().withLeft(otherField).withOp(NotEqual).withRight(LiteralExpr.nullLiteral()))
								.withElseExpr(equalTest);
					}

					return ifStmt().withCondition(equalTest).withThenStmt(returnStmt().withExpr(some(LiteralExpr.of(false))));
				}));
			}

			stmts = stmts.append(returnStmt().withExpr(some(LiteralExpr.of(true))));

			decl = decl.withBody(some(blockStmt().withStmts(stmts)));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Compares this state object to the specified object.",
								new String[]{"the object to compare this state with."},
								"<code>true</code> if the specified object is equal to this state, <code>false</code> otherwise."
						)
				);

			return decl;
		}
	}

	public static class HashCodeMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<MemberDecl> matcher(TreeClassDescriptor arg) {
			return memberDecl("@Override public int hashCode() { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {
			NodeList<Stmt> stmts = NodeList.empty();

			stmts = stmts.append(stmt("int result = 17;").build());

			for (FormalParameter param : arg.parameters) {
				final Type type = param.type();
				final Name thisField = param.id().name();

				Expr hashExpr = null;
				if (type instanceof PrimitiveType) {
					switch (((PrimitiveType) type).primitive()) {
						case Boolean:
							hashExpr = expr("($p ? 1 : 0)").build(Substitution.empty().bind("p", thisField));
							break;
						case Int:
							hashExpr = expr("$p").build(Substitution.empty().bind("p", thisField));
							break;
					}
				} else {
					hashExpr = expr("$p.hashCode()").build(Substitution.empty().bind("p", thisField));
				}

				if (hashExpr == null) throw new IllegalStateException();

				Stmt hashStmt = stmt("result = 37 * result + $h;").build(Substitution.empty().bind("h", hashExpr));
				if (nullable(param)) {
					hashStmt = ifStmt()
							.withCondition(binaryExpr().withLeft(thisField).withOp(NotEqual).withRight(LiteralExpr.nullLiteral()))
							.withThenStmt(hashStmt);
				}
				stmts = stmts.append(hashStmt);
			}

			stmts = stmts.append(stmt("return result;").build());

			decl = decl.withBody(some(blockStmt().withStmts(stmts)));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Returns a hash code for this state object.",
								new String[]{},
								"a hash code value for this object."
						)
				);

			return decl;
		}
	}
}
