package org.jlato.cc;

import org.jlato.cc.grammar.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Didier Villevalois
 */
public class GrammarAnalysis {

	private final GProductions productions;
	private boolean someStateHasNoUniqueRole;

	public GrammarAnalysis(GProductions productions) {
		this.productions = productions;
	}

	public void analysis() {
		assignConstantNames();
		assignLL1Decisions();
		assignConstantIds();
		assignGrammarStates();

		for (String stat : statistics()) {
			System.out.println(stat);
		}
	}

	public List<String> statistics() {
		List<String> stats = new ArrayList<String>();
		stats.add("Decision count: " + decisionCount + " (LL1: " + ll1DecisionCount + "; ALL*: " + (decisionCount - ll1DecisionCount) + ")");
		stats.add("State count: " + grammar.states.size() + " (Non-terminal end: " + nonTerminalEnd + "; choices: " + choiceStates + "; non-terminal: " + nonTerminalStates + "; terminal: " + terminalStates + ")");
		if (someStateHasNoUniqueRole) stats.add("Warning: Some state has not a unique role !");
		return stats;
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
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
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

	// Computation of LL1 decisions

	public int decisionCount = 0;
	public int ll1DecisionCount = 0;

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
		GContinuation after = new GContinuation(location).moveToNextOrParentsNext(productions);
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

	// Assignment of constant ids

	// The constants are non-terminal ids and non-ll1 choice-point ids
	public int entryPointCount = 0;
	public Map<String, Integer> entryPointIds = new LinkedHashMap<>();
	public int nonTerminalCount = 0;
	public Map<String, Integer> nonTerminalIds = new LinkedHashMap<>();
	public int choicePointCount = 0;
	public Map<String, Integer> choicePointIds = new LinkedHashMap<>();

	private void assignConstantIds() {
		for (GProduction production : productions.getAll()) {
			assignConstantIds(production);
		}
	}

	private void assignConstantIds(GProduction production) {
		String symbol = production.symbol;

		if (symbol.endsWith("Entry")) entryPointIds.put(symbol, entryPointCount++);
		nonTerminalIds.put(symbol, nonTerminalCount++);

		assignConstantIds(production.expansion);
	}

	private void assignConstantIds(GExpansion expansion) {
		switch (expansion.kind) {
			case Choice:
			case ZeroOrOne:
			case ZeroOrMore:
			case OneOrMore:
				if (!expansion.canUseLL1) choicePointIds.put(expansion.constantName, choicePointCount++);
			case Sequence:
				for (GExpansion child : expansion.children) {
					if (child.kind == GExpansion.Kind.Action) continue;
					assignConstantIds(child);
				}
				break;
			default:
				break;
		}
	}

	// Assignment of grammar states

	public Grammar grammar;

	public Map<String, Integer> nonTerminalReturnIds = new LinkedHashMap<>();

	public int nonTerminalEnd = 0;
	public int choiceStates = 0;
	public int nonTerminalStates = 0;
	public int terminalStates = 0;

	private void assignGrammarStates() {
		grammar = new Grammar(this);

		for (GProduction production : productions.getAll()) {
			assignGrammarStates(production);
		}

		// Check that every state has a unique role
		// And count each type of states
		for (GrammarState state : grammar.states) {
			boolean nonTerminalEnd = state.endedNonTerminal != null;
			boolean choice = !state.choiceTransitions.isEmpty();
			boolean nonTerminal = state.nonTerminalTransition != null;
			boolean terminal = state.terminalTransition != null;

			int roleCount = nonTerminalEnd ? 1 : 0;
			roleCount += choice ? 1 : 0;
			roleCount += nonTerminal ? 1 : 0;
			roleCount += terminal ? 1 : 0;

			someStateHasNoUniqueRole = someStateHasNoUniqueRole || roleCount != 1;

			if (nonTerminalEnd) this.nonTerminalEnd++;
			if (choice) choiceStates++;
			if (nonTerminal) nonTerminalStates++;
			if (terminal) terminalStates++;
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
