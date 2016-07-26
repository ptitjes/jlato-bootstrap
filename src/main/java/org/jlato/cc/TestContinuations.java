package org.jlato.cc;

import org.javacc.parser.ParseException;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.cc.grammar.GContinuations;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class TestContinuations {

	public static void main(String[] args) throws Exception {
		new TestContinuations().generate();
	}

	private void generate() throws IOException, ParseException, org.jlato.parser.ParseException {
		GProductions productions = Grammar.productions;

		GProduction production = productions.get("Annotations");
		System.out.println(production.expansion);

		GContinuations c = new GContinuations(production.expansion.location(), Grammar.productions);
		c.next();
		System.out.println(c.locations());
		System.out.println(c.terminals());
	}
}
