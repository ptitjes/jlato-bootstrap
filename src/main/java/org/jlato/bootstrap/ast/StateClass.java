package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.decl.ClassDecl;
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
		return typeDecl("public static class State extends ..$_ implements ..$_ { ..$_ }");
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeClassDescriptor arg) {
		ClassDecl classDecl = (ClassDecl) super.rewrite(decl, arg);

		if (GenSettings.generateDocs)
			classDecl = classDecl.insertLeadingComment("/** A state object for " + arg.prefixedDescription() + ". */");

		return classDecl;
	}
}
