package org.jlato.cc.grammar;

import java.util.*;
import java.util.function.Function;

/**
 * @author Didier Villevalois
 */
public class GProductions {

	private List<GProduction> productions = new ArrayList<>();
	private Map<String, GProduction> productionsByName = new HashMap<>();
	private Map<String, List<GProductionRef>> references = new HashMap<>();

	public GProductions(List<GProduction> productions) {
		this.productions = productions;
		for (GProduction production : productions) {
			this.productionsByName.put(production.symbol, production);
		}
	}

	public List<GProduction> getAll() {
		return productions;
	}

	public GProduction get(String symbol) {
		return productionsByName.get(symbol);
	}

	public void rewrite(Function<GProduction, GProduction> f) {
		List<GProduction> rewrote = new ArrayList<>();
		Map<String, GProduction> rewroteByName = new HashMap<>();

		for (GProduction production : productions) {
			GProduction r = f.apply(production);
			rewrote.add(r);
			rewroteByName.put(r.symbol, r);
		}
		productions = rewrote;
		productionsByName = rewroteByName;
	}

	public void recomputeReferences() {
		references = new HashMap<>();
		for (GProduction production : productions) {
			recomputeUsesIn(production.expansion, production, new Stack<>());
		}
	}

	private void recomputeUsesIn(GExpansion expansion, GProduction production, Stack<Integer> path) {
		switch (expansion.kind) {
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				for (int i = 0; i < expansion.children.size(); i++) {
					path.push(i);
					recomputeUsesIn(expansion.children.get(i), production, path);
					path.pop();
				}
				break;
			case NonTerminal:
				addUsesOf(expansion.symbol, production, path);
				break;
			case Terminal:
			case Action:
			default:
				break;
		}
	}

	private void addUsesOf(String symbol, GProduction production, Stack<Integer> path) {
		List<GProductionRef> refs = references.get(symbol);
		if (refs == null) {
			refs = new ArrayList<>();
			references.put(symbol, refs);
		}

		GExpansionPath expansionPath = null;
		for (int i = path.size() - 1; i >= 0; i--) {
			expansionPath = new GExpansionPath(path.get(i), expansionPath);
		}
		refs.add(new GProductionRef(production.symbol, expansionPath));
	}

	public List<GProductionRef> referencesOf(GProduction production) {
		return referencesOf(production.symbol);
	}

	public List<GProductionRef> referencesOf(String symbol) {
		List<GProductionRef> refs = references.get(symbol);
		return refs == null ? Collections.emptyList() : refs;
	}

	public GLocation traverse(GProductionRef ref) {
		GProduction production = productionsByName.get(ref.referingProduction);
		return production.location().traverse(ref.path);
	}
}
