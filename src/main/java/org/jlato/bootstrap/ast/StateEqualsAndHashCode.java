package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.rewrite.Substitution;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.FieldAccessExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.Type;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.*;
import static org.jlato.tree.Trees.*;
import static org.jlato.tree.expr.BinaryOp.Equal;
import static org.jlato.tree.expr.BinaryOp.NotEqual;
import static org.jlato.tree.expr.UnaryOp.Not;

/**
 * @author Didier Villevalois
 */
public class StateEqualsAndHashCode implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		return Arrays.asList(
				new EqualsMethod(),
				new HashCodeMethod()
		);
	}

	public static class EqualsMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("@Override\npublic boolean equals(Object o) { ..$_ }");
		}

		public static final Name EQUALS = name("equals");

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> params = arg.parameters;

			NodeList<Stmt> stmts = emptyList();

			stmts = stmts.appendAll(listOf(
					stmt("if (this == o)\nreturn true;").build(),
					stmt("if (o == null || getClass() != o.getClass())\nreturn false;").build()
			));

			if (!params.isEmpty()) {
				stmts = stmts.append(
						stmt(arg.stateTypeName() + " state = (" + arg.stateTypeName() + ") o;").build()
				);

				final Name state = name("state");

				stmts = stmts.appendAll(params.map(p -> {
					final Name thisField = p.id().name();
					final FieldAccessExpr otherField = fieldAccessExpr(thisField).withScope(state);

					Expr equalTest = p.type() instanceof PrimitiveType ?
							binaryExpr(thisField, NotEqual, otherField) :
							unaryExpr(Not, methodInvocationExpr(EQUALS).withScope(thisField).withArgs(listOf(otherField)));

					if (nullable(p)) {
						equalTest = conditionalExpr(
								binaryExpr(thisField, Equal, nullLiteralExpr()),
								binaryExpr(otherField, NotEqual, nullLiteralExpr()),
								equalTest);
					}

					return ifStmt(equalTest, returnStmt().withExpr(literalExpr(false)));
				}));
			}

			stmts = stmts.append(returnStmt().withExpr(literalExpr(true)));

			decl = decl.withBody(blockStmt().withStmts(stmts));

			if (GenSettings.generateDocs)
				decl = decl.withDocComment(
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
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("@Override\npublic int hashCode() { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			NodeList<Stmt> stmts = emptyList();

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
					hashStmt = ifStmt(binaryExpr(thisField, NotEqual, nullLiteralExpr()), hashStmt);
				}
				stmts = stmts.append(hashStmt);
			}

			stmts = stmts.append(stmt("return result;").build());

			decl = decl.withBody(blockStmt().withStmts(stmts));

			if (GenSettings.generateDocs)
				decl = decl.withDocComment(
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
