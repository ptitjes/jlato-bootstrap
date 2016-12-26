package org.jlato.cc.grammar;

import org.jlato.cc.GrammarAnalysis;
import org.jlato.cc.JavaGrammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Didier Villevalois
 */
public class GrammarState {

	public final int id;
	public final String name;
	public final String endedNonTerminal;

	public final Map<Integer, GrammarState> choiceTransitions = new HashMap<>();

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

	public org.jlato.internal.parser.all.GrammarState build(GrammarAnalysis grammarAnalysis) {
		Optional<Integer> maxChoice = choiceTransitions.keySet().stream().max(Integer::compare);
		int[] theChoiceTransitions;
		if (maxChoice.isPresent()) {
			theChoiceTransitions = new int[maxChoice.get() + 1];
			for (int i = 0; i <= maxChoice.get(); i++) {
				GrammarState state = choiceTransitions.get(i);
				theChoiceTransitions[i] = state == null ? -1 : state.id;
			}
		} else theChoiceTransitions = new int[0];

		return new org.jlato.internal.parser.all.GrammarState(id,
				endedNonTerminal == null ? -1 : grammarAnalysis.nonTerminalIds.get(endedNonTerminal),
				theChoiceTransitions,
				nonTerminalTransition == null ? -1 : grammarAnalysis.nonTerminalIds.get(nonTerminalTransition),
				nonTerminalTransitionEnd == null ? -1 : nonTerminalTransitionEnd.id,
				terminalTransition == null ? -1 : JavaGrammar.terminals.get(terminalTransition),
				terminalTransitionEnd == null ? -1 : terminalTransitionEnd.id);
	}

}
