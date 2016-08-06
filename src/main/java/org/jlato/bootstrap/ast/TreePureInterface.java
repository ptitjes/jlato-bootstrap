package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.CompilationUnitPattern;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.pattern.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.type.QualifiedType;

import java.util.Arrays;
import java.util.List;

import static org.jlato.pattern.Quotes.memberDecl;
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

									List<Pattern<MemberDecl>> modifiers = Arrays.asList(
											memberDecl("Modifier Public = modifier(ModifierKeyword.Public);"),
											memberDecl("Modifier Protected = modifier(ModifierKeyword.Protected);"),
											memberDecl("Modifier Private = modifier(ModifierKeyword.Private);"),
											memberDecl("Modifier Abstract = modifier(ModifierKeyword.Abstract);"),
											memberDecl("Modifier Default = modifier(ModifierKeyword.Default);"),
											memberDecl("Modifier Static = modifier(ModifierKeyword.Static);"),
											memberDecl("Modifier Final = modifier(ModifierKeyword.Final);"),
											memberDecl("Modifier Transient = modifier(ModifierKeyword.Transient);"),
											memberDecl("Modifier Volatile = modifier(ModifierKeyword.Volatile);"),
											memberDecl("Modifier Synchronized = modifier(ModifierKeyword.Synchronized);"),
											memberDecl("Modifier Native = modifier(ModifierKeyword.Native);"),
											memberDecl("Modifier StrictFP = modifier(ModifierKeyword.StrictFP);")
									);

									for (Pattern<MemberDecl> modifier : modifiers) {
										if (!decl.findAll(modifier).iterator().hasNext())
											decl = decl.withMembers(ms -> ms.append(modifier.build()));
									}
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
