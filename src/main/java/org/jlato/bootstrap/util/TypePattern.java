package org.jlato.bootstrap.util;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Pattern;
import org.jlato.rewrite.TypeSafeMatcher;
import org.jlato.tree.decl.*;

import java.util.Collections;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.Trees.emptyList;

/**
 * @author Didier Villevalois
 */
public abstract class TypePattern<A, T extends TypeDecl> extends Utils implements DeclPattern<A, TypeDecl> {

	@Override
	public final Pattern<? extends Decl> matcher(A arg) {
		return typeDecl(makeQuote(arg));
	}

	protected abstract String makeQuote(A arg);

	@Override
	@SuppressWarnings("unchecked")
	public TypeDecl rewrite(TypeDecl decl, ImportManager importManager, A arg) {

		if (GenSettings.generateDocs) {
			final String doc = makeDoc((T) decl, arg);
			if (doc != null) decl = insertJavadoc((T) decl, doc);
		}

		decl = contributeSignature((T) decl, importManager, arg);
		decl = ensureBody((T) decl);
		decl = contributeBody((T) decl, importManager, arg);
		for (DeclContribution<A, MemberDecl> contribution : contributions(arg)) {
			decl = applyContribution((T) decl, importManager, arg, contribution);
		}
		return decl;
	}

	protected T contributeBody(T decl, ImportManager importManager, A arg) {
		return decl;
	}

	protected Iterable<DeclContribution<A, MemberDecl>> contributions(A arg) {
		return Collections.emptyList();
	}

	protected abstract String makeDoc(T decl, A arg);

	protected T contributeSignature(T decl, ImportManager importManager, A arg) {
		return decl;
	}

	protected abstract T insertJavadoc(T decl, String doc);

	private T applyContribution(T decl, ImportManager importManager, A arg, DeclContribution<A, MemberDecl> contribution) {
		for (DeclPattern<A, ? extends MemberDecl> declaration : contribution.declarations(arg)) {
			decl = contributeMember(decl, importManager, declaration, arg);
		}
		return decl;
	}

	@SuppressWarnings("unchecked")
	private <M extends MemberDecl> T contributeMember(T type, ImportManager importManager, DeclPattern<A, M> pattern, A arg) {
		final Pattern<? extends M> matcher = (Pattern<? extends M>) pattern.matcher(arg);
		final MatchVisitor<M> visitor = (m, s) -> pattern.rewrite(m, importManager, arg);

		if (type.findAll(pattern.matcher(arg)).iterator().hasNext()) {
			return forAll(type, matcher, visitor);
		} else {
			final M member = matcher.build();
			return appendMember(type, pattern.rewrite(member, importManager, arg));
		}
	}


	protected abstract <M extends MemberDecl> T forAll(T type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor);

	protected abstract T ensureBody(T type);

	protected abstract <M extends MemberDecl> T appendMember(T type, M member);

	public static abstract class OfClass<A> extends TypePattern<A, ClassDecl> {

		@Override
		protected ClassDecl insertJavadoc(ClassDecl decl, String doc) {
			return decl.insertLeadingComment(doc);
		}

		@Override
		protected <M extends MemberDecl> ClassDecl forAll(ClassDecl type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor) {
			return type.forAll(matcher, visitor);
		}

		@Override
		protected ClassDecl ensureBody(ClassDecl type) {
			return type.withMembers(ms -> ms == null ? emptyList() : ms);
		}

		@Override
		protected <M extends MemberDecl> ClassDecl appendMember(ClassDecl type, M member) {
			return type.withMembers(ms -> ms.append(member));
		}
	}

	public static abstract class OfInterface<A> extends TypePattern<A, InterfaceDecl> {

		@Override
		protected InterfaceDecl insertJavadoc(InterfaceDecl decl, String doc) {
			return decl.insertLeadingComment(doc);
		}

		@Override
		protected <M extends MemberDecl> InterfaceDecl forAll(InterfaceDecl type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor) {
			return type.forAll(matcher, visitor);
		}

		@Override
		protected InterfaceDecl ensureBody(InterfaceDecl type) {
			return type.withMembers(ms -> ms == null ? emptyList() : ms);
		}

		@Override
		protected <M extends MemberDecl> InterfaceDecl appendMember(InterfaceDecl type, M member) {
			return type.withMembers(ms -> ms.append(member));
		}
	}
}
