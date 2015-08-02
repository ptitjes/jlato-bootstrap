package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.NodeOption;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.Decl;
import org.jlato.tree.decl.FieldDecl;
import org.jlato.tree.decl.TypeDecl;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClass extends TypePattern.OfClass<TreeClassDescriptor> {

	public TreeClass() {
		super(
				new TreeKind(),
				new TreeConstruction(),
//				new TreeKind(),
				new TreeAccessors(),
				a -> Arrays.asList(new StateClass()),
				new PropertyAndTraversalClasses(),
				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}

	@Override
	public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
		return typeDecl(
				"public class " + arg.name + " extends TreeBase<..$_> implements ..$_ { ..$_ }"
		);
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeClassDescriptor arg) {
		ClassDecl classDecl = (ClassDecl) super.rewrite(decl, arg);

		classDecl = classDecl
				.withExtendsClause(some(
						qualifiedType()
								.withName(TreeTypeDescriptor.TREE_BASE_NAME)
								.withTypeArgs(some(NodeList.of(
										arg.stateType(),
										arg.superInterfaces.get(0),
										arg.type()
								)))
				))
				.withImplementsClause(arg.superInterfaces);

		if (GenSettings.generateDocs)
			classDecl = classDecl.insertLeadingComment("/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */");

		return classDecl;
	}
}
