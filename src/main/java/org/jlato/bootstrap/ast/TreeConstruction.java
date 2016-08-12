package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.pattern.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.Arrays;

import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class TreeConstruction implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		return Arrays.asList(
				new TreeLocationConstructor(),
				new TreeStandardConstructor()
		);
	}

	public static class TreeLocationConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
		@Override
		public Pattern<? extends Decl> matcher(TreeClassDescriptor arg) {
			return memberDecl("public " + arg.className() + "(" + AllDescriptors.TD_LOCATION + "<" + arg.stateType() + "> location) { ..$_ }");
		}

		@Override
		public ConstructorDecl rewrite(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.TD_LOCATION_QUALIFIED);

			final Name name = arg.name;
			final QualifiedType stateType = arg.stateType();
			final QualifiedType locationType = qualifiedType(AllDescriptors.TD_LOCATION).withTypeArgs(listOf(stateType));

			final Name location = name("location");

			decl = constructorDecl(arg.className())
					.withModifiers(listOf(Modifier.Public))
					.withParams(listOf(
							formalParameter(locationType).withId(variableDeclaratorId(location))
					))
					.withBody(blockStmt().withStmts(listOf(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(listOf(location))
					)));

			if (GenSettings.generateDocs)
				decl = decl.withDocComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " for the specified tree location.",
								new String[]{"the tree location."}
						)
				);

			return decl;
		}
	}

	public static class TreeStandardConstructor extends Utils implements DeclPattern<TreeClassDescriptor, ConstructorDecl> {
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
			final QualifiedType locationType = qualifiedType(AllDescriptors.TD_LOCATION).withTypeArgs(listOf(stateType));
			final NodeList<FormalParameter> parameters = arg.parameters;

			// Make TDLocation creation expression from Trees
			final ObjectCreationExpr tdLocationCreationExpr = objectCreationExpr(locationType)
					.withArgs(listOf(
							methodInvocationExpr(name("make"))
									.withScope(arg.stateTypeName())
									.withArgs(parameters.map(p -> {
										Type treeType = p.type();
										if (propertyFieldType(treeType)) return p.id().get().name();
										else return methodInvocationExpr(name("treeOf"))
												.withScope(AllDescriptors.TD_TREE)
												.withTypeArgs(listOf(treeTypeToStateType((QualifiedType) treeType)))
												.withArgs(listOf(p.id().get().name()));
									}))
					));

			decl = constructorDecl(arg.className())
					.withModifiers(listOf(Modifier.Public))
					.withParams(parameters)
					.withBody(blockStmt().withStmts(listOf(
							explicitConstructorInvocationStmt()
									.setThis(false)
									.withArgs(listOf(tdLocationCreationExpr))
					)));

			if (GenSettings.generateDocs) {
				// TODO document the arguments

				decl = decl.withDocComment(
						genDoc(decl,
								"Creates " + arg.prefixedDescription() + " with the specified child trees.",
								paramDoc(parameters, p -> "the " + makeDocumentationName(p.id().get().name()) + " child tree.")
						)
				);
			}

			return decl;
		}
	}
}
