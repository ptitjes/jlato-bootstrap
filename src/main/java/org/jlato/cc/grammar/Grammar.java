package org.jlato.cc.grammar;

import org.jlato.cc.GrammarAnalysis;
import org.jlato.internal.parser.util.IntSet;

import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Grammar {

	public final GrammarAnalysis grammarAnalysis;
	public final int entryPointCount, nonTerminalCount, choicePointCount;
	public List<GrammarState> states = new ArrayList<>();
	public GrammarState[] nonTerminalStartStates;
	public GrammarState[] choicePointStates;
	public Set<GrammarState>[] nonTerminalEndStates;
	public Set<GrammarState>[][] perEntryPointNonTerminalEndStates;

	public Grammar(GrammarAnalysis grammarAnalysis) {
		this.grammarAnalysis = grammarAnalysis;
		this.entryPointCount = grammarAnalysis.entryPointCount;
		this.nonTerminalCount = grammarAnalysis.nonTerminalCount;
		this.choicePointCount = grammarAnalysis.choicePointCount;

		nonTerminalStartStates = new GrammarState[nonTerminalCount];
		choicePointStates = new GrammarState[choicePointCount];
		nonTerminalEndStates = (Set<GrammarState>[]) new Set[nonTerminalCount];
		for (int i = 0; i < nonTerminalCount; i++) {
			nonTerminalEndStates[i] = new HashSet<>();
		}

		perEntryPointNonTerminalEndStates = (Set<GrammarState>[][]) new Set[entryPointCount][nonTerminalCount];
		for (int i = 0; i < entryPointCount; i++) {
			for (int j = 0; j < nonTerminalCount; j++) {
				perEntryPointNonTerminalEndStates[i][j] = new HashSet<>();
			}
		}
	}

	public GrammarState newGrammarState(String name) {
		return newGrammarState(name, null);
	}

	public GrammarState newGrammarState(String name, String endedNonTerminal) {
		GrammarState state = new GrammarState(states.size(), name, endedNonTerminal);
		states.add(state);
		return state;
	}

	public void addNonTerminalStartState(String nonTerminal, GrammarState start) {
		nonTerminalStartStates[grammarAnalysis.nonTerminalIds.get(nonTerminal)] = start;
	}

	public void addChoicePointState(String choicePoint, GrammarState start) {
		choicePointStates[grammarAnalysis.choicePointIds.get(choicePoint)] = start;
	}

	public void addNonTerminalEndState(String nonTerminal, GrammarState state) {
		nonTerminalEndStates[grammarAnalysis.nonTerminalIds.get(nonTerminal)].add(state);
	}

	public void addNonTerminalEntryPointEndState(String entryPoint, String nonTerminal, GrammarState state) {
		perEntryPointNonTerminalEndStates[grammarAnalysis.entryPointIds.get(entryPoint)][grammarAnalysis.nonTerminalIds.get(nonTerminal)].add(state);
	}

	public org.jlato.internal.parser.all.Grammar build(GrammarAnalysis grammarAnalysis) {

		org.jlato.internal.parser.all.GrammarState[] theStates = new org.jlato.internal.parser.all.GrammarState[states.size()];
		for (int i = 0; i < states.size(); i++) {
			theStates[i] = states.get(i).build(grammarAnalysis);
		}

		int[] theNonTerminalStartStates = new int[nonTerminalCount];
		for (int i = 0; i < nonTerminalCount; i++) {
			theNonTerminalStartStates[i] = nonTerminalStartStates[i].id;
		}

		int[] theChoicePointStates = new int[choicePointCount];
		for (int i = 0; i < choicePointCount; i++) {
			theChoicePointStates[i] = choicePointStates[i].id;
		}

		IntSet[] theNonTerminalEndStates = new IntSet[nonTerminalCount];
		for (int i = 0; i < nonTerminalCount; i++) {
			IntSet set = new IntSet();
			for (GrammarState state : nonTerminalEndStates[i]) {
				set.add(state.id);
			}
			theNonTerminalEndStates[i] = set;
		}

		IntSet[][] thePerEntryPointNonTerminalEndStates = new IntSet[entryPointCount][nonTerminalCount];
		for (int i = 0; i < entryPointCount; i++) {
			for (int j = 0; j < nonTerminalCount; j++) {
				IntSet set = new IntSet();
				for (GrammarState state : perEntryPointNonTerminalEndStates[i][j]) {
					set.add(state.id);
				}
				thePerEntryPointNonTerminalEndStates[i][j] = set;
			}
		}

		return new org.jlato.internal.parser.all.Grammar(theStates, theNonTerminalStartStates, theChoicePointStates,
				theNonTerminalEndStates, thePerEntryPointNonTerminalEndStates);
	}
}
