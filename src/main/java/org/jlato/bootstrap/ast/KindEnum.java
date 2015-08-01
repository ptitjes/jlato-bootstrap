package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.EnumConstantDecl;
import org.jlato.tree.decl.EnumDecl;
import org.jlato.tree.decl.Modifier;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.Name;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.TreeFactory.enumConstantDecl;
import static org.jlato.tree.TreeFactory.enumDecl;

/**
 * @author Didier Villevalois
 */
public class KindEnum implements DeclPattern<TreeClassDescriptor[], EnumDecl> {

	@Override
	public Pattern<TypeDecl> matcher(TreeClassDescriptor[] arg) {
		return typeDecl("public enum Kind { ..$_ }");
	}

	@Override
	public EnumDecl rewrite(EnumDecl decl, TreeClassDescriptor[] arg) {
		decl = enumDecl()
				.withModifiers(NodeList.of(Modifier.Public))
				.withName(new Name("Kind"));

		for (TreeClassDescriptor descriptor : arg) {
			decl = decl.withEnumConstants(cs -> {
				EnumConstantDecl constantDecl = enumConstantDecl().withName(descriptor.name);

				if (GenSettings.generateDocs)
					constantDecl = constantDecl
							.insertLeadingComment("/** Kind for " + descriptor.prefixedDescription() + ". */");

				return cs.append(constantDecl);
			});
		}

		return decl;
	}
}
