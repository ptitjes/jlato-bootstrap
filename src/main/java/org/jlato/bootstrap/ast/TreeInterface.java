package org.jlato.bootstrap.ast;

import org.jlato.bootstrap.Utils;
import org.jlato.bootstrap.descriptors.TreeInterfaceDescriptor;
import org.jlato.bootstrap.util.DeclContribution;
import org.jlato.bootstrap.util.ImportManager;
import org.jlato.bootstrap.util.TypePattern;
import org.jlato.tree.*;
import org.jlato.tree.decl.*;
import org.jlato.tree.type.*;

import java.util.Arrays;

/**
 * @author Didier Villevalois
 */
public class TreeInterface extends TypePattern.OfInterface<TreeInterfaceDescriptor> {

	@Override
	protected String makeQuote(TreeInterfaceDescriptor arg) {
		return "public interface " + arg.name + " extends ..$_ { ..$_ }";
	}

	@Override
	protected String makeDoc(InterfaceDecl decl, TreeInterfaceDescriptor arg) {
		return "/** " + Utils.upperCaseFirst(arg.prefixedDescription()) + ". */";
	}

	@Override
	protected InterfaceDecl contributeSignature(InterfaceDecl decl, ImportManager importManager, TreeInterfaceDescriptor arg) {
		NodeList<QualifiedType> parentInterfaces = arg.superInterfaces;
		return decl.withExtendsClause(parentInterfaces);
	}

	@Override
	protected Iterable<DeclContribution<TreeInterfaceDescriptor, MemberDecl>> contributions(TreeInterfaceDescriptor arg) {
		return Arrays.asList(
				new TreeInterfaceAccessors()//,
//				a -> Arrays.asList(new StateInterface()),
//				DeclContribution.mergeFields(a -> a.shapes.map(m -> (FieldDecl) m))
		);
	}
}
