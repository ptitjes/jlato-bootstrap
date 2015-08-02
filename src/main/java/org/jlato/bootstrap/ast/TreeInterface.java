package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.GenSettings;
import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeClassDescriptor;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.descriptors.TreeTypeDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.DeclPattern;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.rewrite.Pattern;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.ClassDecl;
import org.jlato.tree.decl.InterfaceDecl;
import org.jlato.tree.decl.MemberDecl;
import org.jlato.tree.decl.TypeDecl;
import org.jlato.tree.name.Name;
import org.jlato.tree.type.QualifiedType;

import java.util.Arrays;

import static org.jlato.rewrite.Quotes.typeDecl;
import static org.jlato.tree.NodeOption.some;
import static org.jlato.tree.TreeFactory.interfaceDecl;
import static org.jlato.tree.TreeFactory.qualifiedType;

/**
 * @author Didier Villevalois
 */
public class TreeInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	public TreeInterface() {
		super(
				new StateInterfaceContribution()
		);
	}

	@Override
	public Pattern<TypeDecl> matcher(TreeInterfaceDescriptor arg) {
		return typeDecl(
				"public interface " + arg.name + " extends ..$_ { ..$_ }"
		);
	}

	@Override
	public TypeDecl rewrite(TypeDecl decl, TreeInterfaceDescriptor arg) {
		InterfaceDecl interfaceDecl = (InterfaceDecl) super.rewrite(decl, arg);

		NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
		boolean treeInterfaceChild = parentInterfaces.size() == 1 && parentInterfaces.get(0).name().equals(TreeTypeDescriptor.TREE_NAME);

		interfaceDecl = interfaceDecl.withExtendsClause(parentInterfaces);

		if (GenSettings.generateDocs)
			interfaceDecl = interfaceDecl.insertLeadingComment("/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */");

		return interfaceDecl;
	}

	public static class StateInterfaceContribution implements DeclContribution<TreeInterfaceDescriptor, MemberDecl> {
		@Override
		public Iterable<DeclPattern<TreeInterfaceDescriptor, ? extends MemberDecl>> declarations(TreeInterfaceDescriptor arg) {
			return Arrays.asList(new StateInterface());
		}
	}
}
