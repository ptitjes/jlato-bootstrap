package org.jlato.cc.grammar;

import org.jlato.cc.GrammarAnalysis;
import org.jlato.cc.JavaGrammar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Didier Villevalois
 */
public class GrammarState {

	public final int id;
	public final String name;
	public final String endedNonTerminal;

	public final Map<Integer, GrammarState> choiceTransitions = new LinkedHashMap<>();

	public String nonTerminalTransition = null;
	public GrammarState nonTerminalTransitionEnd;

	public String terminalTransition = null;
	public GrammarState terminalTransitionEnd;

	public GrammarState(int id, String name, String endedNonTerminal) {
		this.id = id;
		this.name = name;
		this.endedNonTerminal = endedNonTerminal;
	}

	public void addChoice(int index, GrammarState target) {
		choiceTransitions.put(index, target);
	}

	public void setNonTerminal(String symbol, GrammarState target) {
		nonTerminalTransition = symbol;
		nonTerminalTransitionEnd = target;
	}

	public void setTerminal(String symbol, GrammarState target) {
		terminalTransition = symbol;
		terminalTransitionEnd = target;
	}

	public void writeTo(DataOutputStream out, GrammarAnalysis grammarAnalysis) throws IOException {

		// Prepare the data

		short theId = (short) this.id;
		short theEndedNonTerminal = endedNonTerminal == null ? -1 : (short) (int) grammarAnalysis.nonTerminalIds.get(endedNonTerminal);

		Optional<Integer> maxChoice = choiceTransitions.keySet().stream().max(Integer::compare);
		short[] theChoiceTransitions;
		if (maxChoice.isPresent()) {
			theChoiceTransitions = new short[maxChoice.get() + 1];
			for (int i = 0; i <= maxChoice.get(); i++) {
				GrammarState state = choiceTransitions.get(i);
				theChoiceTransitions[i] = (short) (state == null ? -1 : state.id);
			}
		} else theChoiceTransitions = new short[0];

		short theNonTerminalTransition = nonTerminalTransition == null ? -1 : (short) (int) grammarAnalysis.nonTerminalIds.get(nonTerminalTransition);
		short theNonTerminalTransitionEnd = nonTerminalTransitionEnd == null ? -1 : (short) nonTerminalTransitionEnd.id;
		short theTerminalTransition = terminalTransition == null ? -1 : (short) (int) JavaGrammar.terminals.get(terminalTransition);
		short theTerminalTransitionEnd = terminalTransitionEnd == null ? -1 : (short) terminalTransitionEnd.id;

		// Write the state

		out.writeShort(theId);
		out.writeShort(theEndedNonTerminal);

		out.writeShort(theChoiceTransitions.length);
		for (int i = 0; i < theChoiceTransitions.length; i++) {
			out.writeShort(theChoiceTransitions[i]);
		}

		out.writeShort(theNonTerminalTransition);
		out.writeShort(theNonTerminalTransitionEnd);
		out.writeShort(theTerminalTransition);
		out.writeShort(theTerminalTransitionEnd);
	}

	public org.jlato.internal.parser.all.GrammarState build(GrammarAnalysis grammarAnalysis) {
		Optional<Integer> maxChoice = choiceTransitions.keySet().stream().max(Integer::compare);
		short[] theChoiceTransitions;
		if (maxChoice.isPresent()) {
			theChoiceTransitions = new short[maxChoice.get() + 1];
			for (int i = 0; i <= maxChoice.get(); i++) {
				GrammarState state = choiceTransitions.get(i);
				theChoiceTransitions[i] = (short) (state == null ? -1 : state.id);
			}
		} else theChoiceTransitions = new short[0];

		return new org.jlato.internal.parser.all.GrammarState((short) id,
				endedNonTerminal == null ? -1 : (short) (int) grammarAnalysis.nonTerminalIds.get(endedNonTerminal),
				theChoiceTransitions,
				nonTerminalTransition == null ? -1 : (short) (int) grammarAnalysis.nonTerminalIds.get(nonTerminalTransition),
				nonTerminalTransitionEnd == null ? -1 : (short) nonTerminalTransitionEnd.id,
				terminalTransition == null ? -1 : (short) (int) JavaGrammar.terminals.get(terminalTransition),
				terminalTransitionEnd == null ? -1 : (short) terminalTransitionEnd.id);
	}

}
