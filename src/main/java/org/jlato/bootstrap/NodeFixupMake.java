package org.jlato.bootstrap;

import org.jlato.parser.ParseException;
import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class NodeFixupMake extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {

		decl = decl.forAll(makeMethodMatcher, (m, s) -> {
					NodeList<FormalParameter> params = m.params();
					NodeList<FormalParameter> newParams = NodeList.empty();

					for (FormalParameter param : params) {
						Type type = param.type();
						Type newType = null;
						if (propertyFieldType(type)) {
							newType = type;
						} else {
							QualifiedType qualifiedType = (QualifiedType) type;

							QualifiedType typeArg = (QualifiedType) qualifiedType.typeArgs().get().get(0);
							if (typeArg.scope().isNone()) {
								// SNodeListState, ...
								newType = type;
							} else {
								Name scopeName = typeArg.scope().get().name();
								if (!hierarchy.isInterface(scopeName)) {
									newType = type;
								} else {
									newType = qualifiedType.withTypeArgs(some(NodeList.of(
											wildcardType().withExt(some(typeArg))
									)));
								}
							}
						}
						newParams = newParams.append(param.withType(newType));
					}
					return m.withParams(newParams);
				}
		);

		return decl;
	}
}
