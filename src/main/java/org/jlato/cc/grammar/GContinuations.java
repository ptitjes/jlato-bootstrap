package org.jlato.cc.grammar;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Didier Villevalois
 */
public class GContinuations {

	static boolean debug = false;

	private GProductions productions;
	private List<GLocation> locations;
	private boolean fromTerminals = false;

	public GContinuations(GLocation location, GProductions productions, boolean fromTerminals) {
		this(Collections.singletonList(location), productions, fromTerminals);
	}

	public GContinuations(List<GLocation> locations, GProductions productions, boolean fromTerminals) {
		this.productions = productions;
		this.locations = locations;
		this.fromTerminals = fromTerminals;
	}

	public List<GLocation> locations() {
		return locations;
	}

	public List<String> terminals() {
		return locations.stream().map(l -> l.current.symbol).distinct().collect(Collectors.toList());
	}

	public Map<String, List<GLocation>> perTerminalLocations() {
		return locations.stream().collect(Collectors.groupingBy(l -> l.current.symbol));
	}

	public void next() {
		if (fromTerminals)
			this.locations = locations.stream().map(GContinuations::nextOrParentsNext).distinct().collect(Collectors.toList());

		this.locations = locations.stream().flatMap(this::moveToNextTerminal).distinct().collect(Collectors.toList());
		fromTerminals = true;
	}

	private Stream<GLocation> moveToNextTerminal(GLocation location) {
		if (location == null) return Stream.empty();

		GExpansion expansion = location.current;

		if (debug) {
			System.out.println("moveToNextTerminal from " + location);
			System.out.println(expansion);
			System.out.println();
		}

		Stream<GLocation> toFollow;
		switch (expansion.kind) {
			case Terminal:
				if (debug) System.out.println("\tFound terminal: " + expansion.symbol);
				return Stream.of(location);
			case Choice:
				toFollow = location.allChildren().toSet().asSet().stream();
				break;
			case ZeroOrOne:
			case ZeroOrMore:
				toFollow = Stream.of(location.firstChild(), nextOrParentsNext(location));
				break;
			case OneOrMore:
				toFollow = Stream.of(location.firstChild());
				break;
			case Sequence:
				toFollow = Stream.of(location.firstChild());
				break;
			case NonTerminal:
				if (debug) System.out.println("\tTraversing non-terminal: " + expansion.symbol);
				if (debug) System.out.println(productions.get(expansion.symbol).expansion);
				toFollow = Stream.of(location.traverseRef(productions));
				break;
			case Action:
				toFollow = Stream.of(nextOrParentsNext(location));
				break;
			case LookAhead:
				toFollow = Stream.of(nextOrParentsNext(location));
				break;
			default:
				throw new IllegalArgumentException();
		}
		return toFollow.flatMap(this::moveToNextTerminal).distinct();
	}

	private static GLocation nextOrParentsNext(GLocation location) {
		GLocation next = location.nextSibling();
		while (next == null && location.parent != null) {
			do {
				location = location.parent;
			} while (location.parent != null && location.parent.current.kind == GExpansion.Kind.Choice);

			if (debug) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + location);
				System.out.println(location.current);
				System.out.println();
			}
			next = location.nextSibling();
		}
		return next;
	}

	private static <E> Set<E> intersection(Set<E> s1, Set<E> s2) {
		boolean set1IsLarger = s1.size() > s2.size();
		Set<E> cloneSet = new HashSet<E>(set1IsLarger ? s2 : s1);
		cloneSet.retainAll(set1IsLarger ? s1 : s2);
		return cloneSet;
	}
}
