package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
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
		return "public class " + arg.className() + " extends TreeBase<..$_> implements ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor arg) {
		return "/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */";
	}

	@Override
	protected ClassDecl contributeSignature(ClassDecl classDecl, ImportManager importManager, TreeClassDescriptor arg) {
		importManager.addImportByName(arg.stateTypeQualifiedName());

		classDecl = classDecl
				.withExtendsClause(some(
						qualifiedType(AllDescriptors.TD_TREE)
								.withTypeArgs(some(NodeList.of(
										arg.stateType(),
										arg.superInterfaces.get(0),
										arg.interfaceType()
								)))
				))
				.withImplementsClause(NodeList.of(arg.interfaceType()));

		importManager.addImportByName(qualifiedName("org.jlato.internal.td.TreeBase"));
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
