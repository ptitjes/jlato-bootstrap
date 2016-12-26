package org.jlato.cc;

import org.jlato.cc.grammar.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public class GrammarAnalysis {

	private final GProductions productions;

	private int decisionCount = 0;
	private int ll1DecisionCount = 0;

	public GrammarAnalysis(GProductions productions) {
		this.productions = productions;
	}

	public void analysis() {
		assignConstantNames();
		assignLL1Decisions();

		System.out.println("Decision count: " + decisionCount + " (LL1: "+ ll1DecisionCount + "; ALL*: " + (decisionCount - ll1DecisionCount) + ")");
	}

	// Assignment of constant names

	private void assignConstantNames() {
		for (GProduction production : productions.getAll()) {
			assignConstantNames(production);
		}
	}

	private void assignConstantNames(GProduction production) {
		assignConstantNames(production.expansion, production.symbol);
	}

	private void assignConstantNames(GExpansion expansion, String namePrefix) {
		expansion.constantName = namePrefix;
		switch (expansion.kind) {
			case Choice:
			case Sequence:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				int index = 1;
				for (GExpansion child : expansion.children) {
					if (child.kind == GExpansion.Kind.Action) continue;
					assignConstantNames(child, namePrefix + "_" + index++);
				}
				break;
			default:
				break;
		}
	}

	// Computation of LL1 decisions

	private void assignLL1Decisions() {
		for (GProduction production : productions.getAll()) {
			assignLL1Decisions(production);
		}
	}

	private void assignLL1Decisions(GProduction production) {
		assignLL1Decisions(production.location());
	}

	private void assignLL1Decisions(GLocation location) {
		GExpansion expansion = location.current;
		switch (expansion.kind) {
			case Choice:
				expansion.ll1Decisions = computeChoiceLL1Decisions(location.allChildren().asList());
				expansion.canUseLL1 = canUseLL1(expansion.ll1Decisions);
				assignChildrenLL1Decisions(location);

				decisionCount++;
				if (expansion.canUseLL1) ll1DecisionCount++;
				break;
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				expansion.ll1Decisions = computeKleeneLL1Decisions(location);
				expansion.canUseLL1 = canUseLL1(expansion.ll1Decisions);
				assignChildrenLL1Decisions(location);

				decisionCount++;
				if (expansion.canUseLL1) ll1DecisionCount++;
				break;
			case Sequence:
				assignChildrenLL1Decisions(location);
				break;
			default:
				break;
		}
	}

	private void assignChildrenLL1Decisions(GLocation location) {
		for (GLocation child : location.allChildren()) {
			assignLL1Decisions(child);
		}
	}

	private List<Set<String>> computeChoiceLL1Decisions(List<GLocation> children) {
		List<GContinuation> continuations = new ArrayList<>(children.size());
		for (GLocation child : children) {
			continuations.add(new GContinuation(child));
		}
		return computeContinuations(continuations);
	}

	private List<Set<String>> computeKleeneLL1Decisions(GLocation location) {
		GContinuation after = new GContinuation(location).moveToNextSiblingOrParentSiblings(productions);
		GContinuation inside = new GContinuation(location).moveToFirstChild(productions);
		return computeContinuations(Arrays.asList(after, inside));
	}

	private List<Set<String>> computeContinuations(List<GContinuation> continuations) {
		List<Set<String>> terminalSets = new ArrayList<>(continuations.size());
		for (GContinuation continuation : continuations) {
			terminalSets.add(continuation.moveToNextTerminals(productions).asTerminals());
		}
		return terminalSets;
	}

	private boolean canUseLL1(List<Set<String>> terminalSets) {
		// Verify that the terminals don't intersect pairwise
		for (int i = 0; i < terminalSets.size(); i++) {
			Set<String> terminalSet1 = terminalSets.get(i);
			if (terminalSet1 == null) return false;
			for (int j = i + 1; j < terminalSets.size(); j++) {
				Set<String> terminalSet2 = terminalSets.get(j);
				if (terminalSet2 == null) return false;
				if (terminalSet1.stream().anyMatch(terminalSet2::contains)) return false;
			}
		}
		return true;
	}
}
