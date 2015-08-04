package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.*;

import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class TreeConstruction implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		return Arrays.asList(
				new TreePrivateConstructor(),
				new TreePublicConstructor()
		);
	}

	public static class TreePrivateConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.className() + "(" + AllDescriptors.TD_LOCATION + "<" + arg.name + ".State> location) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.TD_LOCATION_QUALIFIED);

			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qualifiedType(AllDescriptors.TD_LOCATION).withTypeArgs(some(listOf(stateType)));

			final Name location = name("location");

			decl = constructorDecl(arg.className(),
					blockStmt().withStmts(listOf(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(listOf(location))
					)))
					.withModifiers(listOf(Modifier.Public))
					.withParams(listOf(
							formalParameter(locationType, variableDeclaratorId(location))
					));

			if (GenSettings.generateDocs)
				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " for the specified tree location.",
								new String[]{"the tree location."}
						)
				);

			return decl;
		}
	}

	public static class TreePublicConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.className() + "(" + arg.parameters.mkString("", ", ", "") + ") { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			for (FormalParameter parameter : arg.parameters) {
				if (!propertyFieldType(parameter.type())) {
					final QualifiedType type = (QualifiedType) parameter.type();
					importManager.addImportByName(AllDescriptors.asStateTypeQualifiedName(type.name()));
				}
			}

			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qualifiedType(AllDescriptors.TD_LOCATION).withTypeArgs(some(listOf(stateType)));
			final NodeList<FormalParameter> parameters = arg.parameters;

			// Make TDLocation creation expression from Trees
			final ObjectCreationExpr tdLocationCreationExpr = objectCreationExpr(locationType)
					.withArgs(listOf(
							methodInvocationExpr(name("make"))
									.withScope(some(arg.stateTypeName()))
									.withArgs(parameters.map(p -> {
										Type treeType = p.type();
										if (propertyFieldType(treeType)) return p.id().name();
										else return methodInvocationExpr(name("treeOf"))
												.withScope(some(AllDescriptors.TD_TREE))
												.withTypeArgs(listOf(treeTypeToStateType((QualifiedType) treeType)))
												.withArgs(listOf(p.id().name()));
									}))
					));

			decl = constructorDecl(arg.className(),
					blockStmt().withStmts(listOf(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(listOf(tdLocationCreationExpr))
					)))
					.withModifiers(listOf(Modifier.Public))
					.withParams(parameters);

			if (GenSettings.generateDocs) {
				// TODO document the arguments

				decl = decl.insertLeadingComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " with the specified content.",
								new String[]{"the object to compare this state with."}
						)
				);
			}

			return decl;
		}
	}
}
