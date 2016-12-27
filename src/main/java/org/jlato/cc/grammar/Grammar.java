package org.jlato.cc.grammar;

import org.jlato.cc.GrammarAnalysis;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
	public Set<GrammarState>[] nonTerminalUseEndStates;
	public short[] entryPointNonTerminalUse;
	public short[] entryPointNonTerminalUseEndState;

	public Grammar(GrammarAnalysis grammarAnalysis) {
		this.grammarAnalysis = grammarAnalysis;
		this.entryPointCount = grammarAnalysis.entryPointCount;
		this.nonTerminalCount = grammarAnalysis.nonTerminalCount;
		this.choicePointCount = grammarAnalysis.choicePointCount;

		nonTerminalStartStates = new GrammarState[nonTerminalCount];
		choicePointStates = new GrammarState[choicePointCount];
		nonTerminalUseEndStates = (Set<GrammarState>[]) new Set[nonTerminalCount];
		for (int i = 0; i < nonTerminalCount; i++) {
			nonTerminalUseEndStates[i] = new HashSet<>();
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
		nonTerminalUseEndStates[grammarAnalysis.nonTerminalIds.get(nonTerminal)].add(state);
	}

	public void addNonTerminalEntryPointEndState(String entryPoint, String nonTerminal, GrammarState state) {
		if (nonTerminal.equals("Epilog")) return;
		int entryPointId = grammarAnalysis.entryPointIds.get(entryPoint);
		entryPointNonTerminalUse[entryPointId] = (short) (int) grammarAnalysis.nonTerminalIds.get(nonTerminal);
		entryPointNonTerminalUseEndState[entryPointId] = (short) state.id;
	}

	public void writeTo(ObjectOutputStream out) throws IOException {
		// Write the grammar

		int stateCount = states.size();
		out.writeShort(stateCount);
		for (int i = 0; i < stateCount; i++) {
			states.get(i).writeTo(out, grammarAnalysis);
		}

		out.writeShort(nonTerminalCount);
		for (int i = 0; i < nonTerminalCount; i++) {
			out.writeShort(nonTerminalStartStates[i].id);
		}

		out.writeShort(choicePointCount);
		for (int i = 0; i < choicePointCount; i++) {
			out.writeShort(choicePointStates[i].id);
		}

		for (int i = 0; i < nonTerminalCount; i++) {
			int useCount = nonTerminalUseEndStates[i].size();
			out.writeShort(useCount);
			for (GrammarState state : nonTerminalUseEndStates[i]) {
				out.writeShort(state.id);
			}
		}

		out.writeShort(entryPointCount);
		for (int i = 0; i < entryPointCount; i++) {
			out.writeShort(entryPointNonTerminalUse[i]);
			out.writeShort(entryPointNonTerminalUseEndState[i]);
		}
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
			theNonTerminalEndStates[i] = new short[nonTerminalUseEndStates[i].size()];
			int j = 0;
			for (GrammarState state : nonTerminalUseEndStates[i]) {
				theNonTerminalEndStates[i][j++] = (short) state.id;
			}
		}

		return new org.jlato.internal.parser.all.Grammar(theStates, theNonTerminalStartStates, theChoicePointStates,
				theNonTerminalEndStates, entryPointNonTerminalUse, entryPointNonTerminalUseEndState);
	}
}
