package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.pattern.Pattern;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.EnumConstantDecl;
import org.jlato.tree.decl.EnumDecl;
import org.jlato.tree.decl.Modifier;

import static org.jlato.pattern.Quotes.typeDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class KindEnum implements DeclPattern<TreeClassDescriptor[], EnumDecl> {

	@Override
	public Pattern<? extends Decl> matcher(TreeClassDescriptor[] arg) {
		return typeDecl("public enum Kind { ..$_ }");
	}

	@Override
	public EnumDecl rewrite(EnumDecl decl, ImportManager importManager, TreeClassDescriptor[] arg) {
		decl = enumDecl(name("Kind"))
				.withModifiers(listOf(Modifier.Public));

		for (TreeClassDescriptor descriptor : arg) {
			decl = decl.withEnumConstants(cs -> {
				EnumConstantDecl constantDecl = enumConstantDecl(descriptor.name);

				if (GenSettings.generateDocs)
					constantDecl = constantDecl
							.withDocComment("Kind for " + descriptor.prefixedDescription() + ".");

				return cs.append(constantDecl);
			});
		}

		return decl;
	}
}
