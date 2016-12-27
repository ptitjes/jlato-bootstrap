package org.jlato.cc.grammar;

import org.jlato.cc.GrammarAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public short[] entryPointNonTerminalUse;
	public short[] entryPointNonTerminalUseEndState;

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

		entryPointNonTerminalUse = new short[entryPointCount];
		entryPointNonTerminalUseEndState = new short[entryPointCount];
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
		if (nonTerminal.equals("Epilog")) return;
		int entryPointId = grammarAnalysis.entryPointIds.get(entryPoint);
		entryPointNonTerminalUse[entryPointId] = (short) (int) grammarAnalysis.nonTerminalIds.get(nonTerminal);
		entryPointNonTerminalUseEndState[entryPointId] = (short) state.id;
	}

	public org.jlato.internal.parser.all.Grammar build(GrammarAnalysis grammarAnalysis) {

		org.jlato.internal.parser.all.GrammarState[] theStates = new org.jlato.internal.parser.all.GrammarState[states.size()];
		for (int i = 0; i < states.size(); i++) {
			theStates[i] = states.get(i).build(grammarAnalysis);
		}

		short[] theNonTerminalStartStates = new short[nonTerminalCount];
		for (int i = 0; i < nonTerminalCount; i++) {
			theNonTerminalStartStates[i] = (short) nonTerminalStartStates[i].id;
		}

		short[] theChoicePointStates = new short[choicePointCount];
		for (int i = 0; i < choicePointCount; i++) {
			theChoicePointStates[i] = (short) choicePointStates[i].id;
		}

		short[][] theNonTerminalEndStates = new short[nonTerminalCount][];
		for (int i = 0; i < nonTerminalCount; i++) {
			theNonTerminalEndStates[i] = new short[nonTerminalEndStates[i].size()];
			int j = 0;
			for (GrammarState state : nonTerminalEndStates[i]) {
				theNonTerminalEndStates[i][j++] = (short) state.id;
			}
		}

		return new org.jlato.internal.parser.all.Grammar(theStates, theNonTerminalStartStates, theChoicePointStates,
				theNonTerminalEndStates, entryPointNonTerminalUse, entryPointNonTerminalUseEndState);
	}
}
