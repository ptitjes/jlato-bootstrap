package org.jlato.cc.grammar;

/**
 * @author Didier Villevalois
 */
public class GProductionRef {

	public final String referingProduction;
	public final GExpansionPath path;

	public GProductionRef(String referingProduction, GExpansionPath path) {
		this.referingProduction = referingProduction;
		this.path = path;
	}

	@Override
	public String toString() {
		return referingProduction + ':' + path;
	}
}
