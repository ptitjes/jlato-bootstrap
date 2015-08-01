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

		final Name name = decl.name();
		final Name packageName = ((CompilationUnit) decl.parent().parent()).packageDecl().name().name();

		final NodeList<QualifiedType> implementsClause = decl.implementsClause();
		final Name superTypeName = implementsClause.get(0).name();

		descriptors.add(new TreeClassDescriptor(packageName, name, params == null, superTypeName, "", params == null ? NodeList.empty() : params));

		return decl;
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {

		descriptors.sort((o1, o2) -> (o1.packageName.id() + "." + o1.name.id()).compareTo(o2.packageName.id() + "." + o2.name.id()));

		System.out.println("public static final TreeClassDescriptor[] ALL = new TreeClassDescriptor[] {");
		for (TreeClassDescriptor descriptor : descriptors) {
			final Expr creation = expr(
					"new TreeClassDescriptor(new Name(\"" + descriptor.packageName + "\"), " +
							"new Name(\"" + descriptor.name + "\"), " +
							(descriptor.customTailored ? "true" : "false") + ", " +
							"new Name(\"" + descriptor.superTypeName.id() + "\"),\n" +
							"\"" + makeDocumentationName(descriptor) + "\",\n" +
							(descriptor.parameters.isEmpty() ?
									"NodeList.<FormalParameter>empty()" :
									descriptor.parameters.map(p ->
													expr("param(\"" + p + "\").build()").build()
									).mkString("NodeList.of(\n", ",\n", "\n)")
							)
							+ "\n)"
			).build();
			System.out.print(creation);
			System.out.println(",");
		}
		System.out.println("};");

		return treeSet;
	}

	private String makeDocumentationName(TreeClassDescriptor descriptor) {
		final String name = descriptor.name.id();

		List<String> words = extractWords(name);

		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (String word : words) {
			if (!first) {
				buffer.append(" ");
			} else first = false;

			word = mapWord(word);
			buffer.append(word);
		}

		return mapDocumentation(buffer.toString());
	}

	private String mapWord(String word) {
		switch (word) {
			case "foreach":
				return "\\\"enhanced\\\" 'for'";
			case "do":
				return "'do-while'";
			case "while":
			case "for":
			case "if":
			case "switch":
			case "try":
			case "throw":
			case "synchronized":
			case "return":
			case "continue":
			case "break":
			case "assert":

			case "this":
			case "super":
				return "'" + word + "'";

			case "decl":
				return "declaration";
			case "stmt":
				return "statement";
			case "expr":
				return "expression";

			case "dim":
				return "dimension";
			case "id":
				return "identifier";
		}
		return word;
	}

	private String mapDocumentation(String documentation) {
		switch (documentation) {
			case "class expression":
				return "'class' expression";
			case "instance of expression":
				return "'instanceof' expression";
			case "member value pair":
				return "annotation member value pair";
			case "assign expression":
				return "assignment expression";
			case "annotation declaration":
				return "annotation type declaration";
			case "annotation member declaration":
				return "annotation type member declaration";
		}
		return documentation;
	}

	private List<String> extractWords(String name) {
		List<String> words = new java.util.ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		boolean first = true;
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c) && !first) {
				words.add(buffer.toString());
				buffer = new StringBuilder();
			}
			if (first) first = false;

			buffer.append(Character.toLowerCase(c));
		}
		words.add(buffer.toString());
		return words;
	}
}
