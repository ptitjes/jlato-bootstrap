package org.jlato.bootstrap;

import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.parser.ParseException;
import org.jlato.tree.NodeList;
import org.jlato.tree.NodeMap;
import org.jlato.tree.decl.*;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.util.List;

import static org.jlato.pattern.Quotes.expr;
import static org.jlato.pattern.Quotes.memberDecl;
import static org.jlato.tree.Trees.*;

/**
 * @author Didier Villevalois
 */
public class ExtractTreeDescriptors extends TreeClassRefactoring {

	@Override
	public NodeMap<CompilationUnit> initialize(NodeMap<CompilationUnit> nodeMap, TreeTypeHierarchy hierarchy) {
		return super.initialize(nodeMap, hierarchy);
	}

	private List<TreeInterfaceDescriptor> interfaceDescriptors = new java.util.ArrayList<>();
	private List<TreeClassDescriptor> classDescriptors = new java.util.ArrayList<>();

	@Override
	public InterfaceDecl refactorTreeInterface(NodeMap<CompilationUnit> nodeMap, String path, InterfaceDecl decl, TreeTypeHierarchy hierarchy) throws ParseException {
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
	public ClassDecl refactorTreeClass(NodeMap<CompilationUnit> nodeMap, String path, ClassDecl decl, TreeTypeHierarchy hierarchy) {
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
	public NodeMap<CompilationUnit> finish(NodeMap<CompilationUnit> nodeMap, TreeTypeHierarchy hierarchy) {

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

		return nodeMap;
	}
}
