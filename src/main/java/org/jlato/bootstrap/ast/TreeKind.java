package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.pattern.Pattern;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;

import java.util.Arrays;

import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.pattern.Quotes.stmt;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreeKind implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		return Arrays.asList(
				new KindMethod()
		);
	}

	public static class KindMethod extends Utils implements DeclPattern<TreeClassDescriptor, MethodDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public Kind kind() { ..$_ }");
		}

		@Override
		public MethodDecl rewrite(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(qualifiedName("org.jlato.tree.Kind"));

			// Add BUTree factory method
			decl = decl.withBody(blockStmt().withStmts(listOf(
					stmt("return Kind." + arg.name + ";").build()
			)));

			if (GenSettings.generateDocs)
				decl = decl.withDocComment(
						genDoc(decl,
								"Returns the kind of this " + arg.description + ".",
								new String[]{},
								"the kind of this " + arg.description + "."
						)
				);

			return decl;
		}
	}
}
