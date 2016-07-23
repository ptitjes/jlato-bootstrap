package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.MemberDecl;

import java.util.Arrays;

import static org.jlato.tree.Trees.listOf;
import static org.jlato.tree.Trees.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeClass extends TypePattern.OfClass<TreeClassDescriptor> {

	@Override
	protected String makeQuote(TreeClassDescriptor arg) {
		return "public class " + arg.className() + " extends " + AllDescriptors.TD_TREE + "<..$_> implements ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor arg) {
		return Utils.upperCaseFirst(arg.prefixedDescription()) + ".";
	}

	@Override
	protected ClassDecl contributeSignature(ClassDecl classDecl, ImportManager importManager, TreeClassDescriptor arg) {
		importManager.addImportByName(arg.stateTypeQualifiedName());

		classDecl = classDecl
				.withExtendsClause(
						qualifiedType(AllDescriptors.TD_TREE)
								.withTypeArgs(listOf(
										arg.stateType(),
										arg.superInterfaces.get(0),
										arg.interfaceType()
								))
				)
				.withImplementsClause(listOf(arg.interfaceType()));

		importManager.addImportByName(AllDescriptors.TD_TREE_QUALIFIED);
		AllDescriptors.addImports(importManager, arg.superInterfaces.get(0));
		AllDescriptors.addImports(importManager, arg.interfaceType());
		return classDecl;
	}

	@Override
	protected Iterable<DeclContribution<TreeClassDescriptor, MemberDecl>> contributions(TreeClassDescriptor arg) {
		return Arrays.asList(
				new TreeKind(),
				new TreeConstruction(),
//				new TreeKind(),
				new TreeClassAccessors()//,
//				a -> Arrays.asList(new StateClass()),
//				new PropertyAndTraversalClasses(),
//				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}
}
