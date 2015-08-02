package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.MethodDecl;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.blockStmt;

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
		public MethodDecl rewrite(MethodDecl decl, TreeClassDescriptor arg) {
			// Add STree factory method
			decl = decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return Kind." + arg.name + ";").build()
			))));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
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