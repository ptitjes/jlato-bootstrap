package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.MemberPattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.AssignExpr;
import org.jlato.tree.expr.ObjectCreationExpr;
import org.jlato.tree.name.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;

/**
 * @author Didier Villevalois
 */
public class StateBaseMembers extends Utils implements DeclContribution<TreeClassDescriptor, MemberDecl> {

	@Override
	public Iterable<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> declarations(TreeClassDescriptor arg) {
		List<DeclPattern<TreeClassDescriptor, ? extends MemberDecl>> decls = new ArrayList<>();

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
		NodeList<FormalParameter> propertyParams = NodeList.empty();
		NodeList<FormalParameter> traversalParams = NodeList.empty();
		for (FormalParameter param : arg.parameters) {
			if (propertyFieldType(param.type()))
				propertyParams = propertyParams.append(param);
			else
				traversalParams = traversalParams.append(param);
		}

		if (!propertyParams.isEmpty()) {
			decls.add(new AllPropertiesMethod(propertyParams));
		}
		if (!traversalParams.isEmpty()) {
			decls.add(new FirstLastChildMethod("first", traversalParams.first()));
			decls.add(new FirstLastChildMethod("last", traversalParams.last()));
		}
		return decls;
	}

	public static class Constructor extends MemberPattern.OfConstructor<TreeClassDescriptor> {
		@Override
		protected String makeQuote(TreeClassDescriptor arg) {
			return TreeTypeDescriptor.STATE_NAME + "(..$_) { ..$_ }";
		}

		@Override
		protected ConstructorDecl makeDecl(ConstructorDecl decl, TreeClassDescriptor arg) {
			final NodeList<FormalParameter> stateParameters = arg.stateParameters();
			return constructorDecl().withName(TreeTypeDescriptor.STATE_NAME)
					.withParams(stateParameters)
					.withBody(blockStmt().withStmts(
							stateParameters.map(p -> expressionStmt().withExpr(
									assignExpr().withTarget(fieldAccessExpr().withScope(some(thisExpr())).withName(p.id().name()))
											.withOp(AssignExpr.AssignOp.Normal).withValue(p.id().name())
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
			return "@Override public Kind kind() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
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
			return "@Override protected Tree doInstantiate(SLocation<" + arg.name + ".State> location) { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return new " + arg.name + "(location);").build()
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
			return "@Override public LexicalShape shape() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
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
			return "@Override public STraversal " + firstOrLast + "Child() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
					stmt("return " + constantName(param) + ";").build()
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
			return "@Override public Iterable<SProperty> allProperties() { ..$_ }";
		}

		@Override
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
					params.size() == 1 ?
							stmt("return Collections.<SProperty>singleton(" + constantName(params.first()) + ");").build() :
							stmt("return Arrays.<SProperty>asList(" +
									params.map(p -> new Name(constantName(p))).mkString("", ", ", "")
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
		protected FieldDecl makeDecl(FieldDecl decl, TreeClassDescriptor arg) {
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
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
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
		protected MethodDecl makeDecl(MethodDecl decl, TreeClassDescriptor arg) {
			final ObjectCreationExpr stateCreationExpr = objectCreationExpr().withType(arg.stateType())
					.withArgs(arg.parameters.map(p -> p.id().name()));

			return decl.withBody(some(blockStmt().withStmts(NodeList.of(
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
