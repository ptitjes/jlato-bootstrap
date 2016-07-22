package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;

import static org.jlato.rewrite.Quotes.typeDecl;
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
							.setDocComment("Kind for " + descriptor.prefixedDescription() + ".");

				return cs.append(constantDecl);
			});
		}

		return decl;
	}
}
