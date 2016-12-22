package org.jlato.cc.grammar;

import com.github.andrewoma.dexx.collection.*;

/**
 * @author Didier Villevalois
 */
public class GLocation {

	public final GLocation parent;
	public final int index;
	public final GExpansion current;

	public GLocation(GExpansion root) {
		this(null, -1, root);
	}

	public GLocation(GLocation parent, int index, GExpansion current) {
		this.parent = parent;
		this.index = index;
		this.current = current;
	}

	public GLocation traverseRef(GProductions productions) {
		if (current.kind != GExpansion.Kind.NonTerminal) throw new IllegalArgumentException();
		GProduction referedProduction = productions.get(current.symbol);
		return new GLocation(parent, index, referedProduction.expansion);
	}

	public GLocation traverse(GExpansionPath path) {
		return path == null ? this :
				new GLocation(this, path.index, current.children.get(path.index)).traverse(path.inner);
	}

	public GLocation nextSibling() {
		if (parent != null && index + 1 < parent.current.children.size())
			return new GLocation(parent, index + 1, parent.current.children.get(index + 1));
		else return null;
	}

	public GLocation firstChild() {
		if (current.children.size() > 0)
			return new GLocation(this, 0, current.children.get(0));
		else return null;
	}

	public List<GLocation> allChildren() {
		Builder<GLocation, LinkedList<GLocation>> builder = LinkedLists.builder();
		GLocation child = firstChild();
		while (child != null) {
			builder.add(child);
			child = child.nextSibling();
		}
		return builder.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GLocation other = (GLocation) o;

		return (index == other.index) &&
				(parent != null ? parent.equals(other.parent) : other.parent == null);
	}

	@Override
	public int hashCode() {
		int result = parent != null ? parent.hashCode() : 0;
		result = 31 * result + index;
		return result;
	}

	@Override
	public String toString() {
		return parent == null ? "<>" : parent + "." + index;
	}
}
