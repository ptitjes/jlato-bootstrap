package org.jlato.bootstrap;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.parser.ParseException;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.util.List;

import static org.jlato.rewrite.Quotes.expr;
import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ExtractTreeDescriptors extends TreeClassRefactoring {

	@Override
	public TreeSet<CompilationUnit> initialize(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {
		return super.initialize(treeSet, hierarchy);
	}

	private List<TreeInterfaceDescriptor> interfaceDescriptors = new java.util.ArrayList<>();
	private List<TreeClassDescriptor> classDescriptors = new java.util.ArrayList<>();

	@Override
	public InterfaceDecl refactorTreeInterface(TreeSet<CompilationUnit> treeSet, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
		final Name name = decl.name();
		final Name packageName = ((CompilationUnit) decl.parent().parent()).packageDecl().name().name();

		final NodeList<QualifiedType> superInterfaces = decl.extendsClause();

		final NodeList<MemberDecl> shapes = listOf(decl.findAll(
				memberDecl("LexicalShape $_ = $_;")
		)).map(m -> ((FieldDecl) m).withModifiers(emptyList()));

		final NodeList<FormalParameter> parameters = listOf(decl.findAll(
				memberDecl("$_ $_ ();")
		)).map(m -> {
			final MethodDecl methodDecl = (MethodDecl) m;
			return formalParameter(methodDecl.type(), variableDeclaratorId(methodDecl.name()));
		});

		// TODO Fix parameters collection for interfaces
		interfaceDescriptors.add(new TreeInterfaceDescriptor(packageName, name, makeDocumentationName(name), superInterfaces, shapes, parameters));

		return decl;
	}

	@Override
	public ClassDecl refactorTreeClass(TreeSet<CompilationUnit> treeSet, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {
		final NodeList<FormalParameter> params = collectConstructorParams(decl);

		final Name name = decl.name();
		final Name packageName = ((CompilationUnit) decl.parent().parent()).packageDecl().name().name();

		final NodeList<QualifiedType> superInterfaces = decl.implementsClause();

		final NodeList<MemberDecl> shapes = listOf(decl.findAll(
				memberDecl("public static final LexicalShape $_ = $_;")
		)).map(m -> ((FieldDecl) m).withModifiers(listOf(Modifier.Public, Modifier.Static, Modifier.Final)));

		classDescriptors.add(new TreeClassDescriptor(packageName, name, makeDocumentationName(name),
				superInterfaces, shapes,
				params == null ? emptyList() : params,
				params == null ? emptyList() : params.map(p -> null),
				params == null));

		return decl;
	}

	@Override
	public TreeSet<CompilationUnit> finish(TreeSet<CompilationUnit> treeSet, TreeTypeHierarchy hierarchy) {

		classDescriptors.sort((o1, o2) -> (o1.packageName.id() + "." + o1.name.id()).compareTo(o2.packageName.id() + "." + o2.name.id()));

		System.out.println("public static final TreeInterfaceDescriptor[] ALL_INTERFACES = new TreeInterfaceDescriptor[] {");
		for (TreeInterfaceDescriptor descriptor : interfaceDescriptors) {
			final Expr creation = expr(
					"new TreeInterfaceDescriptor(name(\"" + descriptor.packageName + "\"), " +
							"name(\"" + descriptor.name + "\"), " +
							"\"" + descriptor.description + "\",\n" +
							(descriptor.superInterfaces.isEmpty() ?
									"NodeList.<QualifiedType>empty()" :
									descriptor.superInterfaces.map(t -> reify(t)).mkString("listOf(\n", ",\n", "\n)")
							) + ",\n" +
							(descriptor.shapes.isEmpty() ?
									"NodeList.<MemberDecl>empty()" :
									descriptor.shapes.map(d -> reify(d)).mkString("listOf(\n", ",\n", "\n)")
							) + ",\n" +
							(descriptor.parameters.isEmpty() ?
									"NodeList.<FormalParameter>empty()" :
									descriptor.parameters.map(p -> reify(p)).mkString("listOf(\n", ",\n", "\n)")
							) + "\n)"
			).build();
			System.out.print(creation);
			System.out.println(",");
		}
		System.out.println("};");
		System.out.println();
		System.out.println();

		classDescriptors.sort((o1, o2) -> (o1.packageName.id() + "." + o1.name.id()).compareTo(o2.packageName.id() + "." + o2.name.id()));

		System.out.println("public static final TreeClassDescriptor[] ALL_CLASSES = new TreeClassDescriptor[] {");
		for (TreeClassDescriptor descriptor : classDescriptors) {
			final Expr creation = expr(
					"new TreeClassDescriptor(name(\"" + descriptor.packageName + "\"), " +
							"name(\"" + descriptor.name + "\"), " +
							"\"" + descriptor.description + "\",\n" +
							(descriptor.superInterfaces.isEmpty() ?
									"NodeList.<QualifiedType>empty()" :
									descriptor.superInterfaces.map(t -> reify(t)).mkString("listOf(\n", ",\n", "\n)")
							) + ",\n" +
							(descriptor.shapes.isEmpty() ?
									"NodeList.<MemberDecl>empty()" :
									descriptor.shapes.map(d -> reify(d)).mkString("listOf(\n", ",\n", "\n)")
							) + ",\n" +
							(descriptor.parameters.isEmpty() ?
									"NodeList.<FormalParameter>empty()" :
									descriptor.parameters.map(p -> reify(p)).mkString("listOf(\n", ",\n", "\n)")
							) + ",\n" +
							(descriptor.defaultValues.isEmpty() ?
									"NodeList.<Expr>empty()" :
									descriptor.defaultValues.mkString("NodeList.<Expr>of(\n(Expr) ", ",\n(Expr) ", "\n)")
							) + ",\n" +
							(descriptor.customTailored ? "true" : "false") + "\n)"
			).build();
			System.out.print(creation);
			System.out.println(",");
		}
		System.out.println("};");

		return treeSet;
	}

	private Expr reify(QualifiedType e) {
		return reify("(QualifiedType) type", e);
	}

	private Expr reify(FormalParameter p) {
		return reify("param", p);
	}

	private Expr reify(MemberDecl d) {
		return reify("memberDecl", d);
	}

	private Expr reify(String kind, Tree d) {
		final String asString = d.toString();
		final String escaped = asString.replace("\n", "\\n\" +\n\"").replace("\t", "\\t");
		return expr(kind + "(\"" + escaped + "\").build()").build();
	}

	private String makeDocumentationName(Name name) {
		List<String> words = extractWords(name.id());

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
