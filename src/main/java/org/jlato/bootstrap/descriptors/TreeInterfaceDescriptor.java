package org.jlato.bootstrap.descriptors;

import org.jlato.tree.NodeList;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import static org.jlato.rewrite.Quotes.type;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeInterfaceDescriptor extends TreeTypeDescriptor {

	public TreeInterfaceDescriptor(Name packageName, Name name, String description,
	                               NodeList<QualifiedType> superInterfaces) {
		super(packageName, name, description, superInterfaces);
	}

	@Override
	public boolean isInterface() {
		return true;
	}

	@Override
	public String treeFilePath() {
		return "org/jlato/tree/" + packageName + "/" + name + ".java";
	}

	@Override
	public QualifiedType stateType() {
		return qualifiedType().withScope(some(qualifiedType().withName(name))).withName(STATE_NAME);
	}

	public static final TreeInterfaceDescriptor[] ALL = new TreeInterfaceDescriptor[]{
			new TreeInterfaceDescriptor(new Name("decl"), new Name("Decl"), "declaration",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("ExtendedModifier"), "extended modifier",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("MemberDecl"), "member declaration",
					NodeList.of(
							(QualifiedType) type("Decl").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("decl"), new Name("TypeDecl"), "type declaration",
					NodeList.of(
							(QualifiedType) type("MemberDecl").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("expr"), new Name("AnnotationExpr"), "annotation expression",
					NodeList.of(
							(QualifiedType) type("Expr").build(),
							(QualifiedType) type("ExtendedModifier").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("expr"), new Name("Expr"), "expression",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("stmt"), new Name("Stmt"), "statement",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("type"), new Name("ReferenceType"), "reference type",
					NodeList.of(
							(QualifiedType) type("Type").build()
					)
			),
			new TreeInterfaceDescriptor(new Name("type"), new Name("Type"), "type",
					NodeList.of(
							(QualifiedType) type("Tree").build()
					)
			),
	};
}
