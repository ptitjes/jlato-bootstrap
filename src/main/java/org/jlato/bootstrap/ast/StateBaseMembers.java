package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.AllDescriptors;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.*;
import org.jlato.tree.stmt.*;
import org.jlato.tree.type.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.Trees.*;
import static org.jlato.tree.expr.AssignOp.Normal;

/**
 * @author Didier Villevalois
 */
public class StateBaseMembers extends Utils implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();

		decls.add(new MakeMethod());

		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
					new Field(parameter)
			));
		}

		decls.add(new Constructor());
		decls.add(new KindMethod());

		for (FormalParameter parameter : arg.parameters) {
			decls.addAll(Arrays.asList(
//					new Accessor(parameter),
					new Mutator(parameter)
			));
		}

		decls.add(new DoInstantiateMethod());
		decls.add(new ShapeMethod());

		// Preprocess params to find properties and traversals
		NodeList<FormalParameter> propertyParams = emptyList();
		NodeList<FormalParameter> traversalParams = emptyList();
		for (FormalParameter param : arg.parameters) {
			if (propertyFieldType(param.type()))
				propertyParams = propertyParams.append(param);
			else
				traversalParams = traversalParams.append(param);
		}

		if (!propertyParams.isEmpty()) {
			decls.add(new AllPropertiesMethod(propertyParams));
		}
		decls.add(new FirstLastChildMethod("first", traversalParams.isEmpty() ? null : traversalParams.first()));
		decls.add(new FirstLastChildMethod("last", traversalParams.isEmpty() ? null : traversalParams.last()));
		return decls;
	}

	public static class MakeMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public static " + AllDescriptors.BU_TREE + "<" + arg.stateTypeName() + "> make(..$_) { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.BU_TREE_QUALIFIED);
			for (FormalParameter parameter : arg.parameters) {
				if (!propertyFieldType(parameter.type())) {
					final QualifiedType type = (QualifiedType) parameter.type();
					importManager.addImportByName(AllDescriptors.asStateTypeQualifiedName(type.name()));
				}
			}

			final NodeList<FormalParameter> parameters = arg.parameters;
			final NodeList<FormalParameter> stateParams = arg.stateParameters();
			final QualifiedType stateType = arg.stateType();
			final QualifiedType treeType = qualifiedType(AllDescriptors.BU_TREE)
					.withTypeArgs(some(listOf(stateType)));

			// Make BUTree creation expression from STrees
			final ObjectCreationExpr sTreeCreationExpr = objectCreationExpr(treeType)
					.withArgs(listOf(
							objectCreationExpr(stateType).withArgs(parameters.map(p -> p.id().name()))
					));

			// Add BUTree factory method
			return methodDecl(treeType, name("make"))
					.withModifiers(listOf(Modifier.Public, Modifier.Static))
					.withParams(stateParams)
					.withBody(some(blockStmt().withStmts(listOf(
							returnStmt().withExpr(some(sTreeCreationExpr))
					))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Compares this state object to the specified object.",
					new String[]{"the object to compare this state with."},
					"<code>true</code> if the specified object is equal to this state, <code>false</code> otherwise."
			);
		}
	}

	public static class Constructor extends MemberPattern.OfConstructor<TreeClassDescriptor> {
		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + arg.stateTypeName() + "(..$_) { ..$_ }";
		}

		@Override
		protected ConstructorDecl makeDecl(ConstructorDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> stateParameters = arg.stateParameters();
			return constructorDecl(arg.stateTypeName())
					.withModifiers(listOf(Modifier.Public))
					.withParams(stateParameters)
					.withBody(blockStmt().withStmts(
							stateParameters.map(p -> expressionStmt(
									assignExpr(fieldAccessExpr(p.id().name()).withScope(some(thisExpr())), Normal, p.id().name())
							))
					));
		}

		@Override
		protected String makeDoc(ConstructorDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Constructs " + arg.prefixedDescription() + " state.",
					arg.parameterDescriptions()
			);
		}
	}

	public static class KindMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "@Override\npublic Kind kind() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(qualifiedName("org.jlato.tree.Kind"));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return Kind." + arg.name + ";").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the kind of this " + arg.description + ".",
					new String[]{},
					"the kind of this " + arg.description + "."
			);
		}
	}

	public static class DoInstantiateMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {
		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "@Override\nprotected Tree doInstantiate(" + AllDescriptors.TD_LOCATION + "<" + arg.stateType() + "> location) { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.TD_LOCATION_QUALIFIED);
			importManager.addImportByName(AllDescriptors.TREE_QUALIFIED);
			importManager.addImportByName(arg.classQualifiedName());

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return new " + arg.className() + "(location);").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Builds a facade for this " + arg.description + " state.",
					new String[]{},
					"a facade for this " + arg.description + " state."
			);
		}
	}

	public static class ShapeMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {
		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "@Override\npublic LexicalShape shape() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			// FIXME Imports for the mergeFields directive below
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes")).setOnDemand(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.LexicalShape")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.LSCondition")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.bu.LToken")));
			importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.IndentationContext")));
			importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.SpacingLocation")));
			importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.IndentationContext")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.printer.FormattingSettings.SpacingLocation")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.SpacingConstraint")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.internal.shapes.IndentationConstraint")).setOnDemand(true).setStatic(true));
			importManager.addImport(importDecl(qualifiedName("org.jlato.parser.ParserImplConstants")));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return shape;").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the shape for this " + arg.description + " state.",
					new String[]{},
					"the shape for this " + arg.description + " state."
			);
		}
	}

	public static class FirstLastChildMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final String firstOrLast;
		private final FormalParameter param;

		public FirstLastChildMethod(String firstOrLast, FormalParameter param) {
			this.firstOrLast = firstOrLast;
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "@Override\npublic STraversal " + firstOrLast + "Child() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(qualifiedName("org.jlato.internal.bu.STraversal"));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					stmt("return " + (param == null ? "null" : constantName(param)) + ";").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the " + firstOrLast + " child traversal for this " + arg.description + " state.",
					new String[]{},
					"the " + firstOrLast + " child traversal for this " + arg.description + " state."
			);
		}
	}

	public static class AllPropertiesMethod extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final NodeList<FormalParameter> params;

		public AllPropertiesMethod(NodeList<FormalParameter> params) {
			this.params = params;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "@Override\npublic Iterable<SProperty> allProperties() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			if (params.size() == 1) importManager.addImportByName(qualifiedName("java.util.Collections"));
			else importManager.addImportByName(qualifiedName("java.util.Arrays"));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					params.size() == 1 ?
							stmt("return Collections.<SProperty>singleton(" + constantName(params.first()) + ");").build() :
							stmt("return Arrays.<SProperty>asList(" +
									params.map(p -> name(constantName(p))).mkString("", ", ", "")
									+ ");").build()
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the properties for this " + arg.description + " state.",
					new String[]{},
					"the properties for this " + arg.description + " state."
			);
		}
	}

	public static class Field extends MemberPattern.OfField<TreeClassDescriptor> {

		private final FormalParameter param;

		public Field(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public final " + treeTypeToSTreeType(param.type()) + " " + param.id().name() + ";";
		}

		@Override
		protected FieldDecl makeDecl(FieldDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			importManager.addImportByName(AllDescriptors.BU_TREE_QUALIFIED);
			for (FormalParameter parameter : arg.parameters) {
				final Type paramType = parameter.type();
				if (!propertyFieldType(paramType)) {
					importManager.addImportByName(AllDescriptors.asStateTypeQualifiedName(((QualifiedType) paramType).name()));
				} else if (paramType instanceof QualifiedType) {
					final QualifiedName qualifiedName = AllDescriptors.resolve(((QualifiedType) paramType).name());
					if (qualifiedName != null) {
						importManager.addImportByName(qualifiedName);
					}
				}
			}

			return decl;
		}

		@Override
		protected String makeDoc(FieldDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"The " + param.id().name() + " of this " + arg.description + " state."
			);
		}
	}

	public static class Accessor extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final FormalParameter param;

		public Accessor(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + treeTypeToSTreeType(param.type()) + " " + param.id().name() + "() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(listOf(
					returnStmt().withExpr(some(param.id().name()))
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + " state.",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + " state."
			);
		}
	}

	public static class Mutator extends MemberPattern.OfMethod<TreeClassDescriptor> {

		private final FormalParameter param;

		public Mutator(FormalParameter param) {
			this.param = param;
		}

		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return "public " + arg.stateType() + " " + propertySetterName(param) + "(" + treeTypeToSTreeType(param.type()) + " " + param.id().name() + ") { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, ImportManager importManager, TreeClassDescriptor arg) {
			final ObjectCreationExpr stateCreationExpr = objectCreationExpr(arg.stateType())
					.withArgs(arg.parameters.map(p -> p.id().name()));

			return decl.withBody(some(blockStmt().withStmts(listOf(
					returnStmt().withExpr(some(stateCreationExpr))
			))));
		}

		@Override
		protected String makeDoc(MethodDecl decl, TreeClassDescriptor arg) {
			return genDoc(decl,
					"Returns the " + param.id().name() + " of this " + arg.description + ".",
					new String[]{},
					"the " + param.id().name() + " of this " + arg.description + "."
			);
		}
	}
}
