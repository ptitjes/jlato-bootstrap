package org.jlato.cc.grammar;

import com.github.andrewoma.dexx.collection.*;
import com.github.andrewoma.dexx.collection.LinkedList;
import com.github.andrewoma.dexx.collection.List;
import com.github.andrewoma.dexx.collection.Set;

/**
 * @author Didier Villevalois
 */
public class GLocation {

	public final GProduction production;
	public final GLocation parent;
	public final int index;
	public final GExpansion current;

	public GLocation(GProduction production, GExpansion root) {
		this(production, null, -1, root);
	}

	public GLocation(GProduction production, GLocation parent, int index, GExpansion current) {
		this.production = production;
		this.parent = parent;
		this.index = index;
		this.current = current;
	}

	public GLocation traverseRef(GProductions productions) {
		if (current.kind != GExpansion.Kind.NonTerminal) throw new IllegalStateException();
		GProduction referedProduction = productions.get(current.symbol);
		return new GLocation(referedProduction, parent, index, referedProduction.expansion);
	}

	public GLocation traverse(GExpansionPath path) {
		return path == null ? this :
				new GLocation(production, this, path.index, current.children.get(path.index)).traverse(path.inner);
	}

	public GLocation nextSibling() {
		if (parent != null && index + 1 < parent.current.children.size())
			return new GLocation(production, parent, index + 1, parent.current.children.get(index + 1));
		else return null;
	}

	public GLocation firstChild() {
		if (current.children.size() > 0)
			return new GLocation(production, this, 0, current.children.get(0));
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
				(parent != null ? parent.equals(other.parent) : other.parent == null) &&
				(production != null ? production.symbol.equals(other.production.symbol) : other.production == null);
	}

	@Override
	public int hashCode() {
		int result = parent != null ? parent.hashCode() : 0;
		result = 31 * result + (production != null ? production.symbol.hashCode() : 0);
		result = 31 * result + index;
		return result;
	}

	@Override
	public String toString() {
		return parent == null ? "<" + production.symbol + ">" : parent + "." + index;
	}
}
