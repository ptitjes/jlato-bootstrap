package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;

import java.util.Arrays;

import static org.jlato.tree.NodeOption.*;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class TreeClass extends TypePattern.OfClass<TreeClassDescriptor> {

	@Override
	protected String makeQuote(TreeClassDescriptor arg) {
		return "public class " + arg.name + " extends TreeBase<..$_> implements ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor arg) {
		return "/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */";
	}

	@Override
	protected ClassDecl contributeSignature(ClassDecl classDecl, ImportManager importManager, TreeClassDescriptor arg) {
		classDecl = classDecl
				.withExtendsClause(some(
						qualifiedType(TreeTypeDescriptor.TREE_BASE_NAME)
								.withTypeArgs(some(NodeList.of(
										arg.stateType(),
										arg.superInterfaces.get(0),
										arg.type()
								)))
				))
				.withImplementsClause(arg.superInterfaces);
		return classDecl;
	}

	@Override
	protected Iterable<DeclContribution<TreeClassDescriptor, MemberDecl>> contributions(TreeClassDescriptor arg) {
		return Arrays.asList(
				new TreeKind(),
				new TreeConstruction(),
//				new TreeKind(),
				new TreeClassAccessors(),
				a -> Arrays.asList(new StateClass()),
				new PropertyAndTraversalClasses(),
				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}
}
