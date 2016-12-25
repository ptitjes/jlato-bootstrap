package org.jlato.cc.grammar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Didier Villevalois
 */
public class GContinuation {

	private Set<GLocation> locations = new HashSet<>();

	public GContinuation(GLocation location) {
		locations.add(location);
	}

	private boolean exists(Predicate<GLocation> p) {
		for (GLocation location : locations) {
			if (p.test(location)) return true;
		}
		return false;
	}

	public Set<String> asTerminals() {
		return locations.stream().map(l -> l.current.symbol).collect(Collectors.toSet());
	}

	public boolean intersects(GContinuation continuation) {
		return exists(l -> continuation.asTerminals().contains(l.current.symbol));
	}

	public GContinuation moveToNextTerminals2(GProductions productions) {
		Set<GLocation> visited = new HashSet<>();
		Set<GLocation> locations = new HashSet<>();
		for (GLocation location : this.locations) {
			moveToNextTerminals2(location, locations, visited, productions);
		}
		this.locations = locations;
		return this;
	}

	private void moveToNextTerminals2(GLocation location, Set<GLocation> locations, Set<GLocation> visited, GProductions productions) {
		if (visited.contains(location)) return;
		else visited.add(location);

		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Terminal:
				locations.add(location);
				break;
			case Choice:
				for (GLocation child : location.allChildren().asList()) {
					moveToNextTerminals2(child, locations, visited, productions);
				}
				break;
			case ZeroOrOne:
			case ZeroOrMore:
				nextSiblingOrParentsNextSiblings(location, locations, visited, productions);
				moveToNextTerminals2(location.firstChild(), locations, visited, productions);
				break;
			case OneOrMore:
			case Sequence:
				moveToNextTerminals2(location.firstChild(), locations, visited, productions);
				break;
			case NonTerminal:
				moveToNextTerminals2(location.traverseRef(productions), locations, visited, productions);
				break;
			case Action:
				nextSiblingOrParentsNextSiblings(location, locations, visited, productions);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private void nextSiblingOrParentsNextSiblings(GLocation location, Set<GLocation> locations, Set<GLocation> visited, GProductions productions) {
		GLocation nextSibling = location.nextSibling();
		if (nextSibling != null) {
			moveToNextTerminals2(nextSibling, locations, visited, productions);
		} else {
			Set<GLocation> nonChoiceParents = nonChoiceParents(location, productions);
			for (GLocation nonChoiceParent : nonChoiceParents) {
				nextSiblingOrParentsNextSiblings(nonChoiceParent, locations, visited, productions);
			}
		}
	}

	private static Set<GLocation> nonChoiceParents(GLocation location, GProductions productions) {
		GLocation parent = location.parent;
		if (parent != null) {
			return parent.current.kind != GExpansion.Kind.Choice ? Collections.singleton(parent) : nonChoiceParents(parent, productions);
		} else {
			GProduction production = location.production;
			if (production == null) throw new IllegalStateException();
			Set<GProductionRef> refs = new HashSet<>(productions.referencesOf(production.symbol));

			Set<GLocation> nonChoiceParents = new HashSet<>();
			for (GProductionRef ref : refs) {
				nonChoiceParents.add(productions.traverse(ref));
			}
			return nonChoiceParents;
		}
	}
}
