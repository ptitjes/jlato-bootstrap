package org.jlato.cc.grammar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Didier Villevalois
 */
public class GContinuation {

	private Set<GLocation> locations = new HashSet<>();

	public GContinuation(GLocation location) {
		locations.add(location);
	}

	public Set<String> asTerminals() {
		return locations.stream().map(l -> l.current.symbol).collect(Collectors.toSet());
	}

	public GContinuation moveToNextOrParentsNext(GProductions productions) {
		Set<GLocation> visited = new HashSet<>();
		Set<GLocation> locations = new HashSet<>();
		for (GLocation location : this.locations) {
			visited.add(location);
			nextOrParentsNext(location, locations, visited, productions);
		}
		this.locations = locations;
		return this;
	}

	public GContinuation moveToFirstChild(GProductions productions) {
		Set<GLocation> locations = new HashSet<>();
		for (GLocation location : this.locations) {
			locations.add(location.firstChild());
		}
		this.locations = locations;
		return this;
	}

	public GContinuation moveToNextTerminals(GProductions productions) {
		Set<GLocation> visited = new HashSet<>();
		Set<GLocation> locations = new HashSet<>();
		for (GLocation location : this.locations) {
			moveToNextTerminals(location, locations, visited, productions);
		}
		this.locations = locations;
		return this;
	}

	private void moveToNextTerminals(GLocation location, Set<GLocation> locations, Set<GLocation> visited, GProductions productions) {
		if (visited.contains(location)) return;
		else visited.add(location);

		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Terminal:
				locations.add(location);
				break;
			case Choice:
				for (GLocation child : location.allChildren().asList()) {
					moveToNextTerminals(child, locations, visited, productions);
				}
				break;
			case ZeroOrOne:
			case ZeroOrMore:
				nextOrParentsNext(location, locations, visited, productions);
				moveToNextTerminals(location.firstChild(), locations, visited, productions);
				break;
			case OneOrMore:
			case Sequence:
				moveToNextTerminals(location.firstChild(), locations, visited, productions);
				break;
			case NonTerminal:
				moveToNextTerminals(location.traverseRef(productions), locations, visited, productions);
				break;
			case Action:
				nextOrParentsNext(location, locations, visited, productions);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private void nextOrParentsNext(GLocation location, Set<GLocation> locations, Set<GLocation> visited, GProductions productions) {
		GLocation thisParent = location.parent;
		GLocation nextSibling = location.nextSibling();
		if (thisParent != null && thisParent.current.kind != GExpansion.Kind.Choice && nextSibling != null) {
			moveToNextTerminals(nextSibling, locations, visited, productions);
		} else {
			Set<GLocation> parents = parents(location, productions);
			for (GLocation parent : parents) {
				if (parent.current.kind == GExpansion.Kind.ZeroOrMore || parent.current.kind == GExpansion.Kind.OneOrMore)
					moveToNextTerminals(parent, locations, visited, productions);
				nextOrParentsNext(parent, locations, visited, productions);
			}
		}
	}

	private static Set<GLocation> parents(GLocation location, GProductions productions) {
		GLocation parent = location.parent;
		if (parent != null) {
			return Collections.singleton(parent);
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
