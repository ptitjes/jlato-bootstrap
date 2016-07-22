package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;

import java.util.Arrays;

import static org.jlato.tree.NodeOption.*;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class StateClass extends TypePattern.OfClass<TreeClassDescriptor> {

	@Override
	protected String makeQuote(TreeClassDescriptor arg) {
		return "public class " + arg.stateTypeName() + " extends " + AllDescriptors.S_NODE + "<..$_> implements ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(ClassDecl decl, TreeClassDescriptor arg) {
		return "A state object for " + arg.prefixedDescription() + ".";
	}

	@Override
	protected ClassDecl contributeSignature(ClassDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
		NodeList<QualifiedName> superStateInterfaceNames = arg.superInterfaces
				.filter(t -> !AllDescriptors.UTILITY_INTERFACES.contains(t.name()))
				.map(t -> AllDescriptors.asStateTypeQualifiedName(t.name()));

		importManager.addImportByName(AllDescriptors.S_NODE_QUALIFIED);
		importManager.addImportsByName(superStateInterfaceNames);

		return decl
				.withExtendsClause(
						qualifiedType(AllDescriptors.S_NODE).withTypeArgs(listOf(arg.stateType()))
				)
				.withImplementsClause(superStateInterfaceNames.map(n -> qualifiedType(n.name())));
	}

	@Override
	protected Iterable<DeclContribution<TreeClassDescriptor, MemberDecl>> contributions(TreeClassDescriptor arg) {
		return Arrays.asList(
				new StateBaseMembers(),
				new StateEqualsAndHashCode(),
				new PropertyAndTraversalClasses(),
				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}
}
