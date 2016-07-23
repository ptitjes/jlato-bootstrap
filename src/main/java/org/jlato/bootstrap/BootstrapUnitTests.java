package org.jlato.bootstrap;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.tests.TreesAccessorsTest;
import org.jlato.bootstrap.tests.TreesEqualsHashCodeTest;
import org.jlato.bootstrap.tests.TreesKindTest;
import org.jlato.bootstrap.tests.TreesLambdaAccessorsTest;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.parser.ParseException;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class BootstrapUnitTests extends Utils {

	public static void main(String[] args) throws IOException, ParseException {
		new BootstrapUnitTests().generate();
	}

	public void generate() throws IOException, ParseException {
		String pathToJLaTo = System.getProperty("path.to.jlato");
		String rootDirectory = pathToJLaTo + "src/test/java";

		final TreeClassDescriptor[] classDescriptors = AllDescriptors.ALL_CLASSES;

		// Generate unit test classes

		CompilationUnitPattern.of(new TreesKindTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesKindTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesEqualsHashCodeTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesEqualsHashCodeTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesAccessorsTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesAccessorsTest.java", classDescriptors);

		CompilationUnitPattern.of(new TreesLambdaAccessorsTest())
				.apply(rootDirectory, "org/jlato/unit/tree/TreesLambdaAccessorsTest.java", classDescriptors);
	}
}
