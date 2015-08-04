package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.*;

import java.util.Arrays;

import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreePureInterface extends CompilationUnitPattern<TreeTypeDescriptor> {

	@Override
	protected Iterable<DeclContribution<TreeTypeDescriptor, TypeDecl>> contributions(TreeTypeDescriptor arg) {
		return Arrays.asList(
				a -> Arrays.asList(
						new TypePattern.OfInterface<TreeTypeDescriptor>() {
							@Override
							protected String makeQuote(TreeTypeDescriptor arg) {
								return "public interface " + arg.name + " extends ..$_ { ..$_ }";
							}

							@Override
							protected String makeDoc(InterfaceDecl decl, TreeTypeDescriptor arg) {
								return "/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */";
							}

							@Override
							protected InterfaceDecl contributeSignature(InterfaceDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
								NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
								AllDescriptors.addImports(importManager, parentInterfaces);

								if (!arg.isInterface()) {
									importManager.addImportByName(qualifiedName("org.jlato.tree.TreeCombinators"));
									parentInterfaces = parentInterfaces.append(
											qualifiedType(name("TreeCombinators")).withTypeArgs(some(listOf(
													arg.interfaceType()
											)))
									);
								}

								return decl.withExtendsClause(parentInterfaces);
							}

							@Override
							protected Iterable<DeclContribution<TreeTypeDescriptor, MemberDecl>> contributions(TreeTypeDescriptor arg) {
								return Arrays.asList(new TreePureInterfaceAccessors());
							}
						}
				)
		);
	}
}
