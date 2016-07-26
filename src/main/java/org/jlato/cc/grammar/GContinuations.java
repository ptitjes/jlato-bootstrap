package org.jlato.cc.grammar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Didier Villevalois
 */
public class GContinuations {

//  // Example use
//	{
//		GProduction production = productions.get("ClassOrInterfaceBodyDecl");
//		List<GExpansion> choices = production.expansion.children.get(1).children.get(1).children.get(2).children;
//		System.out.println(choices);
//
//		Stream<GContinuations> continuations =
//				choices.stream().map(c -> new GContinuations(productions, c.location()));
//
//		continuations.forEach(c -> {
//			c.next();
//			System.out.println(c.terminals());
//		});
//	}

	private GProductions productions;
	private Set<GLocation> locations;

	public GContinuations(GLocation location, GProductions productions) {
		this(Collections.singleton(location), productions);
	}

	GContinuations(Set<GLocation> locations, GProductions productions) {
		this.productions = productions;
		this.locations = locations;
	}

	public Set<GLocation> locations() {
		return locations;
	}

	public Set<String> terminals() {
		return locations.stream().map(l -> l.current.symbol).collect(Collectors.toSet());
	}

	public void next() {
		this.locations = locations.stream().flatMap(this::moveToNextTerminal).collect(Collectors.toSet());
	}

	private Stream<GLocation> moveToNextTerminal(GLocation location) {
		if (location == null) return Stream.empty();

//		System.out.println("moveToNextTerminal from " + location);

		GExpansion expansion = location.current;
		Stream<GLocation> toFollow;
		switch (expansion.kind) {
			case Terminal:
//				System.out.println("\tFound terminal: " + expansion.symbol);
				return Stream.of(location);
			case Choice:
				toFollow = location.allChildren().stream();
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
//				System.out.println("\tTraversing non-terminal: " + expansion.symbol);
//				System.out.println(productions.get(expansion.symbol).expansion);
				toFollow = Stream.of(location.traverseRef(productions));
				break;
			case Action:
//				System.out.println("\tAction: " + expansion);
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
//			System.out.println("\t\t>>" + location.parent);
			location = location.parent;
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
