package org.jlato.bootstrap.util;

import org.jlato.tree.decl.Decl;

/**
 * @author Didier Villevalois
 */
public interface DeclContribution<A, D extends Decl> {

	Iterable<DeclPattern<A, ? extends D>> declarations(A arg);
}
