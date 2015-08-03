package org.jlato.bootstrap.util;

import org.jlato.rewrite.Pattern;
import org.jlato.rewrite.TypeSafeMatcher;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;

/**
 * @author Didier Villevalois
 */
public interface DeclPattern<A, D extends Decl> {

	Pattern<? extends Decl> matcher(A arg);

	D rewrite(D decl, ImportManager importManager, A arg);
}
