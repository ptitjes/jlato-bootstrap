package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.type.QualifiedType;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.memberDecl;
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
								return Utils.upperCaseFirst(arg.prefixedDescription()) + ".";
							}

							@Override
							protected InterfaceDecl contributeSignature(InterfaceDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
								NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
								AllDescriptors.addImports(importManager, parentInterfaces);

								if (!arg.isInterface()) {
									importManager.addImportByName(qualifiedName("org.jlato.tree.TreeCombinators"));
									parentInterfaces = parentInterfaces.append(
											qualifiedType(name("TreeCombinators")).withTypeArgs(listOf(
													arg.interfaceType()
											))
									);
								}

								return decl.withExtendsClause(parentInterfaces);
							}

							@Override
							protected InterfaceDecl contributeBody(InterfaceDecl decl, ImportManager importManager, TreeTypeDescriptor arg) {
								decl = super.contributeBody(decl, importManager, arg);

								if (arg.name.id().equals("Modifier")) {
									importManager.addImport(importDecl(qualifiedName("org.jlato.tree.Trees.modifier")).setStatic(true));
									decl = decl.withMembers(ms -> ms.appendAll(listOf(
											memberDecl("\tModifier Public = modifier(ModifierKeyword.Public);").build(),
											memberDecl("Modifier Protected = modifier(ModifierKeyword.Protected);").build(),
											memberDecl("Modifier Private = modifier(ModifierKeyword.Private);").build(),
											memberDecl("Modifier Abstract = modifier(ModifierKeyword.Abstract);").build(),
											memberDecl("Modifier Default = modifier(ModifierKeyword.Default);").build(),
											memberDecl("Modifier Static = modifier(ModifierKeyword.Static);").build(),
											memberDecl("Modifier Final = modifier(ModifierKeyword.Final);").build(),
											memberDecl("Modifier Transient = modifier(ModifierKeyword.Transient);").build(),
											memberDecl("Modifier Volatile = modifier(ModifierKeyword.Volatile);").build(),
											memberDecl("Modifier Synchronized = modifier(ModifierKeyword.Synchronized);").build(),
											memberDecl("Modifier Native = modifier(ModifierKeyword.Native);").build(),
											memberDecl("Modifier StrictFP = modifier(ModifierKeyword.StrictFP);").build()
									)));
								}

								return decl;
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
