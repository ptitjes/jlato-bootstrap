package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.tree.NodeList;
import org.jlato.tree.Trees;
import org.jlato.tree.stmt.Stmt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class GrammarTransform {

	public GProductions transform(GProductions productions) {
		return productions
				.rewrite(this::mergeActions);
	}

	private GProduction mergeActions(GProduction production) {
		return production.rewrite(e ->
				e.kind == GExpansion.Kind.Sequence ? e.withChildren(mergeActions(e.children)) : e
		);
	}

	private List<GExpansion> mergeActions(List<GExpansion> children) {
		List<GExpansion> newChildren = new ArrayList<>();

		NodeList<Stmt> action = null;
		for (GExpansion child : children) {
			switch (child.kind) {
				case Action:
					if (action == null) action = child.action;
					else action = action.appendAll(child.action);
					break;
				default:
					if (action != null) {
						newChildren.add(GExpansion.action(action));
						action = null;
					}
					newChildren.add(child);
					break;
			}
		}
		if (action != null) {
			newChildren.add(GExpansion.action(action));
		}

		return newChildren;
	}
}
