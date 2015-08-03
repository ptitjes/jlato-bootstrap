package org.jlato.bootstrap.util;

import org.jlato.bootstrap.GenSettings;
import org.jlato.rewrite.MatchVisitor;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;

import static org.jlato.tree.TreeFactory.compilationUnit;
import static org.jlato.tree.TreeFactory.packageDecl;
import static org.jlato.tree.TreeFactory.qualifiedName;

/**
 * @author Didier Villevalois
 */
public abstract class CompilationUnitPattern<A> {

	public TreeSet<CompilationUnit> apply(TreeSet<CompilationUnit> treeSet, String path, A arg) {
		final CompilationUnit cu = treeSet.get(path);
		final QualifiedName packageName = makePackageName(path);

		CompilationUnit newCU = cu;
		if (cu == null || GenSettings.replace) {
			newCU = create(path, packageName);
		}

		ImportManager importManager = new ImportManager(packageName, newCU.imports());
		newCU = rewrite(newCU, importManager, arg);
		newCU.withImports(importManager.imports());

		return treeSet.put(path, newCU);
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
}
