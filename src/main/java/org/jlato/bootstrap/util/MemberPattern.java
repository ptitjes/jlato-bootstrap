package org.jlato.bootstrap.util;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.*;

import static org.jlato.rewrite.Quotes.memberDecl;

/**
 * @author Didier Villevalois
 */
public abstract class MemberPattern<A, M extends MemberDecl> extends Utils implements DeclPattern<A, M> {

	@Override
	public Pattern<? extends Decl> matcher(A arg) {
		return memberDecl(makeQuote(arg));
	}

	protected abstract String makeQuote(A arg);

	@Override
	public M rewrite(M decl, A arg) {
		decl = makeDecl(decl, arg);

		if (GenSettings.generateDocs) {
			final String doc = makeDoc(decl, arg);
			decl = insertJavadoc(decl, doc);
		}

		return decl;
	}

	protected abstract M makeDecl(M decl, A arg);

	protected abstract String makeDoc(M decl, A arg);

	protected abstract M insertJavadoc(M decl, String doc);

	public static abstract class OfField<A> extends MemberPattern<A, FieldDecl> {

		protected FieldDecl insertJavadoc(FieldDecl decl, String doc) {
			return decl.insertLeadingComment(doc);
		}
	}

	public static abstract class OfMethod<A> extends MemberPattern<A, MethodDecl> {

		protected MethodDecl insertJavadoc(MethodDecl decl, String doc) {
			return decl.insertLeadingComment(doc);
		}
	}

	public static abstract class OfConstructor<A> extends MemberPattern<A, ConstructorDecl> {

		protected ConstructorDecl insertJavadoc(ConstructorDecl decl, String doc) {
			return decl.insertLeadingComment(doc);
		}
	}
}
