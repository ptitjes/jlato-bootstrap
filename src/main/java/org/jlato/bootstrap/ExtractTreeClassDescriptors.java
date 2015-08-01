package org.jlato.bootstrap;

import org.jlato.bootstrap.ast.TreeClassDescriptor;
import org.jlato.rewrite.Substitution;
import org.jlato.tree.NodeList;
import org.jlato.tree.TreeSet;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.expr.FieldAccessExpr;
import org.jlato.tree.expr.LiteralExpr;
import org.jlato.tree.name.Name;
import org.jlato.tree.stmt.Stmt;
import org.jlato.tree.type.PrimitiveType;
import org.jlato.tree.type.QualifiedType;
import org.jlato.tree.type.Type;

import java.util.Comparator;
import java.util.List;

import static org.jlato.rewrite.Quotes.expr;
import static org.jlato.rewrite.Quotes.param;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.*;
import static org.jlato.tree.expr.BinaryExpr.BinaryOp.Equal;
import static org.jlato.tree.expr.BinaryExpr.BinaryOp.NotEqual;
import static org.jlato.tree.expr.UnaryExpr.UnaryOp.Not;

/**
 * @author Didier Villevalois
 */
public class ExtractTreeClassDescriptors extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	private List<TreeClassDescriptor> descriptors = new java.util.ArrayList<>();

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {
		final NodeList<FormalParameter> params = collectConstructorParams(decl);
		if (params == null) return decl;

		final Name name = decl.name();
		final Name packageName = ((CompilationUnit) decl.parent().parent()).packageDecl().name().name();

		final NodeList<QualifiedType> implementsClause = decl.implementsClause();
		final Name superTypeName = implementsClause.get(0).name();

		descriptors.add(new TreeClassDescriptor(packageName, name, superTypeName, params));

		return decl;
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {

		descriptors.sort((o1, o2) -> (o1.packageName.id() + "." + o1.name.id()).compareTo(o2.packageName.id() + "." + o2.name.id()));

		for (TreeClassDescriptor descriptor : descriptors) {
			final Expr creation = expr(
					"new TreeClassDescriptor(new Name(\"" + descriptor.packageName.id() + "\"), "+
							"new Name(\"" + descriptor.name.id() + "\"), " +
							"new Name(\"" + descriptor.superTypeName.id() + "\"), " +
							(descriptor.parameters.isEmpty() ?
									"NodeList.<FormalParameter>empty()" :
									descriptor.parameters.map(p ->
													expr("param(\"" + p.toString() + "\").build()").build()
									).mkString("NodeList.of(", ", ", ")")
							)
							+ ")"
			).build();
			System.out.print(creation.toString());
			System.out.println(",");
		}

		return treeSet;
	}
}
