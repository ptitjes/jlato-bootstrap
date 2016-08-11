package org.jlato.bootstrap.util;

import org.jlato.pattern.Pattern;
import org.jlato.pattern.Substitution;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.FieldDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.util.Function1;

import java.util.ArrayList;
import java.util.List;

import static org.jlato.tree.Trees.importDecl;
import static org.jlato.tree.Trees.qualifiedName;

/**
 * @author Didier Villevalois
 */
public interface DeclContribution<A, D extends Decl> {

	Iterable<DeclPattern<A, ? extends D>> declarations(A arg);

	static <A> DeclContribution<A, MemberDecl> mergeFields(Function1<A, NodeList<FieldDecl>> declarations) {
		return new DeclContribution<A, MemberDecl>() {
			@Override
			public Iterable<DeclPattern<A, ? extends MemberDecl>> declarations(A arg) {
				List<DeclPattern<A, ? extends MemberDecl>> patterns = new ArrayList<>();
				for (FieldDecl declaration : declarations.apply(arg)) {
					patterns.add(new DeclPattern<A, FieldDecl>() {
						@Override
						public Pattern<? extends Decl> matcher(A arg) {
							return new Pattern<Decl>() {
								@Override
								public Substitution match(Object o, Substitution substitution) {
									if (!(o instanceof FieldDecl)) return null;
									if (!declaration.variables().get(0).id().name().equals(((FieldDecl) o).variables().get(0).id().name()))
										return null;
									return substitution;
								}

								@Override
								public Decl build(Substitution substitution) {
									return declaration;
								}
							};
						}

						@Override
						public FieldDecl rewrite(FieldDecl decl, ImportManager importManager, A arg) {
							return declaration;
						}
					});
				}
				return patterns;
			}
		};
	}
}
