package org.jlato.bootstrap.util;

import org.jlato.bootstrap.GenSettings;
import org.jlato.parser.ParseException;
import org.jlato.parser.Parser;
import org.jlato.parser.ParserConfiguration;
import org.jlato.printer.FormattingSettings;
import org.jlato.printer.Printer;
import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeMap;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.QualifiedName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public abstract class CompilationUnitPattern<A> {

	public NodeMap<CompilationUnit> apply(NodeMap<CompilationUnit> nodeMap, String path, A arg) {
		final CompilationUnit cu = nodeMap.get(path);
		CompilationUnit newCU = applyPattern(cu, path, arg);
		return nodeMap.put(path, newCU);
	}

	public void apply(String basePath, String path, A arg) throws ParseException, IOException {
		String fullPath = basePath + (basePath.endsWith("/") ? "" : "/") + path;
		File file = new File(fullPath);

		CompilationUnit cu = null;
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} else {
			cu = new Parser(ParserConfiguration.Default.preserveWhitespaces(true)).parse(file, "UTF-8");
		}

		CompilationUnit newCU = applyPattern(cu, path, arg);

		final PrintWriter writer = new PrintWriter(new FileWriter(file));
		final Printer printer = new Printer(false, FormattingSettings.Default);
		printer.print(newCU, writer);
		writer.close();
	}

	private CompilationUnit applyPattern(CompilationUnit cu, String path, A arg) {
		final QualifiedName packageName = makePackageName(path);

		CompilationUnit newCU = cu;
		if (cu == null || GenSettings.replace) {
			newCU = create(path, packageName);
		}

		ImportManager importManager = new ImportManager(packageName, newCU.imports());
		newCU = rewrite(newCU, importManager, arg);
		newCU = newCU.withImports(importManager.imports());
		return newCU;
	}

	private CompilationUnit create(String path, QualifiedName packageName) {
		return compilationUnit(packageDecl(packageName));
	}

	private QualifiedName makePackageName(String path) {
		final String packageName = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
		return qualifiedName(packageName);
	}

	@SuppressWarnings("unchecked")
	private CompilationUnit rewrite(CompilationUnit cu, ImportManager importManager, A arg) {
		for (DeclContribution<A, TypeDecl> contribution : contributions(arg)) {
			cu = applyContribution(cu, importManager, arg, contribution);
		}
		return cu;
	}

	protected abstract Iterable<DeclContribution<A, TypeDecl>> contributions(A arg);

	private CompilationUnit applyContribution(CompilationUnit decl, ImportManager importManager, A arg, DeclContribution<A, TypeDecl> contribution) {
		for (DeclPattern<A, ? extends TypeDecl> declaration : contribution.declarations(arg)) {
			decl = contributeType(decl, importManager, declaration, arg);
		}
		return decl;
	}

	@SuppressWarnings("unchecked")
	private <T extends TypeDecl> CompilationUnit contributeType(CompilationUnit cu, ImportManager importManager, DeclPattern<A, T> pattern, A arg) {
		final Pattern<? extends T> matcher = (Pattern<? extends T>) pattern.matcher(arg);
		final MatchVisitor<T> visitor = (t, s) -> pattern.rewrite(t, importManager, arg);

		if (cu.findAll(pattern.matcher(arg)).iterator().hasNext()) {
			return cu.forAll(matcher, visitor);
		} else {
			final T type = matcher.build();
			return cu.withTypes(ts -> ts.append(pattern.rewrite(type, importManager, arg)));
		}
	}

	public static <A> CompilationUnitPattern<A> of(DeclPattern<A, TypeDecl> typePattern) {
		return new CompilationUnitPattern<A>() {
			@Override
			protected Iterable<DeclContribution<A, TypeDecl>> contributions(A arg) {
				return Arrays.asList(
						a -> Arrays.asList(typePattern)
				);
			}
		};
	}
}
