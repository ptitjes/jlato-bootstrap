package org.jlato.cc.grammar;

/**
 * @author Didier Villevalois
 */
public class GExpansionPath {
	public final int index;
	public final GExpansionPath inner;

	public GExpansionPath(int index, GExpansionPath inner) {
		this.index = index;
		this.inner = inner;
	}

	@Override
	public String toString() {
		return index + (inner != null ? "." + inner : "");
	}
}
