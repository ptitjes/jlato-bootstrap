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

	public GContinuations(GProductions productions, GLocation location) {
		this(productions, Collections.singleton(location));
	}

	GContinuations(GProductions productions, Set<GLocation> locations) {
		this.productions = productions;
		this.locations = locations;
	}

	public Set<String> terminals() {
		return locations.stream().map(l -> l.current.symbol).collect(Collectors.toSet());
	}

	public void next() {
		this.locations = locations.stream().flatMap(this::moveToNextTerminal).collect(Collectors.toSet());
	}

	private Stream<GLocation> moveToNextTerminal(GLocation location) {
		if (location == null) return Stream.empty();

		GExpansion expansion = location.current;
		Stream<GLocation> toFollow;
		switch (expansion.kind) {
			case Terminal:
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
				toFollow = Stream.of(location.traverseRef(productions));
				break;
			case Action:
				toFollow = Stream.of(nextOrParentsNext(location));
				break;
			default:
				throw new IllegalArgumentException();
		}
		return toFollow.flatMap(this::moveToNextTerminal).distinct();
	}

	private static GLocation nextOrParentsNext(GLocation location) {
		GLocation next = location.nextSibling();
		while (next == null && location.parent != null)
			next = location.parent.nextSibling();
		return next;
	}

	private static <E> Set<E> intersection(Set<E> s1, Set<E> s2) {
		boolean set1IsLarger = s1.size() > s2.size();
		Set<E> cloneSet = new HashSet<E>(set1IsLarger ? s2 : s1);
		cloneSet.retainAll(set1IsLarger ? s1 : s2);
		return cloneSet;
	}
}
