package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.cc.grammar.*;
import org.jlato.cc.old.GrammarOld;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class TestContinuations {

	public static void main(String[] args) throws Exception {
		new TestContinuations().generate();
	}

	private void generate() throws IOException, ParseException, org.jlato.parser.ParseException {
		GProductions productions = GrammarOld.productions;

		GProduction production = productions.get("ConstructorDecl");
		GExpansion expansion = production.expansion;
		GExpansion stmtsExpansion = expansion.children.get(6);
		System.out.println(expansion);
		System.out.println(stmtsExpansion);
		System.out.println();
		System.out.println();

		GLocation location = stmtsExpansion.location();
		GContinuations c = run(location, false);
		for (Map.Entry<String, List<GLocation>> entry : c.perTerminalLocations().entrySet()) {
			if (entry.getKey().equals("THIS")) {
				debugEntry(entry, "");
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();

				GLocation thisLocation = entry.getValue().get(0);
				GContinuations thisContinuations = run(thisLocation, true);

				for (Map.Entry<String, List<GLocation>> thisEntry : thisContinuations.perTerminalLocations().entrySet()) {
					debugEntry(thisEntry, "\t\t\t");
					System.out.println();
					System.out.println();
				}

			}
		}
	}

	private GContinuations run(GLocation location, boolean fromTerminals) {
		GContinuations c = new GContinuations(location, GrammarOld.productions, fromTerminals);
		c.next();
		return c;
	}

	private void debugEntry(Map.Entry<String, List<GLocation>> entry, String indent) {
		System.out.println(indent + entry.getKey());
		for (GLocation location : entry.getValue()) {
			System.out.println(indent + "\t" + location);
		}

		System.out.println();
	}
}
