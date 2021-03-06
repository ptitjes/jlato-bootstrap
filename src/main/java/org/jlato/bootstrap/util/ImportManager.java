package org.jlato.bootstrap.util;

import org.jlato.pattern.Matcher;
import org.jlato.pattern.Quotes;
import org.jlato.pattern.Substitution;
import org.jlato.tree.*;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.ImportDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.name.QualifiedName;
import org.jlato.util.Function1;

import java.util.*;

import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.importDecl;
import static org.jlato.tree.Trees.listOf;

/**
 * @author Didier Villevalois
 */
public class ImportManager {

	public final QualifiedName packageName;
	private Set<QualifiedName> onDemandImports = new HashSet<>();
	private Set<QualifiedName> singleImports = new HashSet<>();
	private Set<QualifiedName> onDemandStaticImports = new HashSet<>();
	private Set<QualifiedName> singleStaticImports = new HashSet<>();

	public ImportManager(QualifiedName packageName, NodeList<ImportDecl> imports) {
		this.packageName = packageName;
		for (ImportDecl importDecl : imports) {
			doAddImport(importDecl);
		}
	}

	public NodeList<ImportDecl> imports() {
		ArrayList<ImportDecl> sortedImports = filterImports(onDemandImports, singleImports, false, this::otherImport);

		ArrayList<ImportDecl> sortedJavaImports = filterImports(onDemandImports, singleImports, false, this::javaxImport);
		sortedJavaImports.addAll(filterImports(onDemandImports, singleImports, false, this::javaImport));

		ArrayList<ImportDecl> sortedStaticImports = filterImports(onDemandStaticImports, singleStaticImports, true, n -> true);

		if (!sortedJavaImports.isEmpty())
			sortedJavaImports.set(0, sortedJavaImports.get(0).prependLeadingNewLine());

		if (!sortedStaticImports.isEmpty())
			sortedStaticImports.set(0, sortedStaticImports.get(0).prependLeadingNewLine());

		return Trees.<ImportDecl>emptyList()
				.appendAll(listOf(sortedImports))
				.appendAll(listOf(sortedJavaImports))
				.appendAll(listOf(sortedStaticImports));
	}

	public CompilationUnit organiseAndSet(CompilationUnit unit) {
		HashSet<String> names = new HashSet<>();
		for (Name name : unit.withImports(emptyList()).findAll(Quotes.names())) {
			Tree parent = name.parent();
			if (parent instanceof Node && ((Node) parent).kind() == Kind.QualifiedName) continue;
			names.add(name.id());
		}

		for (QualifiedName name : unit.withImports(emptyList()).findAll((Matcher<QualifiedName>)
				(Object o, Substitution s) -> o instanceof Node && ((Node) o).kind() == Kind.QualifiedName ? s : null
		)) {
			if (name.qualifier().isSome()) continue;
			names.add(name.name().id());
		}

		// Remove unused imports

		for (QualifiedName name : new HashSet<>(singleImports)) {
			if (!names.contains(name.name().id())) {
				singleImports.remove(name);
			}
		}

		for (QualifiedName name : new HashSet<>(singleStaticImports)) {
			if (!names.contains(name.name().id())) {
				singleStaticImports.remove(name);
			}
		}

		// Make multiple imports

		Map<QualifiedName, Integer> counts = new HashMap<>();
		for (QualifiedName name : new HashSet<>(singleImports)) {
			if (name.qualifier().isSome()) {
				QualifiedName qualifier = name.qualifier().get();
				Integer count = counts.get(qualifier);
				count = count == null ? 1 : count + 1;
				counts.put(qualifier, count);
			}
		}
		Map<QualifiedName, Integer> staticCounts = new HashMap<>();
		for (QualifiedName name : new HashSet<>(singleStaticImports)) {
			if (name.qualifier().isSome()) {
				QualifiedName qualifier = name.qualifier().get();
				Integer count = staticCounts.get(qualifier);
				count = count == null ? 1 : count + 1;
				staticCounts.put(qualifier, count);
			}
		}

		for (Map.Entry<QualifiedName, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > 5) onDemandImports.add(entry.getKey());
		}
		for (Map.Entry<QualifiedName, Integer> entry : staticCounts.entrySet()) {
			if (entry.getValue() > 3) onDemandStaticImports.add(entry.getKey());
		}

