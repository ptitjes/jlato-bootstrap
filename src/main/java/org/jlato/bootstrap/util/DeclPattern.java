package org.jlato.bootstrap.util;

import org.jlato.pattern.Pattern;
import org.jlato.tree.decl.Decl;

/**
 * @author Didier Villevalois
 */
public interface DeclPattern<A, D extends Decl> {

	Pattern<? extends Decl> matcher(A arg);

	D rewrite(D decl, ImportManager importManager, A arg);
}
