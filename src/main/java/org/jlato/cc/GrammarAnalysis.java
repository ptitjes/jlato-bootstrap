package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;

/**
 * @author Didier Villevalois
 */
public class GrammarAnalysis {

	public GProductions analysis(GProductions productions) {
		assignConstantNames(productions);
		return productions;
	}

	private void assignConstantNames(GProductions productions) {
		for (GProduction production : productions.getAll()) {
			assignConstantNames(production);
		}
	}

	private void assignConstantNames(GProduction production) {
		assignConstantNames(production.expansion, production.symbol);
	}

	private void assignConstantNames(GExpansion expansion, String namePrefix) {
		expansion.constantName = namePrefix;
		switch (expansion.kind) {
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				int index = 1;
				for (GExpansion child : expansion.children) {
					if (child.kind == GExpansion.Kind.Action) continue;
					assignConstantNames(child, namePrefix + "_" + index++);
				}
				break;
			default:
				break;
		}
	}

}
