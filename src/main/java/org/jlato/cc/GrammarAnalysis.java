package org.jlato.cc;

import org.jlato.cc.grammar.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Didier Villevalois
 */
public class GrammarAnalysis {

	private final GProductions productions;

	public int decisionCount = 0;
	public int ll1DecisionCount = 0;

	// The constants are non-terminal ids and non-ll1 choice-point ids
	public int entryPointCount = 0;
	public Map<String, Integer> entryPointIds = new LinkedHashMap<>();
	public int nonTerminalCount = 0;
	public Map<String, Integer> nonTerminalIds = new LinkedHashMap<>();
	public int choicePointCount = 0;
	public Map<String, Integer> choicePointIds = new LinkedHashMap<>();

	public Map<String, Integer> nonTerminalReturnIds = new LinkedHashMap<>();

	public Grammar grammar;

	public GrammarAnalysis(GProductions productions) {
		this.productions = productions;
	}

	public void analysis() {
		assignLL1Decisions();
		System.out.println("Decision count: " + decisionCount + " (LL1: " + ll1DecisionCount + "; ALL*: " + (decisionCount - ll1DecisionCount) + ")");

		assignConstantNames();

		assignGrammarStates();
		System.out.println("State count: " + grammar.states.size());

		int nonTerminalEnd = 0;
		int choiceStates = 0;
		int nonTerminalStates = 0;
		int terminalStates = 0;
		for (GrammarState state : grammar.states) {
			if (state.endedNonTerminal != null) nonTerminalEnd++;
			if (!state.choiceTransitions.isEmpty()) choiceStates++;
			if (state.nonTerminalTransition != null) nonTerminalStates++;
			if (state.terminalTransition != null) terminalStates++;
		}
		System.out.println("(Non-terminal end: " + nonTerminalEnd + "; choices: " + choiceStates + "; non-terminal: " + nonTerminalStates + "; terminal: " + terminalStates + ")");
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

	// Assignment of constant names

	private void assignConstantNames() {
		for (GProduction production : productions.getAll()) {
			assignConstantNames(production);
		}
	}

	private void assignConstantNames(GProduction production) {
		String symbol = production.symbol;

		if (symbol.endsWith("Entry")) entryPointIds.put(symbol, entryPointCount++);
		nonTerminalIds.put(symbol, nonTerminalCount++);

		assignConstantNames(production.expansion, symbol);
	}

	private void assignConstantNames(GExpansion expansion, String namePrefix) {
		expansion.constantName = namePrefix;
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				if (!expansion.canUseLL1) choicePointIds.put(expansion.constantName, choicePointCount++);
			case Sequence:
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

	// Assignment of grammar states

	private void assignGrammarStates() {
		grammar = new Grammar(this);

		for (GProduction production : productions.getAll()) {
			assignGrammarStates(production);
		}
	}

	private void assignGrammarStates(GProduction production) {
		String nonTerminal = production.symbol;
		boolean isEntryPoint = nonTerminal.endsWith("Entry");

		GrammarState start = grammar.newGrammarState(nonTerminal);
		GrammarState end = grammar.newGrammarState(nonTerminal, nonTerminal);
		assignGrammarStates(production.expansion, start, end, isEntryPoint ? nonTerminal : null);

		grammar.addNonTerminalStartState(nonTerminal, start);
	}

	private void assignGrammarStates(GExpansion expansion, GrammarState start, GrammarState end, String entryPoint) {
		expansion.startState = start;
		expansion.endState = end;

		switch (expansion.kind) {
			case Choice: {
				List<GExpansion> children = expansion.children;
				for (int i = 0; i < children.size(); i++) {
					GrammarState choiceStart = grammar.newGrammarState(expansion.constantName);
					start.addChoice(i + 1, choiceStart);
					assignGrammarStates(children.get(i), choiceStart, end, entryPoint);
				}
				if (!expansion.canUseLL1) grammar.addChoicePointState(expansion.constantName, start);
				break;
			}
			case Sequence: {
				List<GExpansion> children = filterActions(expansion.children);
				GrammarState state;
				int lastIndex = children.size() - 1;
				for (int i = 0; i <= lastIndex; i++) {
					if (i == lastIndex) state = end;
					else state = grammar.newGrammarState(expansion.name);

					assignGrammarStates(children.get(i), start, state, entryPoint);
					start = state;
				}
				break;
			}
			case ZeroOrOne: {
				GrammarState childStart = grammar.newGrammarState(expansion.constantName);
				assignGrammarStates(uniqueChild(expansion), childStart, end, entryPoint);

				start.addChoice(0, end);
				start.addChoice(1, childStart);
				if (!expansion.canUseLL1) grammar.addChoicePointState(expansion.constantName, start);
				break;
			}
			case ZeroOrMore: {
				GrammarState childStart = grammar.newGrammarState(expansion.constantName);
				assignGrammarStates(uniqueChild(expansion), childStart, start, entryPoint);

				start.addChoice(0, end);
				start.addChoice(1, childStart);
				if (!expansion.canUseLL1) grammar.addChoicePointState(expansion.constantName, start);
				break;
			}
			case OneOrMore: {
				GrammarState childEnd = grammar.newGrammarState(expansion.constantName);
				assignGrammarStates(uniqueChild(expansion), start, childEnd, entryPoint);

				childEnd.addChoice(0, end);
				childEnd.addChoice(1, start);
				if (!expansion.canUseLL1) grammar.addChoicePointState(expansion.constantName, childEnd);
				break;
			}
			case NonTerminal: {
				nonTerminalReturnIds.put(expansion.constantName, end.id);
				start.setNonTerminal(expansion.symbol, end);

				if (entryPoint != null) grammar.addNonTerminalEntryPointEndState(entryPoint, expansion.symbol, end);
				else grammar.addNonTerminalEndState(expansion.symbol, end);
				break;
			}
			case Terminal: {
				start.setTerminal(expansion.symbol, end);
				break;
			}
			case Action:
				break;
		}
	}

	private List<GExpansion> filterActions(List<GExpansion> children) {
		return children.stream().filter(e -> e.kind != GExpansion.Kind.Action).collect(Collectors.toList());
	}

	private GExpansion uniqueChild(GExpansion expansion) {
		for (GExpansion child : expansion.children) {
			if (child.kind != GExpansion.Kind.Action) return child;
		}
		throw new IllegalArgumentException();
	}
}
