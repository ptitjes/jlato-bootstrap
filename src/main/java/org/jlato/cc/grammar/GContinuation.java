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

	public boolean intersects(GContinuation continuation) {
		return exists(continuation.locations::contains);
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
}
