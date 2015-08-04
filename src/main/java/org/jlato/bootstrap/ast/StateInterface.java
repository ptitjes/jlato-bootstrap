package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.name.*;
import org.jlato.tree.type.*;

import java.util.Arrays;

import static org.jlato.tree.NodeOption.*;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class StateInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	@Override
	protected String makeQuote(TreeInterfaceDescriptor arg) {
		return "public interface " + arg.stateTypeName() + " extends ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(InterfaceDecl decl, TreeInterfaceDescriptor arg) {
		return "/** A state object for " + arg.prefixedDescription() + ". */";
	}

	@Override
	protected InterfaceDecl contributeSignature(InterfaceDecl decl, ImportManager importManager, TreeInterfaceDescriptor arg) {
		// FIXME Imports for the mergeFields directive below
		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes")).setOnDemand(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.LexicalShape")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.LSCondition")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.internal.bu.LToken")));
		importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.IndentationContext")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.SpacingLocation")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.printer.SpacingConstraint")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.printer.IndentationConstraint")).setOnDemand(true).setStatic(true));
		importManager.addImport(importDecl(qualifiedName("org.jlato.tree.Kind")));
		importManager.addImport(importDecl(AllDescriptors.S_NODE_LIST_QUALIFIED));

		NodeList<QualifiedName> superStateInterfaceNames = arg.superInterfaces.map(t -> AllDescriptors.asStateTypeQualifiedName(t.name()));

		importManager.addImportsByName(superStateInterfaceNames);
		return decl.withExtendsClause(superStateInterfaceNames.map(n -> qualifiedType(n.name())));
	}

	@Override
	protected Iterable<DeclContribution<TreeInterfaceDescriptor, MemberDecl>> contributions(TreeInterfaceDescriptor arg) {
		return Arrays.asList(
				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}
}
