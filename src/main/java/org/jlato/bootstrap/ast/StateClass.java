package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.ast.classes.StateEqualsAndHashCode;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.typeDecl;

/**
 * @author Didier Villevalois
 */
class StateClass extends TypePattern.OfClass<TreeClassDescriptor> {

	public StateClass() {
		super(
				new StateEqualsAndHashCode()
		);
	}

	@Override
	public Pattern<TypeDecl> matcher(TreeClassDescriptor arg) {
		final QualifiedType superType = arg.stateSuperType();
		return typeDecl("public static class State extends SNodeState<State> implements " + superType + " { ..$_ }");
	}
}
