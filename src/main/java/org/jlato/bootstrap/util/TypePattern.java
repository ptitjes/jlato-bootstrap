package org.jlato.bootstrap.util;

import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Pattern;
import org.jlato.rewrite.TypeSafeMatcher;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;

/**
 * @author Didier Villevalois
 */
public abstract class TypePattern<A, T extends TypeDecl> implements DeclPattern<A, TypeDecl> {

	public final DeclContribution<A, MemberDecl>[] contributions;

	@SafeVarargs
	public TypePattern(DeclContribution<A, MemberDecl>... contributions) {
		this.contributions = contributions;
	}

	@Override
	@SuppressWarnings("unchecked")
	public TypeDecl rewrite(TypeDecl decl, A arg) {
		for (DeclContribution<A, MemberDecl> contribution : contributions) {
			decl = applyContribution((T) decl, arg, contribution);
		}
		return decl;
	}

	private T applyContribution(T decl, A arg, DeclContribution<A, MemberDecl> contribution) {
		for (DeclPattern<A, ? extends MemberDecl> declaration : contribution.declarations(arg)) {
			decl = contributeMember(decl, declaration, arg);
		}
		return decl;
	}

	@SuppressWarnings("unchecked")
	private <M extends MemberDecl> T contributeMember(T type, DeclPattern<A, M> pattern, A arg) {
		final Pattern<? extends M> matcher = (Pattern<? extends M>) pattern.matcher(arg);
		final MatchVisitor<M> visitor = (m, s) -> pattern.rewrite(m, arg);

		if (type.findAll(pattern.matcher(arg)).iterator().hasNext()) {
			return forAll(type, matcher, visitor);
		} else {
			final M member = matcher.build();
			return appendMember(type, pattern.rewrite(member, arg));
		}
	}

	protected abstract <M extends MemberDecl> T forAll(T type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor);

	protected abstract <M extends MemberDecl> T appendMember(T type, M member);

	public static abstract class OfClass<A> extends TypePattern<A, ClassDecl> {

		public OfClass(DeclContribution<A, MemberDecl>... contributions) {
			super(contributions);
		}

		@Override
		protected <M extends MemberDecl> ClassDecl forAll(ClassDecl type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor) {
			return type.forAll(matcher, visitor);
		}

		@Override
		protected <M extends MemberDecl> ClassDecl appendMember(ClassDecl type, M member) {
			return type.withMembers(ms -> (ms == null ? NodeList.<MemberDecl>empty() : ms).append(member));
		}
	}

	public static abstract class OfInterface<A> extends TypePattern<A, InterfaceDecl> {

		public OfInterface(DeclContribution<A, MemberDecl>... contributions) {
			super(contributions);
		}

		@Override
		protected <M extends MemberDecl> InterfaceDecl forAll(InterfaceDecl type, TypeSafeMatcher<? extends M> matcher, MatchVisitor<M> visitor) {
			return type.forAll(matcher, visitor);
		}

		@Override
		protected <M extends MemberDecl> InterfaceDecl appendMember(InterfaceDecl type, M member) {
			return type.withMembers(ms -> (ms == null ? NodeList.<MemberDecl>empty() : ms).append(member));
		}
	}
}