		for (QualifiedName name : new HashSet<>(singleImports)) {
			if (name.qualifier().isSome()) {
				QualifiedName qualifier = name.qualifier().get();
				if (onDemandImports.contains(qualifier)) singleImports.remove(name);
			}
		}
		for (QualifiedName name : new HashSet<>(singleStaticImports)) {
			if (name.qualifier().isSome()) {
				QualifiedName qualifier = name.qualifier().get();
				if (onDemandStaticImports.contains(qualifier)) singleStaticImports.remove(name);
			}
		}

		return unit.withImports(imports());
	}

	private boolean otherImport(String name) {
		return !javaxImport(name) && !javaImport(name);
	}

	private boolean javaImport(String name) {
		return name.startsWith("java");
	}

	private boolean javaxImport(String name) {
		return name.startsWith("javax");
	}

	private ArrayList<ImportDecl> filterImports(Set<QualifiedName> onDemandImports, Set<QualifiedName> singleImports,
	                                            boolean isStatic, Function1<String, Boolean> filter) {
		final ArrayList<ImportDecl> sortedImports = new ArrayList<>();
		filterAddAll(sortedImports, filter,
				listOf(onDemandImports).<ImportDecl>map((QualifiedName n) -> importDecl(n).setOnDemand(true).setStatic(isStatic)));
		filterAddAll(sortedImports, filter,
				listOf(singleImports).<ImportDecl>map((QualifiedName n) -> importDecl(n).setOnDemand(false).setStatic(isStatic)));
		sortedImports.sort(NAME_COMPARATOR);
		return sortedImports;
	}

	private void filterAddAll(Collection<ImportDecl> collection, Function1<String, Boolean> filter, Iterable<ImportDecl> iterable) {
		for (ImportDecl e : iterable) {
			if (filter.apply(e.name().toString())) collection.add(e);
		}
	}

	public void addImportsByName(NodeList<QualifiedName> qualifiedNames) {
		addImports(qualifiedNames.map(n -> importDecl(n)));
	}

	public void addImportByName(QualifiedName qualifiedName) {
		addImport(importDecl(qualifiedName));
	}

	public void addImports(NodeList<ImportDecl> importDecls) {
		for (ImportDecl importDecl : importDecls) {
			addImport(importDecl);
		}
	}

	public void addImport(ImportDecl importDecl) {
		final QualifiedName name = importDecl.name();
		if (packageName.equals(name.qualifier().get())) return;

		if (importDecl.isStatic()) {
			if (importDecl.isOnDemand()) {
				if (onDemandStaticImports.contains(name)) return;
			} else {
				final NodeOption<QualifiedName> qualifier = name.qualifier();
				if (qualifier.isDefined() && onDemandStaticImports.contains(qualifier.get())) return;
				if (singleStaticImports.contains(name)) return;
			}
		} else {
			if (importDecl.isOnDemand()) {
				if (onDemandImports.contains(name)) return;
			} else {
				final NodeOption<QualifiedName> qualifier = name.qualifier();
				if (qualifier.isDefined() && onDemandImports.contains(qualifier.get())) return;
				if (singleImports.contains(name)) return;
			}
		}

		doAddImport(importDecl);
	}

	private void doAddImport(ImportDecl importDecl) {
		if (importDecl.isStatic()) {
			if (importDecl.isOnDemand()) {
				onDemandStaticImports.add(importDecl.name());
			} else {
				singleStaticImports.add(importDecl.name());
			}
		} else {
			if (importDecl.isOnDemand()) {
				onDemandImports.add(importDecl.name());
			} else {
				singleImports.add(importDecl.name());
			}
		}
	}

	public static final Comparator<ImportDecl> NAME_COMPARATOR = new Comparator<ImportDecl>() {
		@Override
		public int compare(ImportDecl o1, ImportDecl o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};
}
