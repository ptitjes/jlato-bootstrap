package org.jlato.cc.grammar;

import com.github.andrewoma.dexx.collection.Builder;
import com.github.andrewoma.dexx.collection.Set;
import com.github.andrewoma.dexx.collection.Sets;
import com.github.andrewoma.dexx.collection.Traversable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Didier Villevalois
 */
public class GContinuation {

	public final Set<GLocation> locations;

	public GContinuation(GLocation location) {
		this(Sets.of(location));
	}

	public GContinuation(Set<GLocation> locations) {
		this.locations = locations;
	}

	public GContinuation flatMap(Function<GLocation, Set<GLocation>> f) {
		Builder<GLocation, Set<GLocation>> builder = Sets.builder();
		for (GLocation location : locations) {
			builder.addAll((Traversable<GLocation>) f.apply(location));
		}
		return new GContinuation(builder.build());
	}

	public boolean exists(Predicate<GLocation> p) {
		for (GLocation location : locations) {
			if (p.test(location)) return true;
		}
		return false;
	}

	public boolean forAll(Predicate<GLocation> p) {
		return !exists(p.negate());
	}

	public boolean allTerminals() {
		return forAll(l -> l.current.kind == GExpansion.Kind.Terminal);
	}

	public Set<String> asTerminals() {
		Builder<String, Set<String>> builder = Sets.builder();
		for (GLocation location : locations) {
			builder.add(location.current.symbol);
		}
		return builder.build();
	}

	public boolean intersects(GContinuation continuation) {
		return exists(l -> continuation.asTerminals().contains(l.current.symbol));
	}

	public GContinuation moveToNextTerminals(GProductions productions) {
		GContinuation continuation = this;
		while (!continuation.allTerminals()) {
			continuation = continuation.flatMap(l -> moveToNextTerminals(l, productions));
		}
		return continuation;
	}

	private static Set<GLocation> moveToNextTerminals(GLocation location, GProductions productions) {
		if (location == null) return Sets.of();

		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Terminal:
				return Sets.of(location);
			case Choice:
				return location.allChildren().toSet();
			case ZeroOrOne:
			case ZeroOrMore:
				return Sets.of(location.firstChild(), nextOrParentsNext(location));
			case OneOrMore:
				return Sets.of(location.firstChild());
			case Sequence:
				return Sets.of(location.firstChild());
			case NonTerminal:
				return Sets.of(location.traverseRef(productions));
			case Action:
				return Sets.of(nextOrParentsNext(location));
			case LookAhead:
				return Sets.of(nextOrParentsNext(location));
			default:
				throw new IllegalArgumentException();
		}
	}

	private static GLocation nextOrParentsNext(GLocation location) {
		GLocation next = location.nextSibling();
		while (next == null && location.parent != null) {
			do {
				location = location.parent;
			} while (location.parent != null && location.parent.current.kind == GExpansion.Kind.Choice);
			next = location.nextSibling();
		}
		return next;
	}

	public GContinuation moveToNextTerminals2(GProductions productions) {
		GContinuation continuation = this;
		int depth = 0;
		while (!continuation.allTerminals()) {
			continuation = continuation.flatMap(l -> moveToNextTerminals2(l, productions));

			// TODO Fix this really dirty trick !!
			depth++;
			if (depth > 400) return null;
		}
		return continuation;
	}

	private static Set<GLocation> moveToNextTerminals2(GLocation location, GProductions productions) {
		if (location == null) return Sets.of();

		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Terminal:
				return Sets.of(location);
			case Choice:
				return location.allChildren().toSet();
			case ZeroOrOne:
			case ZeroOrMore:
				return nextSiblingOrParentsNextSiblings(location, productions).add(location.firstChild());
			case OneOrMore:
				return Sets.of(location.firstChild());
			case Sequence:
				return Sets.of(location.firstChild());
			case NonTerminal:
				return Sets.of(location.traverseRef(productions));
			case Action:
			case LookAhead:
				return nextSiblingOrParentsNextSiblings(location, productions);
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Set<GLocation> nonChoiceParents(GLocation location, GProductions productions) {
		GLocation parent = location.parent;
		if (parent != null) {
			return parent.current.kind != GExpansion.Kind.Choice ? Sets.of(parent) : nonChoiceParents(parent, productions);
		} else {
			GProduction production = location.production;
			if (production == null) throw new IllegalStateException();
			Set<GProductionRef> refs = Sets.copyOf(productions.referencesOf(production.symbol));

			Builder<GLocation, Set<GLocation>> builder = Sets.builder();
			for (GProductionRef ref : refs) {
				builder.add(productions.traverse(ref));
			}
			return builder.build();
		}
	}

	private static Set<GLocation> nextSiblingOrParentsNextSiblings(GLocation location, GProductions productions) {
		GLocation nextSibling = location.nextSibling();
		if (nextSibling != null) return Sets.of(nextSibling);

		Set<GLocation> nonChoiceParents = nonChoiceParents(location, productions);

		Builder<GLocation, Set<GLocation>> builder = Sets.builder();
		for (GLocation nonChoiceParent : nonChoiceParents) {
			builder.addAll((Traversable<GLocation>) nextSiblingOrParentsNextSiblings(nonChoiceParent, productions));
		}
		return builder.build();
	}
}
