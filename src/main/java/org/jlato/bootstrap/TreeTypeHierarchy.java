package org.jlato.bootstrap;

import org.jlato.tree.NodeList;
import org.jlato.tree.NodeOption;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.CompilationUnit;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.util.Function1;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Didier Villevalois
 */
public class TreeTypeHierarchy {

	private final HashSet<String> interfaceTypes = new HashSet<>();
	private final HashSet<String> classTypes = new HashSet<>();
	private final HashMap<String, NodeList<Name>> typeToParentTypes = new HashMap<>();

	public void initialize(TreeSet<CompilationUnit> treeSet) {
		for (String path : treeSet.paths()) {
			CompilationUnit cu = treeSet.get(path);

			final TypeDecl typeDecl = cu.types().get(0);
			if (typeDecl.typeKind() == TypeDecl.TypeKind.Class) {
				final ClassDecl classDecl = (ClassDecl) typeDecl;
				final String nameString = classDecl.name().id();

				final NodeOption<QualifiedType> superclass = classDecl.extendsClause();
				if (Refactoring.filterTreeClass(nameString, superclass)) {
					this.classTypes.add(nameString);

					final NodeList<QualifiedType> implementsClause = classDecl.implementsClause();
					if (implementsClause.size() != 0) {
						for (QualifiedType parentInterfaceType : implementsClause) {
							String parentTypeName = parentInterfaceType.name().id();
							this.interfaceTypes.add(parentTypeName);
						}

						Function1<QualifiedType, Name> f = t -> t.name();
						this.typeToParentTypes.put(nameString, implementsClause.map(f));
					}

					this.classTypes.add(nameString);
				}
			}
		}

		for (String path : treeSet.paths()) {
			CompilationUnit cu = treeSet.get(path);

			final TypeDecl typeDecl = cu.types().get(0);
			if (typeDecl.typeKind() == TypeDecl.TypeKind.Interface) {
				final InterfaceDecl interfaceDecl = (InterfaceDecl) typeDecl;
				final Name name = interfaceDecl.name();
				String nameString = name.id();

				if (isInterface(name) && !isTreeInterface(name)) {
					final NodeList<QualifiedType> extendsClause = interfaceDecl.extendsClause();
					if (extendsClause.size() != 0) {
						for (QualifiedType parentInterfaceType : extendsClause) {
							String parentTypeName = parentInterfaceType.name().id();
							this.interfaceTypes.add(parentTypeName);
						}

						Function1<QualifiedType,Name> f = t -> t.name();
						this.typeToParentTypes.put(nameString, extendsClause.map(f));
					}
				}
			}
		}
	}


	public boolean isTreeInterface(Name name) {
		return name.id().equals("Tree");
	}

	public boolean isInterface(Name name) {
		return interfaceTypes.contains(name.id());
	}

	public boolean isClass(Name name) {
		return classTypes.contains(name.id());
	}

	public NodeList<Name> getParentInterfaces(Name name) {
		return typeToParentTypes.get(name.id());
	}
}
