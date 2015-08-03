package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.QualifiedType;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.typeDecl;

/**
 * @author Didier Villevalois
 */
public class TreeInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	public TreeInterface() {
		super(
				new TreeInterfaceAccessors(),
				a -> Arrays.asList(new StateInterface()),
				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}

	@Override
	public Pattern<? extends Decl> matcher(TreeInterfaceDescriptor arg) {
		return typeDecl(
				"public interface " + arg.name + " extends ..$_ { ..$_ }"
		);
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, ImportManager importManager, TreeInterfaceDescriptor arg) {
		InterfaceDecl interfaceDecl = (InterfaceDecl) super.rewrite(decl, importManager, arg);

		NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && parentInterfaces.get(0).name().equals(TreeTypeDescriptor.TREE_NAME);

		interfaceDecl = interfaceDecl.withExtendsClause(parentInterfaces);

		if (GenSettings.generateDocs)
			interfaceDecl = interfaceDecl.insertLeadingComment("/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */");

		return interfaceDecl;
	}
}
