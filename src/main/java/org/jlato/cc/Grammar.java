package org.jlato.cc;

import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GExpansion;
import org.jlato.tree.decl.MethodDecl;

import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.listOf;

/**
 * @author Didier Villevalois
 */
public class Grammar {
	public GProduction[] productions = new GProduction[]{
			new GProduction("NodeListVar",
					(MethodDecl) memberDecl("BUTree<SNodeList> NodeListVar();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.terminal(null, "<NODE_LIST_VARIABLE>"),
							GExpansion.action(
									listOf(
											stmt("return makeVar();").build()
									)
							)
					)
			),
			new GProduction("NodeVar",
					(MethodDecl) memberDecl("BUTree<SName> NodeVar();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.terminal(null, "<NODE_VARIABLE>"),
							GExpansion.action(
									listOf(
											stmt("return makeVar();").build()
									)
							)
					)
			),
			new GProduction("CompilationUnit",
					(MethodDecl) memberDecl("BUTree<SCompilationUnit> CompilationUnit();").build(),
					listOf(
							stmt("BUTree<SPackageDecl> packageDecl = null;").build(),
							stmt("BUTree<SNodeList> imports;").build(),
							stmt("BUTree<SNodeList> types;").build(),
							stmt("BUTree<SCompilationUnit> compilationUnit;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "PackageDecl")
									),
									GExpansion.nonTerminal("packageDecl", "PackageDecl")
							),
							GExpansion.nonTerminal("imports", "ImportDecls"),
							GExpansion.nonTerminal("types", "TypeDecls"),
							GExpansion.action(
									listOf(
											stmt("compilationUnit = dress(SCompilationUnit.make(packageDecl, imports, types));").build()
									)
							),
							GExpansion.nonTerminal(null, "Epilog"),
							GExpansion.action(
									listOf(
											stmt("return dressWithPrologAndEpilog(compilationUnit);").build()
									)
							)
					)
			),
			new GProduction("Epilog",
					(MethodDecl) memberDecl("void Epilog();").build(),
					emptyList(),
					GExpansion.choice(
							GExpansion.terminal(null, "<EOF>"),
							GExpansion.terminal(null, "\\u001a")
					)
			),
			new GProduction("PackageDecl",
					(MethodDecl) memberDecl("BUTree<SPackageDecl> PackageDecl();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SQualifiedName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.terminal(null, "package"),
							GExpansion.nonTerminal("name", "QualifiedName"),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SPackageDecl.make(annotations, name));").build()
									)
							)
					)
			),
			new GProduction("ImportDecls",
					(MethodDecl) memberDecl("BUTree<SNodeList> ImportDecls();").build(),
					listOf(
							stmt("BUTree<SNodeList> imports = emptyList();").build(),
							stmt("BUTree<SImportDecl> importDecl = null;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.nonTerminal("importDecl", "ImportDecl"),
									GExpansion.action(
											listOf(
													stmt("imports = append(imports, importDecl);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return imports;").build()
									)
							)
					)
			),
			new GProduction("ImportDecl",
					(MethodDecl) memberDecl("BUTree<SImportDecl> ImportDecl();").build(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("boolean isStatic = false;").build(),
							stmt("boolean isAsterisk = false;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "import"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "static"),
									GExpansion.action(
											listOf(
													stmt("isStatic = true;").build()
											)
									)
							),
							GExpansion.nonTerminal("name", "QualifiedName"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "."),
									GExpansion.terminal(null, "*"),
									GExpansion.action(
											listOf(
													stmt("isAsterisk = true;").build()
											)
									)
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SImportDecl.make(name, isStatic, isAsterisk));").build()
									)
							)
					)
			),
			new GProduction("TypeDecls",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeDecls();").build(),
					listOf(
							stmt("BUTree<SNodeList> types = emptyList();").build(),
							stmt("BUTree<? extends STypeDecl> typeDecl = null;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.nonTerminal("typeDecl", "TypeDecl"),
									GExpansion.action(
											listOf(
													stmt("types = append(types, typeDecl);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return types;").build()
									)
							)
					)
			),
			new GProduction("Modifiers",
					(MethodDecl) memberDecl("BUTree<SNodeList> Modifiers();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> ann;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "public"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "protected"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "private"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "abstract"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "default"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Default));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "static"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "final"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "transient"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "volatile"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "synchronized"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "native"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "strictfp"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal("ann", "Annotation"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, ann);").build()
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return modifiers;").build()
									)
							)
					)
			),
			new GProduction("ModifiersNoDefault",
					(MethodDecl) memberDecl("BUTree<SNodeList> ModifiersNoDefault();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> ann;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "public"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "protected"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "private"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "abstract"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "static"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "final"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "transient"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "volatile"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "synchronized"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "native"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "strictfp"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal("ann", "Annotation"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, ann);").build()
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return modifiers;").build()
									)
							)
					)
			),
			new GProduction("TypeDecl",
					(MethodDecl) memberDecl("BUTree<? extends STypeDecl> TypeDecl();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends STypeDecl> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, ";"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers"),
											GExpansion.choice(
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl"),
													GExpansion.nonTerminal("ret", "EnumDecl"),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ClassOrInterfaceDecl",
					(MethodDecl) memberDecl("BUTree<? extends STypeDecl> ClassOrInterfaceDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("TypeKind typeKind;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> typeParams = null;").build(),
							stmt("BUTree<SQualifiedType> superClassType = null;").build(),
							stmt("BUTree<SNodeList> extendsClause = null;").build(),
							stmt("BUTree<SNodeList> implementsClause = null;").build(),
							stmt("BUTree<SNodeList> members;").build(),
							stmt("ByRef<BUProblem> problem = new ByRef<BUProblem>(null);").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "class"),
											GExpansion.action(
													listOf(
															stmt("typeKind = TypeKind.Class;").build()
													)
											),
											GExpansion.nonTerminal("name", "Name"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeParams", "TypeParameters")
											),
											GExpansion.zeroOrOne(
													GExpansion.terminal(null, "extends"),
													GExpansion.nonTerminal("superClassType", "AnnotatedQualifiedType")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("implementsClause", "ImplementsList")
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "interface"),
											GExpansion.action(
													listOf(
															stmt("typeKind = TypeKind.Interface;").build()
													)
											),
											GExpansion.nonTerminal("name", "Name"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeParams", "TypeParameters")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("extendsClause", "ExtendsList")
											)
									)
							),
							GExpansion.nonTerminal("members", "ClassOrInterfaceBody"),
							GExpansion.action(
									listOf(
											stmt("if (typeKind == TypeKind.Interface)\n" +
													"\treturn dress(SInterfaceDecl.make(modifiers, name, ensureNotNull(typeParams), ensureNotNull(extendsClause), members)).withProblem(problem.value);\n" +
													"else {\n" +
													"\treturn dress(SClassDecl.make(modifiers, name, ensureNotNull(typeParams), optionOf(superClassType), ensureNotNull(implementsClause), members));\n" +
													"}").build()
									)
							)
					)
			),
			new GProduction("ExtendsList",
					(MethodDecl) memberDecl("BUTree<SNodeList> ExtendsList();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "extends"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, ","),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("ret", "NodeListVar")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ImplementsList",
					(MethodDecl) memberDecl("BUTree<SNodeList> ImplementsList(TypeKind typeKind, ByRef<BUProblem> problem);").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "implements"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, ","),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											),
											GExpansion.action(
													listOf(
															stmt("if (typeKind == TypeKind.Interface) problem.value = new BUProblem(Severity.ERROR, \"An interface cannot implement other interfaces\");\n" +
																	"").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("ret", "NodeListVar")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("EnumDecl",
					(MethodDecl) memberDecl("BUTree<? extends STypeDecl> EnumDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> implementsClause = emptyList();").build(),
							stmt("BUTree<SEnumConstantDecl> entry;").build(),
							stmt("BUTree<SNodeList> constants = emptyList();").build(),
							stmt("boolean trailingComma = false;").build(),
							stmt("BUTree<SNodeList> members = null;").build(),
							stmt("ByRef<BUProblem> problem = new ByRef<BUProblem>(null);").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "enum"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("implementsClause", "ImplementsList")
							),
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.nonTerminal("entry", "EnumConstantDecl"),
													GExpansion.action(
															listOf(
																	stmt("constants = append(constants, entry);").build()
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.lookAhead(2),
															GExpansion.terminal(null, ","),
															GExpansion.nonTerminal("entry", "EnumConstantDecl"),
															GExpansion.action(
																	listOf(
																			stmt("constants = append(constants, entry);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("constants", "NodeListVar")
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, ","),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, ";"),
									GExpansion.nonTerminal("members", "ClassOrInterfaceBodyDecls")
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return dress(SEnumDecl.make(modifiers, name, implementsClause, constants, trailingComma, ensureNotNull(members))).withProblem(problem.value);").build()
									)
							)
					)
			),
			new GProduction("EnumConstantDecl",
					(MethodDecl) memberDecl("BUTree<SEnumConstantDecl> EnumConstantDecl();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> args = null;").build(),
							stmt("BUTree<SNodeList> classBody = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("modifiers", "Modifiers"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("args", "Arguments")
							),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("classBody", "ClassOrInterfaceBody")
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SEnumConstantDecl.make(modifiers, name, optionOf(args), optionOf(classBody)));").build()
									)
							)
					)
			),
			new GProduction("AnnotationTypeDecl",
					(MethodDecl) memberDecl("BUTree<SAnnotationDecl> AnnotationTypeDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> members;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "@"),
							GExpansion.terminal(null, "interface"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.nonTerminal("members", "AnnotationTypeBody"),
							GExpansion.action(
									listOf(
											stmt("return dress(SAnnotationDecl.make(modifiers, name, members));").build()
									)
							)
					)
			),
			new GProduction("AnnotationTypeBody",
					(MethodDecl) memberDecl("BUTree<SNodeList> AnnotationTypeBody();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SMemberDecl> member;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.oneOrMore(
													GExpansion.nonTerminal("member", "AnnotationTypeBodyDecl"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("ret", "NodeListVar")
											)
									)
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AnnotationTypeBodyDecl",
					(MethodDecl) memberDecl("BUTree<? extends SMemberDecl> AnnotationTypeBodyDecl();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SMemberDecl> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, ";"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers"),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Type"),
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, "(")
															),
															GExpansion.nonTerminal("ret", "AnnotationTypeMemberDecl")
													),
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl"),
													GExpansion.nonTerminal("ret", "EnumDecl"),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl"),
													GExpansion.nonTerminal("ret", "FieldDecl")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AnnotationTypeMemberDecl",
					(MethodDecl) memberDecl("BUTree<SAnnotationMemberDecl> AnnotationTypeMemberDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> dims;").build(),
							stmt("BUTree<SNodeOption> defaultVal = none();").build(),
							stmt("BUTree<? extends SExpr> val = null;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("type", "Type"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.terminal(null, "("),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("dims", "ArrayDims"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "default"),
									GExpansion.nonTerminal("val", "MemberValue"),
									GExpansion.action(
											listOf(
													stmt("defaultVal = optionOf(val);").build()
											)
									)
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SAnnotationMemberDecl.make(modifiers, type, name, dims, defaultVal));").build()
									)
							)
					)
			),
			new GProduction("TypeParameters",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeParameters();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<STypeParameter> tp;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "<"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("tp", "TypeParameter"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, tp);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, ","),
													GExpansion.nonTerminal("tp", "TypeParameter"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, tp);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("ret", "NodeListVar")
									)
							),
							GExpansion.terminal(null, ">"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("TypeParameter",
					(MethodDecl) memberDecl("BUTree<STypeParameter> TypeParameter();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> typeBounds = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeBounds", "TypeBounds")
							),
							GExpansion.action(
									listOf(
											stmt("return dress(STypeParameter.make(annotations, name, ensureNotNull(typeBounds)));").build()
									)
							)
					)
			),
			new GProduction("TypeBounds",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeBounds();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "extends"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "&"),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("ret", "NodeListVar")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ClassOrInterfaceBody",
					(MethodDecl) memberDecl("BUTree<SNodeList> ClassOrInterfaceBody(TypeKind typeKind);").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SMemberDecl> member;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "{"),
							GExpansion.nonTerminal("ret", "ClassOrInterfaceBodyDecls"),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ClassOrInterfaceBodyDecls",
					(MethodDecl) memberDecl("BUTree<SNodeList> ClassOrInterfaceBodyDecls(TypeKind typeKind);").build(),
					listOf(
							stmt("BUTree<? extends SMemberDecl> member;").build(),
							stmt("BUTree<SNodeList> ret = emptyList();").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.oneOrMore(
													GExpansion.nonTerminal("member", "ClassOrInterfaceBodyDecl"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("ret", "NodeListVar")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ClassOrInterfaceBodyDecl",
					(MethodDecl) memberDecl("BUTree<? extends SMemberDecl> ClassOrInterfaceBodyDecl(TypeKind typeKind);").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SMemberDecl> ret;").build(),
							stmt("BUProblem problem = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, ";"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyMemberDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers"),
											GExpansion.action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default)) && typeKind != TypeKind.Interface) problem = new BUProblem(Severity.ERROR, \"Only interfaces can have default members\");\n" +
																	"").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.nonTerminal("ret", "InitializerDecl"),
															GExpansion.action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));\n" +
																					"").build()
																	)
															)
													),
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl"),
													GExpansion.nonTerminal("ret", "EnumDecl"),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl"),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.zeroOrOne(
																			GExpansion.nonTerminal(null, "TypeParameters")
																	),
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, "(")
															),
															GExpansion.nonTerminal("ret", "ConstructorDecl"),
															GExpansion.action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));\n" +
																					"").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Type"),
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.zeroOrMore(
																			GExpansion.terminal(null, "["),
																			GExpansion.terminal(null, "]")
																	),
																	GExpansion.choice(
																			GExpansion.terminal(null, ","),
																			GExpansion.terminal(null, "="),
																			GExpansion.terminal(null, ";")
																	)
															),
															GExpansion.nonTerminal("ret", "FieldDecl")
													),
													GExpansion.nonTerminal("ret", "MethodDecl")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret.withProblem(problem);").build()
									)
							)
					)
			),
			new GProduction("FieldDecl",
					(MethodDecl) memberDecl("BUTree<SFieldDecl> FieldDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> variables = emptyList();").build(),
							stmt("BUTree<SVariableDeclarator> val;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("type", "Type"),
							GExpansion.nonTerminal("variables", "VariableDeclarators"),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SFieldDecl.make(modifiers, type, variables));").build()
									)
							)
					)
			),
			new GProduction("VariableDecl",
					(MethodDecl) memberDecl("BUTree<SLocalVariableDecl> VariableDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> variables = emptyList();").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("type", "Type"),
							GExpansion.nonTerminal("variables", "VariableDeclarators"),
							GExpansion.action(
									listOf(
											stmt("return dress(SLocalVariableDecl.make(modifiers, type, variables));").build()
									)
							)
					)
			),
			new GProduction("VariableDeclarators",
					(MethodDecl) memberDecl("BUTree<SNodeList> VariableDeclarators();").build(),
					listOf(
							stmt("BUTree<SNodeList> variables = emptyList();").build(),
							stmt("BUTree<SVariableDeclarator> val;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("val", "VariableDeclarator"),
							GExpansion.action(
									listOf(
											stmt("variables = append(variables, val);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, ","),
									GExpansion.nonTerminal("val", "VariableDeclarator"),
									GExpansion.action(
											listOf(
													stmt("variables = append(variables, val);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return variables;").build()
									)
							)
					)
			),
			new GProduction("VariableDeclarator",
					(MethodDecl) memberDecl("BUTree<SVariableDeclarator> VariableDeclarator();").build(),
					listOf(
							stmt("BUTree<SVariableDeclaratorId> id;").build(),
							stmt("BUTree<SNodeOption> init = none();").build(),
							stmt("BUTree<? extends SExpr> initExpr = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("id", "VariableDeclaratorId"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "="),
									GExpansion.nonTerminal("initExpr", "VariableInitializer"),
									GExpansion.action(
											listOf(
													stmt("init = optionOf(initExpr);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SVariableDeclarator.make(id, init));").build()
									)
							)
					)
			),
			new GProduction("VariableDeclaratorId",
					(MethodDecl) memberDecl("BUTree<SVariableDeclaratorId> VariableDeclaratorId();").build(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.nonTerminal("arrayDims", "ArrayDims"),
							GExpansion.action(
									listOf(
											stmt("return dress(SVariableDeclaratorId.make(name, arrayDims));").build()
									)
							)
					)
			),
			new GProduction("ArrayDims",
					(MethodDecl) memberDecl("BUTree<SNodeList> ArrayDims();").build(),
					listOf(
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "Annotations"),
											GExpansion.terminal(null, "["),
											GExpansion.terminal(null, "]")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations"),
									GExpansion.terminal(null, "["),
									GExpansion.terminal(null, "]"),
									GExpansion.action(
											listOf(
													stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return arrayDims;").build()
									)
							)
					)
			),
			new GProduction("VariableInitializer",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> VariableInitializer();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "ArrayInitializer"),
									GExpansion.nonTerminal("ret", "Expression")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ArrayInitializer",
					(MethodDecl) memberDecl("BUTree<SArrayInitializerExpr> ArrayInitializer();").build(),
					listOf(
							stmt("BUTree<SNodeList> values = emptyList();").build(),
							stmt("BUTree<? extends SExpr> val;").build(),
							stmt("boolean trailingComma = false;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("val", "VariableInitializer"),
									GExpansion.action(
											listOf(
													stmt("values = append(values, val);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.lookAhead(2),
											GExpansion.terminal(null, ","),
											GExpansion.nonTerminal("val", "VariableInitializer"),
											GExpansion.action(
													listOf(
															stmt("values = append(values, val);").build()
													)
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, ","),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return dress(SArrayInitializerExpr.make(values, trailingComma));").build()
									)
							)
					)
			),
			new GProduction("MethodDecl",
					(MethodDecl) memberDecl("BUTree<SMethodDecl> MethodDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<SNodeList> typeParameters = null;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> parameters;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build(),
							stmt("BUTree<SNodeList> throwsClause = null;").build(),
							stmt("BUTree<SBlockStmt> block = null;").build(),
							stmt("BUProblem problem = null;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeParameters", "TypeParameters")
							),
							GExpansion.nonTerminal("type", "ResultType"),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.nonTerminal("parameters", "FormalParameters"),
							GExpansion.nonTerminal("arrayDims", "ArrayDims"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("throwsClause", "ThrowsClause")
							),
							GExpansion.choice(
									GExpansion.nonTerminal("block", "Block"),
									GExpansion.sequence(
											GExpansion.terminal(null, ";"),
											GExpansion.action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default))) problem = new BUProblem(Severity.ERROR, \"Default methods must have a body\");\n" +
																	"").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SMethodDecl.make(modifiers, ensureNotNull(typeParameters), type, name, parameters, arrayDims, ensureNotNull(throwsClause), optionOf(block))).withProblem(problem);").build()
									)
							)
					)
			),
			new GProduction("FormalParameters",
					(MethodDecl) memberDecl("BUTree<SNodeList> FormalParameters();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<SFormalParameter> par;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "("),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "FormalParameterList")
							),
							GExpansion.terminal(null, ")"),
							GExpansion.action(
									listOf(
											stmt("return ensureNotNull(ret);").build()
									)
							)
					)
			),
			new GProduction("FormalParameterList",
					(MethodDecl) memberDecl("BUTree<SNodeList> FormalParameterList();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<SFormalParameter> par;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("par", "FormalParameter"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, par);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, ","),
													GExpansion.nonTerminal("par", "FormalParameter"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, par);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("ret", "NodeListVar")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("FormalParameter",
					(MethodDecl) memberDecl("BUTree<SFormalParameter> FormalParameter();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("boolean isVarArg = false;").build(),
							stmt("BUTree<SVariableDeclaratorId> id;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("modifiers", "Modifiers"),
							GExpansion.nonTerminal("type", "Type"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "..."),
									GExpansion.action(
											listOf(
													stmt("isVarArg = true;").build()
											)
									)
							),
							GExpansion.nonTerminal("id", "VariableDeclaratorId"),
							GExpansion.action(
									listOf(
											stmt("return dress(SFormalParameter.make(modifiers, type, isVarArg, id));").build()
									)
							)
					)
			),
			new GProduction("ThrowsClause",
					(MethodDecl) memberDecl("BUTree<SNodeList> ThrowsClause();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "throws"),
							GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, cit);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, ","),
									GExpansion.nonTerminal("cit", "AnnotatedQualifiedType"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, cit);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ConstructorDecl",
					(MethodDecl) memberDecl("BUTree<SConstructorDecl> ConstructorDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<SNodeList> typeParameters = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> parameters;").build(),
							stmt("BUTree<SNodeList> throwsClause = null;").build(),
							stmt("BUTree<SExplicitConstructorInvocationStmt> exConsInv = null;").build(),
							stmt("BUTree<SBlockStmt> block;").build(),
							stmt("BUTree<SNodeList> stmts = emptyList();").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeParameters", "TypeParameters")
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.nonTerminal("parameters", "FormalParameters"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("throwsClause", "ThrowsClause")
							),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.lookAhead(2),
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.lookAhead(
																			GExpansion.nonTerminal(null, "ExplicitConstructorInvocation")
																	),
																	GExpansion.nonTerminal("stmt", "ExplicitConstructorInvocation"),
																	GExpansion.action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															),
															GExpansion.sequence(
																	GExpansion.lookAhead(2),
																	GExpansion.nonTerminal("stmt", "BlockStatement"),
																	GExpansion.action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.lookAhead(2),
															GExpansion.nonTerminal("stmt", "BlockStatement"),
															GExpansion.action(
																	listOf(
																			stmt("stmts = append(stmts, stmt);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("stmts", "NodeListVar")
											)
									)
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("block = dress(SBlockStmt.make(stmts));").build()
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SConstructorDecl.make(modifiers, ensureNotNull(typeParameters), name, parameters, ensureNotNull(throwsClause), block));").build()
									)
							)
					)
			),
			new GProduction("ExplicitConstructorInvocation",
					(MethodDecl) memberDecl("BUTree<SExplicitConstructorInvocationStmt> ExplicitConstructorInvocation();").build(),
					listOf(
							stmt("boolean isThis = false;").build(),
							stmt("BUTree<SNodeList> args;").build(),
							stmt("BUTree<? extends SExpr> expr = null;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.zeroOrOne(
															GExpansion.nonTerminal(null, "TypeArguments")
													),
													GExpansion.terminal(null, "this"),
													GExpansion.terminal(null, "(")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeArgs", "TypeArguments")
											),
											GExpansion.terminal(null, "this"),
											GExpansion.action(
													listOf(
															stmt("isThis = true;").build()
													)
											),
											GExpansion.nonTerminal("args", "Arguments"),
											GExpansion.terminal(null, ";")
									),
									GExpansion.sequence(
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "PrimaryExpressionWithoutSuperSuffix"),
															GExpansion.terminal(null, ".")
													),
													GExpansion.nonTerminal("expr", "PrimaryExpressionWithoutSuperSuffix"),
													GExpansion.terminal(null, ".")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeArgs", "TypeArguments")
											),
											GExpansion.terminal(null, "super"),
											GExpansion.nonTerminal("args", "Arguments"),
											GExpansion.terminal(null, ";")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SExplicitConstructorInvocationStmt.make(ensureNotNull(typeArgs), isThis, optionOf(expr), args));").build()
									)
							)
					)
			),
			new GProduction("Statements",
					(MethodDecl) memberDecl("BUTree<SNodeList> Statements();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.choice(
											GExpansion.oneOrMore(
													GExpansion.nonTerminal("stmt", "BlockStatement"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, stmt);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("ret", "NodeListVar")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ensureNotNull(ret);").build()
									)
							)
					)
			),
			new GProduction("InitializerDecl",
					(MethodDecl) memberDecl("BUTree<SInitializerDecl> InitializerDecl(BUTree<SNodeList> modifiers);").build(),
					listOf(
							stmt("BUTree<SBlockStmt> block;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("block", "Block"),
							GExpansion.action(
									listOf(
											stmt("return dress(SInitializerDecl.make(modifiers, block));").build()
									)
							)
					)
			),
			new GProduction("Type",
					(MethodDecl) memberDecl("BUTree<? extends SType> Type(BUTree<SNodeList> annotations);").build(),
					listOf(
							stmt("BUTree<? extends SType> primitiveType = null;").build(),
							stmt("BUTree<? extends SReferenceType> type = null;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("primitiveType", "PrimitiveType"),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations"),
															GExpansion.terminal(null, "[")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType"),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations"),
															GExpansion.terminal(null, "[")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return type == null ? primitiveType : type;").build()
									)
							)
					)
			),
			new GProduction("ReferenceType",
					(MethodDecl) memberDecl("BUTree<? extends SReferenceType> ReferenceType(BUTree<SNodeList> annotations);").build(),
					listOf(
							stmt("BUTree<? extends SType> primitiveType;").build(),
							stmt("BUTree<? extends SReferenceType> type;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("primitiveType", "PrimitiveType"),
											GExpansion.action(
													listOf(
															stmt("lateRun();").build()
													)
											),
											GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
											GExpansion.action(
													listOf(
															stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType"),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations"),
															GExpansion.terminal(null, "[")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return type;").build()
									)
							)
					)
			),
			new GProduction("QualifiedType",
					(MethodDecl) memberDecl("BUTree<SQualifiedType> QualifiedType(BUTree<SNodeList> annotations);").build(),
					listOf(
							stmt("BUTree<SNodeOption> scope = none();").build(),
							stmt("BUTree<SQualifiedType> ret;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("if (annotations == null) {\n" +
													"\trun();\n" +
													"\tannotations = emptyList();\n" +
													"}").build()
									)
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
							),
							GExpansion.action(
									listOf(
											stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, "."),
									GExpansion.action(
											listOf(
													stmt("scope = optionOf(ret);").build()
											)
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations"),
									GExpansion.nonTerminal("name", "Name"),
									GExpansion.zeroOrOne(
											GExpansion.lookAhead(2),
											GExpansion.nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
									),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("TypeArguments",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeArguments();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "<"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "TypeArgumentList")
							),
							GExpansion.terminal(null, ">"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("TypeArgumentsOrDiamond",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeArgumentsOrDiamond();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "<"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "TypeArgumentList")
							),
							GExpansion.terminal(null, ">"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("TypeArgumentList",
					(MethodDecl) memberDecl("BUTree<SNodeList> TypeArgumentList();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					GExpansion.choice(
							GExpansion.sequence(
									GExpansion.nonTerminal("type", "TypeArgument"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, type);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.terminal(null, ","),
											GExpansion.nonTerminal("type", "TypeArgument"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, type);").build()
													)
											)
									),
									GExpansion.action(
											listOf(
													stmt("return ret;").build()
											)
									)
							),
							GExpansion.sequence(
									GExpansion.lookAhead(0),
									GExpansion.terminal(null, "<NODE_LIST_VARIABLE>"),
									GExpansion.action(
											listOf(
													stmt("return makeVar();").build()
											)
									)
							)
					)
			),
			new GProduction("TypeArgument",
					(MethodDecl) memberDecl("BUTree<? extends SType> TypeArgument();").build(),
					listOf(
							stmt("BUTree<? extends SType> ret;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "ReferenceType"),
									GExpansion.nonTerminal("ret", "Wildcard")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("Wildcard",
					(MethodDecl) memberDecl("BUTree<SWildcardType> Wildcard(BUTree<SNodeList> annotations);").build(),
					listOf(
							stmt("BUTree<? extends SReferenceType> ext = null;").build(),
							stmt("BUTree<? extends SReferenceType> sup = null;").build(),
							stmt("BUTree<SNodeList> boundAnnotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("if (annotations == null) {\n" +
													"\trun();\n" +
													"\tannotations = emptyList();\n" +
													"}").build()
									)
							),
							GExpansion.terminal(null, "?"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "extends"),
													GExpansion.action(
															listOf(
																	stmt("run();").build()
															)
													),
													GExpansion.nonTerminal("boundAnnotations", "Annotations"),
													GExpansion.nonTerminal("ext", "ReferenceType")
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "super"),
													GExpansion.action(
															listOf(
																	stmt("run();").build()
															)
													),
													GExpansion.nonTerminal("boundAnnotations", "Annotations"),
													GExpansion.nonTerminal("sup", "ReferenceType")
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SWildcardType.make(annotations, optionOf(ext), optionOf(sup)));").build()
									)
							)
					)
			),
			new GProduction("PrimitiveType",
					(MethodDecl) memberDecl("BUTree<SPrimitiveType> PrimitiveType(BUTree<SNodeList> annotations);").build(),
					listOf(
							stmt("Primitive primitive;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("if (annotations == null) {\n" +
													"\trun();\n" +
													"\tannotations = emptyList();\n" +
													"}").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "boolean"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Boolean;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "char"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Char;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "byte"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Byte;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "short"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Short;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "int"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Int;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "long"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Long;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "float"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Float;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "double"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Double;").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SPrimitiveType.make(annotations, primitive));").build()
									)
							)
					)
			),
			new GProduction("ResultType",
					(MethodDecl) memberDecl("BUTree<? extends SType> ResultType();").build(),
					listOf(
							stmt("BUTree<? extends SType> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "void"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SVoidType.make());").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "Type")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AnnotatedQualifiedType",
					(MethodDecl) memberDecl("BUTree<SQualifiedType> AnnotatedQualifiedType();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<SQualifiedType> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.nonTerminal("ret", "QualifiedType"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("QualifiedName",
					(MethodDecl) memberDecl("BUTree<SQualifiedName> QualifiedName();").build(),
					listOf(
							stmt("BUTree<SNodeOption> qualifier = none();").build(),
							stmt("BUTree<SQualifiedName> ret = null;").build(),
							stmt("BUTree<SName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.action(
									listOf(
											stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "."),
									GExpansion.action(
											listOf(
													stmt("qualifier = optionOf(ret);").build()
											)
									),
									GExpansion.nonTerminal("name", "Name"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("Name",
					(MethodDecl) memberDecl("BUTree<SName> Name();").build(),
					listOf(
							stmt("BUTree<SName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "<IDENTIFIER>"),
											GExpansion.action(
													listOf(
															stmt("name = dress(SName.make(token.image));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(0),
											GExpansion.nonTerminal("name", "NodeVar")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return name;").build()
									)
							)
					)
			),
			new GProduction("Expression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> Expression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("AssignOp op;").build(),
							stmt("BUTree<? extends SExpr> value;").build(),
							stmt("BUTree<SNodeList> params;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "Name"),
													GExpansion.terminal(null, "->")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("ret", "Name"),
											GExpansion.terminal(null, "->"),
											GExpansion.nonTerminal("ret", "LambdaBody")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "("),
													GExpansion.terminal(null, ")"),
													GExpansion.terminal(null, "->")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "("),
											GExpansion.terminal(null, ")"),
											GExpansion.terminal(null, "->"),
											GExpansion.nonTerminal("ret", "LambdaBody")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "("),
													GExpansion.nonTerminal(null, "Name"),
													GExpansion.terminal(null, ")"),
													GExpansion.terminal(null, "->")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "("),
											GExpansion.nonTerminal("ret", "Name"),
											GExpansion.terminal(null, ")"),
											GExpansion.terminal(null, "->"),
											GExpansion.nonTerminal("ret", "LambdaBody")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "("),
													GExpansion.nonTerminal(null, "Name"),
													GExpansion.terminal(null, ",")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "("),
											GExpansion.nonTerminal("params", "InferredFormalParameterList"),
											GExpansion.terminal(null, ")"),
											GExpansion.terminal(null, "->"),
											GExpansion.nonTerminal("ret", "LambdaBody")
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("ret", "ConditionalExpression"),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(2),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("op", "AssignmentOperator"),
													GExpansion.nonTerminal("value", "Expression"),
													GExpansion.action(
															listOf(
																	stmt("ret = dress(SAssignExpr.make(ret, op, value));").build()
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("LambdaBody",
					(MethodDecl) memberDecl("BUTree<SLambdaExpr> LambdaBody(BUTree<SNodeList> parameters, boolean parenthesis);").build(),
					listOf(
							stmt("BUTree<SBlockStmt> block;").build(),
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SLambdaExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("expr", "Expression"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, left(expr)));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("block", "Block"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, right(block)));").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("InferredFormalParameterList",
					(MethodDecl) memberDecl("BUTree<SNodeList> InferredFormalParameterList();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SFormalParameter> param;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("param", "InferredFormalParameter"),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, param);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, ","),
									GExpansion.nonTerminal("param", "InferredFormalParameter"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, param);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("InferredFormalParameter",
					(MethodDecl) memberDecl("BUTree<SFormalParameter> InferredFormalParameter();").build(),
					listOf(
							stmt("BUTree<SName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.action(
									listOf(
											stmt("return makeFormalParameter(name);").build()
									)
							)
					)
			),
			new GProduction("AssignmentOperator",
					(MethodDecl) memberDecl("AssignOp AssignmentOperator();").build(),
					listOf(
							stmt("AssignOp ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Normal;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "*="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Times;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "/="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Divide;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "%="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Remainder;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "+="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Plus;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "-="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Minus;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<<="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.LeftShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, ">>="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.RightSignedShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, ">>>="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.RightUnsignedShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "&="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.And;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "^="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.XOr;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "|="),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Or;").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ConditionalExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ConditionalExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> left;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "ConditionalOrExpression"),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "?"),
									GExpansion.nonTerminal("left", "Expression"),
									GExpansion.terminal(null, ":"),
									GExpansion.nonTerminal("right", "ConditionalExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SConditionalExpr.make(ret, left, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ConditionalOrExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ConditionalOrExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "ConditionalAndExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "||"),
									GExpansion.nonTerminal("right", "ConditionalAndExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.Or, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ConditionalAndExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ConditionalAndExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "InclusiveOrExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "&&"),
									GExpansion.nonTerminal("right", "InclusiveOrExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.And, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("InclusiveOrExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> InclusiveOrExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "ExclusiveOrExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "|"),
									GExpansion.nonTerminal("right", "ExclusiveOrExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinOr, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ExclusiveOrExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ExclusiveOrExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "AndExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "^"),
									GExpansion.nonTerminal("right", "AndExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.XOr, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AndExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> AndExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "EqualityExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "&"),
									GExpansion.nonTerminal("right", "EqualityExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinAnd, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("EqualityExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> EqualityExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "InstanceOfExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "=="),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Equal;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "!="),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.NotEqual;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "InstanceOfExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("InstanceOfExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> InstanceOfExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "RelationalExpression"),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "instanceof"),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations"),
									GExpansion.nonTerminal("type", "Type"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SInstanceOfExpr.make(ret, type));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("RelationalExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> RelationalExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "ShiftExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "<"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Less;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, ">"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Greater;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "<="),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.LessOrEqual;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, ">="),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.GreaterOrEqual;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "ShiftExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ShiftExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ShiftExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "AdditiveExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "<<"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.LeftShift;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal(null, "RSIGNEDSHIFT"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.RightSignedShift;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal(null, "RUNSIGNEDSHIFT"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.RightUnsignedShift;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "AdditiveExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AdditiveExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> AdditiveExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "MultiplicativeExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "+"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Plus;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "-"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Minus;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "MultiplicativeExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("MultiplicativeExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> MultiplicativeExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "UnaryExpression"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "*"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Times;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "/"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Divide;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "%"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Remainder;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "UnaryExpression"),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("UnaryExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> UnaryExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "PrefixExpression"),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "+"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Positive;").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "-"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Negative;").build()
																	)
															)
													)
											),
											GExpansion.nonTerminal("ret", "UnaryExpression"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PrefixExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrefixExpression();").build(),
					listOf(
							stmt("UnaryOp op;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "++"),
											GExpansion.action(
													listOf(
															stmt("op = UnaryOp.PreIncrement;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "--"),
											GExpansion.action(
													listOf(
															stmt("op = UnaryOp.PreDecrement;").build()
													)
											)
									)
							),
							GExpansion.nonTerminal("ret", "UnaryExpression"),
							GExpansion.action(
									listOf(
											stmt("return dress(SUnaryExpr.make(op, ret));").build()
									)
							)
					)
			),
			new GProduction("UnaryExpressionNotPlusMinus",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> UnaryExpressionNotPlusMinus();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "~"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Inverse;").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "!"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Not;").build()
																	)
															)
													)
											),
											GExpansion.nonTerminal("ret", "UnaryExpression"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "CastExpression")
											),
											GExpansion.nonTerminal("ret", "CastExpression")
									),
									GExpansion.nonTerminal("ret", "PostfixExpression")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PostfixExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PostfixExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "PrimaryExpression"),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "++"),
													GExpansion.action(
															listOf(
																	stmt("op = UnaryOp.PostIncrement;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "--"),
													GExpansion.action(
															listOf(
																	stmt("op = UnaryOp.PostDecrement;").build()
															)
													)
											)
									),
									GExpansion.action(
											listOf(
													stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("CastExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> CastExpression();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<? extends SType> primitiveType;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "("),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("primitiveType", "PrimitiveType"),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, ")"),
															GExpansion.nonTerminal("ret", "UnaryExpression"),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SCastExpr.make(primitiveType, ret));").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
															GExpansion.action(
																	listOf(
																			stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
																	)
															),
															GExpansion.nonTerminal("type", "ReferenceCastTypeRest"),
															GExpansion.terminal(null, ")"),
															GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SCastExpr.make(type, ret));").build()
																	)
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType"),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations"),
															GExpansion.terminal(null, "[")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											),
											GExpansion.nonTerminal("type", "ReferenceCastTypeRest"),
											GExpansion.terminal(null, ")"),
											GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SCastExpr.make(type, ret));").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ReferenceCastTypeRest",
					(MethodDecl) memberDecl("BUTree<? extends SType> ReferenceCastTypeRest(BUTree<? extends SType> type);").build(),
					listOf(
							stmt("BUTree<SNodeList> types = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(
											GExpansion.terminal(null, "&")
									),
									GExpansion.action(
											listOf(
													stmt("types = append(types, type);").build()
											)
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.oneOrMore(
											GExpansion.terminal(null, "&"),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("annotations", "Annotations"),
											GExpansion.nonTerminal("type", "ReferenceType"),
											GExpansion.action(
													listOf(
															stmt("types = append(types, type);").build()
													)
											)
									),
									GExpansion.action(
											listOf(
													stmt("type = dress(SIntersectionType.make(types));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return type;").build()
									)
							)
					)
			),
			new GProduction("Literal",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> Literal();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "<INTEGER_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Integer.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<LONG_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Long.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<FLOAT_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Float.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<DOUBLE_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Double.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<CHARACTER_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Character.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "<STRING_LITERAL>"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(String.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "true"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "false"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, token.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "null"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Void.class, token.image);").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(ret);").build()
									)
							)
					)
			),
			new GProduction("PrimaryExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrimaryExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "PrimaryPrefix"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.nonTerminal("ret", "PrimarySuffix")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PrimaryExpressionWithoutSuperSuffix",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrimaryExpressionWithoutSuperSuffix();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "PrimaryPrefix"),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "PrimarySuffixWithoutSuper")
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.nonTerminal("ret", "PrimarySuffixWithoutSuper")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PrimaryPrefix",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrimaryPrefix();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret = null;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SNodeList> params;").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "Literal"),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "this"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SThisExpr.make(none()));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "super"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SSuperExpr.make(none()));").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															GExpansion.terminal(null, "."),
															GExpansion.choice(
																	GExpansion.sequence(
																			GExpansion.lookAhead(
																					GExpansion.zeroOrOne(
																							GExpansion.nonTerminal(null, "TypeArguments")
																					),
																					GExpansion.nonTerminal(null, "Name"),
																					GExpansion.terminal(null, "(")
																			),
																			GExpansion.nonTerminal("ret", "MethodInvocation")
																	),
																	GExpansion.nonTerminal("ret", "FieldAccess")
															)
													),
													GExpansion.sequence(
															GExpansion.action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															GExpansion.nonTerminal("ret", "MethodReferenceSuffix")
													)
											)
									),
									GExpansion.nonTerminal("ret", "AllocationExpression"),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "ResultType"),
													GExpansion.terminal(null, "."),
													GExpansion.terminal(null, "class")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("type", "ResultType"),
											GExpansion.terminal(null, "."),
											GExpansion.terminal(null, "class"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SClassExpr.make(type));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "ResultType"),
													GExpansion.terminal(null, "::")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("type", "ResultType"),
											GExpansion.action(
													listOf(
															stmt("ret = STypeExpr.make(type);").build()
													)
											),
											GExpansion.nonTerminal("ret", "MethodReferenceSuffix")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.zeroOrOne(
															GExpansion.nonTerminal(null, "TypeArguments")
													),
													GExpansion.nonTerminal(null, "Name"),
													GExpansion.terminal(null, "(")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("ret", "MethodInvocation")
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("ret", "Name"),
											GExpansion.zeroOrOne(
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.terminal(null, "->"),
													GExpansion.nonTerminal("ret", "LambdaBody")
											)
									),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "("),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, ")"),
															GExpansion.terminal(null, "->"),
															GExpansion.nonTerminal("ret", "LambdaBody")
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, ")"),
																	GExpansion.terminal(null, "->")
															),
															GExpansion.nonTerminal("ret", "Name"),
															GExpansion.terminal(null, ")"),
															GExpansion.terminal(null, "->"),
															GExpansion.nonTerminal("ret", "LambdaBody")
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, ",")
															),
															GExpansion.nonTerminal("params", "InferredFormalParameterList"),
															GExpansion.terminal(null, ")"),
															GExpansion.terminal(null, "->"),
															GExpansion.nonTerminal("ret", "LambdaBody")
													),
													GExpansion.sequence(
															GExpansion.lookAhead(0),
															GExpansion.nonTerminal("params", "FormalParameterList"),
															GExpansion.terminal(null, ")"),
															GExpansion.terminal(null, "->"),
															GExpansion.nonTerminal("ret", "LambdaBody")
													),
													GExpansion.sequence(
															GExpansion.nonTerminal("ret", "Expression"),
															GExpansion.terminal(null, ")"),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SParenthesizedExpr.make(ret));").build()
																	)
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PrimarySuffix",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrimarySuffix(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(2),
											GExpansion.nonTerminal("ret", "PrimarySuffixWithoutSuper")
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "."),
											GExpansion.terminal(null, "super"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SSuperExpr.make(optionOf(scope)));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "MethodReferenceSuffix")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("PrimarySuffixWithoutSuper",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> PrimarySuffixWithoutSuper(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<SName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "."),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "this"),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SThisExpr.make(optionOf(scope)));").build()
																	)
															)
													),
													GExpansion.nonTerminal("ret", "AllocationExpression"),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.zeroOrOne(
																			GExpansion.nonTerminal(null, "TypeArguments")
																	),
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, "(")
															),
															GExpansion.nonTerminal("ret", "MethodInvocation")
													),
													GExpansion.nonTerminal("ret", "FieldAccess")
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "["),
											GExpansion.nonTerminal("ret", "Expression"),
											GExpansion.terminal(null, "]"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SArrayAccessExpr.make(scope, ret));").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("FieldAccess",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> FieldAccess(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<SName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.action(
									listOf(
											stmt("return dress(SFieldAccessExpr.make(optionOf(scope), name));").build()
									)
							)
					)
			),
			new GProduction("MethodInvocation",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> MethodInvocation(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> args = null;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeArgs", "TypeArguments")
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.nonTerminal("args", "Arguments"),
							GExpansion.action(
									listOf(
											stmt("return dress(SMethodInvocationExpr.make(optionOf(scope), ensureNotNull(typeArgs), name, args));").build()
									)
							)
					)
			),
			new GProduction("Arguments",
					(MethodDecl) memberDecl("BUTree<SNodeList> Arguments();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "("),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.lookAhead(1),
													GExpansion.nonTerminal("expr", "Expression"),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, expr);").build()
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.terminal(null, ","),
															GExpansion.nonTerminal("expr", "Expression"),
															GExpansion.action(
																	listOf(
																			stmt("ret = append(ret, expr);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(0),
													GExpansion.nonTerminal("ret", "NodeListVar")
											)
									)
							),
							GExpansion.terminal(null, ")"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("MethodReferenceSuffix",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> MethodReferenceSuffix(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "::"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeArgs", "TypeArguments")
							),
							GExpansion.choice(
									GExpansion.nonTerminal("name", "Name"),
									GExpansion.sequence(
											GExpansion.terminal(null, "new"),
											GExpansion.action(
													listOf(
															stmt("name = SName.make(\"new\");").build()
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("ret = dress(SMethodReferenceExpr.make(scope, ensureNotNull(typeArgs), name));").build()
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AllocationExpression",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> AllocationExpression(BUTree<? extends SExpr> scope);").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SNodeList> anonymousBody = null;").build(),
							stmt("BUTree<SNodeList> args;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("if (scope == null) run();\n" +
													"").build()
									)
							),
							GExpansion.terminal(null, "new"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeArgs", "TypeArguments")
							),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "PrimitiveType"),
											GExpansion.nonTerminal("ret", "ArrayCreationExpr")
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType"),
											GExpansion.choice(
													GExpansion.nonTerminal("ret", "ArrayCreationExpr"),
													GExpansion.sequence(
															GExpansion.nonTerminal("args", "Arguments"),
															GExpansion.zeroOrOne(
																	GExpansion.lookAhead(
																			GExpansion.terminal(null, "{")
																	),
																	GExpansion.nonTerminal("anonymousBody", "ClassOrInterfaceBody")
															),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SObjectCreationExpr.make(optionOf(scope), ensureNotNull(typeArgs), (BUTree<SQualifiedType>) type, args, optionOf(anonymousBody)));").build()
																	)
															)
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ArrayCreationExpr",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> ArrayCreationExpr(BUTree<? extends SType> componentType);").build(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SNodeList> arrayDimExprs = emptyList();").build(),
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SArrayInitializerExpr> initializer;").build()
					),
					GExpansion.choice(
							GExpansion.sequence(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "Annotations"),
											GExpansion.terminal(null, "["),
											GExpansion.nonTerminal(null, "Expression"),
											GExpansion.terminal(null, "]")
									),
									GExpansion.nonTerminal("arrayDimExprs", "ArrayDimExprsMandatory"),
									GExpansion.nonTerminal("arrayDims", "ArrayDims"),
									GExpansion.action(
											listOf(
													stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, none()));").build()
											)
									)
							),
							GExpansion.sequence(
									GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory"),
									GExpansion.nonTerminal("initializer", "ArrayInitializer"),
									GExpansion.action(
											listOf(
													stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, optionOf(initializer)));").build()
											)
									)
							)
					)
			),
			new GProduction("ArrayDimExprsMandatory",
					(MethodDecl) memberDecl("BUTree<SNodeList> ArrayDimExprsMandatory();").build(),
					listOf(
							stmt("BUTree<SNodeList> arrayDimExprs = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					GExpansion.sequence(
							GExpansion.oneOrMore(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "Annotations"),
											GExpansion.terminal(null, "["),
											GExpansion.nonTerminal(null, "Expression"),
											GExpansion.terminal(null, "]")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations"),
									GExpansion.terminal(null, "["),
									GExpansion.nonTerminal("expr", "Expression"),
									GExpansion.terminal(null, "]"),
									GExpansion.action(
											listOf(
													stmt("arrayDimExprs = append(arrayDimExprs, dress(SArrayDimExpr.make(annotations, expr)));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return arrayDimExprs;").build()
									)
							)
					)
			),
			new GProduction("ArrayDimsMandatory",
					(MethodDecl) memberDecl("BUTree<SNodeList> ArrayDimsMandatory();").build(),
					listOf(
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build()
					),
					GExpansion.sequence(
							GExpansion.oneOrMore(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "Annotations"),
											GExpansion.terminal(null, "["),
											GExpansion.terminal(null, "]")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations"),
									GExpansion.terminal(null, "["),
									GExpansion.terminal(null, "]"),
									GExpansion.action(
											listOf(
													stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return arrayDims;").build()
									)
							)
					)
			),
			new GProduction("Statement",
					(MethodDecl) memberDecl("BUTree<? extends SStmt> Statement();").build(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(2),
											GExpansion.nonTerminal("ret", "LabeledStatement")
									),
									GExpansion.nonTerminal("ret", "AssertStatement"),
									GExpansion.nonTerminal("ret", "Block"),
									GExpansion.nonTerminal("ret", "EmptyStatement"),
									GExpansion.nonTerminal("ret", "StatementExpression"),
									GExpansion.nonTerminal("ret", "SwitchStatement"),
									GExpansion.nonTerminal("ret", "IfStatement"),
									GExpansion.nonTerminal("ret", "WhileStatement"),
									GExpansion.nonTerminal("ret", "DoStatement"),
									GExpansion.nonTerminal("ret", "ForStatement"),
									GExpansion.nonTerminal("ret", "BreakStatement"),
									GExpansion.nonTerminal("ret", "ContinueStatement"),
									GExpansion.nonTerminal("ret", "ReturnStatement"),
									GExpansion.nonTerminal("ret", "ThrowStatement"),
									GExpansion.nonTerminal("ret", "SynchronizedStatement"),
									GExpansion.nonTerminal("ret", "TryStatement")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("AssertStatement",
					(MethodDecl) memberDecl("BUTree<SAssertStmt> AssertStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> check;").build(),
							stmt("BUTree<? extends SExpr> msg = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "assert"),
							GExpansion.nonTerminal("check", "Expression"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, ":"),
									GExpansion.nonTerminal("msg", "Expression")
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SAssertStmt.make(check, optionOf(msg)));").build()
									)
							)
					)
			),
			new GProduction("LabeledStatement",
					(MethodDecl) memberDecl("BUTree<SLabeledStmt> LabeledStatement();").build(),
					listOf(
							stmt("BUTree<SName> label;").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("label", "Name"),
							GExpansion.terminal(null, ":"),
							GExpansion.nonTerminal("stmt", "Statement"),
							GExpansion.action(
									listOf(
											stmt("return dress(SLabeledStmt.make(label, stmt));").build()
									)
							)
					)
			),
			new GProduction("Block",
					(MethodDecl) memberDecl("BUTree<SBlockStmt> Block();").build(),
					listOf(
							stmt("BUTree<SNodeList> stmts;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "{"),
							GExpansion.nonTerminal("stmts", "Statements"),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return dress(SBlockStmt.make(ensureNotNull(stmts)));").build()
									)
							)
					)
			),
			new GProduction("BlockStatement",
					(MethodDecl) memberDecl("BUTree<? extends SStmt> BlockStatement();").build(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build(),
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<? extends STypeDecl> typeDecl;").build(),
							stmt("BUTree<SNodeList> modifiers;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "ModifiersNoDefault"),
													GExpansion.choice(
															GExpansion.terminal(null, "class"),
															GExpansion.terminal(null, "interface")
													)
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("modifiers", "ModifiersNoDefault"),
											GExpansion.nonTerminal("typeDecl", "ClassOrInterfaceDecl"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(STypeDeclarationStmt.make(typeDecl));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "VariableDeclExpression")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("expr", "VariableDeclExpression"),
											GExpansion.terminal(null, ";"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SExpressionStmt.make(expr));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "Statement")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("VariableDeclExpression",
					(MethodDecl) memberDecl("BUTree<SVariableDeclarationExpr> VariableDeclExpression();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<SLocalVariableDecl> variableDecl;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("modifiers", "ModifiersNoDefault"),
							GExpansion.nonTerminal("variableDecl", "VariableDecl"),
							GExpansion.action(
									listOf(
											stmt("return dress(SVariableDeclarationExpr.make(variableDecl));").build()
									)
							)
					)
			),
			new GProduction("EmptyStatement",
					(MethodDecl) memberDecl("BUTree<SEmptyStmt> EmptyStatement();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SEmptyStmt.make());").build()
									)
							)
					)
			),
			new GProduction("StatementExpression",
					(MethodDecl) memberDecl("BUTree<SExpressionStmt> StatementExpression();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("AssignOp op;").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.nonTerminal("expr", "PrefixExpression"),
									GExpansion.sequence(
											GExpansion.nonTerminal("expr", "PrimaryExpression"),
											GExpansion.zeroOrOne(
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	GExpansion.terminal(null, "++"),
																	GExpansion.action(
																			listOf(
																					stmt("expr = dress(SUnaryExpr.make(UnaryOp.PostIncrement, expr));").build()
																			)
																	)
															),
															GExpansion.sequence(
																	GExpansion.action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	GExpansion.terminal(null, "--"),
																	GExpansion.action(
																			listOf(
																					stmt("expr = dress(SUnaryExpr.make(UnaryOp.PostDecrement, expr));").build()
																			)
																	)
															),
															GExpansion.sequence(
																	GExpansion.action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	GExpansion.nonTerminal("op", "AssignmentOperator"),
																	GExpansion.nonTerminal("value", "Expression"),
																	GExpansion.action(
																			listOf(
																					stmt("expr = dress(SAssignExpr.make(expr, op, value));").build()
																			)
																	)
															)
													)
											)
									)
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SExpressionStmt.make(expr));").build()
									)
							)
					)
			),
			new GProduction("SwitchStatement",
					(MethodDecl) memberDecl("BUTree<SSwitchStmt> SwitchStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> selector;").build(),
							stmt("BUTree<SSwitchCase> entry;").build(),
							stmt("BUTree<SNodeList> entries = emptyList();").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "switch"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("selector", "Expression"),
							GExpansion.terminal(null, ")"),
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrMore(
									GExpansion.nonTerminal("entry", "SwitchEntry"),
									GExpansion.action(
											listOf(
													stmt("entries = append(entries, entry);").build()
											)
									)
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return dress(SSwitchStmt.make(selector, entries));").build()
									)
							)
					)
			),
			new GProduction("SwitchEntry",
					(MethodDecl) memberDecl("BUTree<SSwitchCase> SwitchEntry();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> label = null;").build(),
							stmt("BUTree<SNodeList> stmts;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "case"),
											GExpansion.nonTerminal("label", "Expression")
									),
									GExpansion.terminal(null, "default")
							),
							GExpansion.terminal(null, ":"),
							GExpansion.nonTerminal("stmts", "Statements"),
							GExpansion.action(
									listOf(
											stmt("return dress(SSwitchCase.make(optionOf(label), ensureNotNull(stmts)));").build()
									)
							)
					)
			),
			new GProduction("IfStatement",
					(MethodDecl) memberDecl("BUTree<SIfStmt> IfStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> thenStmt;").build(),
							stmt("BUTree<? extends SStmt> elseStmt = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "if"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("condition", "Expression"),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("thenStmt", "Statement"),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(1),
									GExpansion.terminal(null, "else"),
									GExpansion.nonTerminal("elseStmt", "Statement")
							),
							GExpansion.action(
									listOf(
											stmt("return dress(SIfStmt.make(condition, thenStmt, optionOf(elseStmt)));").build()
									)
							)
					)
			),
			new GProduction("WhileStatement",
					(MethodDecl) memberDecl("BUTree<SWhileStmt> WhileStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "while"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("condition", "Expression"),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("body", "Statement"),
							GExpansion.action(
									listOf(
											stmt("return dress(SWhileStmt.make(condition, body));").build()
									)
							)
					)
			),
			new GProduction("DoStatement",
					(MethodDecl) memberDecl("BUTree<SDoStmt> DoStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "do"),
							GExpansion.nonTerminal("body", "Statement"),
							GExpansion.terminal(null, "while"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("condition", "Expression"),
							GExpansion.terminal(null, ")"),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SDoStmt.make(body, condition));").build()
									)
							)
					)
			),
			new GProduction("ForStatement",
					(MethodDecl) memberDecl("BUTree<? extends SStmt> ForStatement();").build(),
					listOf(
							stmt("BUTree<SVariableDeclarationExpr> varExpr = null;").build(),
							stmt("BUTree<? extends SExpr> expr = null;").build(),
							stmt("BUTree<SNodeList> init = null;").build(),
							stmt("BUTree<SNodeList> update = null;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "for"),
							GExpansion.terminal(null, "("),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "VariableDeclExpression"),
													GExpansion.terminal(null, ":")
											),
											GExpansion.nonTerminal("varExpr", "VariableDeclExpression"),
											GExpansion.terminal(null, ":"),
											GExpansion.nonTerminal("expr", "Expression")
									),
									GExpansion.sequence(
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("init", "ForInit")
											),
											GExpansion.terminal(null, ";"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("expr", "Expression")
											),
											GExpansion.terminal(null, ";"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("update", "ForUpdate")
											)
									)
							),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("body", "Statement"),
							GExpansion.action(
									listOf(
											stmt("if (varExpr != null)\n" +
													"\treturn dress(SForeachStmt.make(varExpr, expr, body));\n" +
													"else\n" +
													"\treturn dress(SForStmt.make(init, expr, update, body));\n" +
													"").build()
									)
							)
					)
			),
			new GProduction("ForInit",
					(MethodDecl) memberDecl("BUTree<SNodeList> ForInit();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "Modifiers"),
													GExpansion.nonTerminal(null, "Type"),
													GExpansion.nonTerminal(null, "Name")
											),
											GExpansion.nonTerminal("expr", "VariableDeclExpression"),
											GExpansion.action(
													listOf(
															stmt("ret = emptyList();").build(),
															stmt("ret = append(ret, expr);").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "ExpressionList")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ExpressionList",
					(MethodDecl) memberDecl("BUTree<SNodeList> ExpressionList();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("expr", "Expression"),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, expr);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, ","),
									GExpansion.nonTerminal("expr", "Expression"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, expr);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("ForUpdate",
					(MethodDecl) memberDecl("BUTree<SNodeList> ForUpdate();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("ret", "ExpressionList"),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("BreakStatement",
					(MethodDecl) memberDecl("BUTree<SBreakStmt> BreakStatement();").build(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "break"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("id", "Name")
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SBreakStmt.make(optionOf(id)));").build()
									)
							)
					)
			),
			new GProduction("ContinueStatement",
					(MethodDecl) memberDecl("BUTree<SContinueStmt> ContinueStatement();").build(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "continue"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("id", "Name")
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SContinueStmt.make(optionOf(id)));").build()
									)
							)
					)
			),
			new GProduction("ReturnStatement",
					(MethodDecl) memberDecl("BUTree<SReturnStmt> ReturnStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> expr = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "return"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("expr", "Expression")
							),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SReturnStmt.make(optionOf(expr)));").build()
									)
							)
					)
			),
			new GProduction("ThrowStatement",
					(MethodDecl) memberDecl("BUTree<SThrowStmt> ThrowStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "throw"),
							GExpansion.nonTerminal("expr", "Expression"),
							GExpansion.terminal(null, ";"),
							GExpansion.action(
									listOf(
											stmt("return dress(SThrowStmt.make(expr));").build()
									)
							)
					)
			),
			new GProduction("SynchronizedStatement",
					(MethodDecl) memberDecl("BUTree<SSynchronizedStmt> SynchronizedStatement();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SBlockStmt> block;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "synchronized"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("expr", "Expression"),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("block", "Block"),
							GExpansion.action(
									listOf(
											stmt("return dress(SSynchronizedStmt.make(expr, block));").build()
									)
							)
					)
			),
			new GProduction("TryStatement",
					(MethodDecl) memberDecl("BUTree<STryStmt> TryStatement();").build(),
					listOf(
							stmt("BUTree<SNodeList> resources = null;").build(),
							stmt("ByRef<Boolean> trailingSemiColon = new ByRef<Boolean>(false);").build(),
							stmt("BUTree<SBlockStmt> tryBlock;").build(),
							stmt("BUTree<SBlockStmt> finallyBlock = null;").build(),
							stmt("BUTree<SNodeList> catchClauses = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "try"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("resources", "ResourceSpecification"),
											GExpansion.nonTerminal("tryBlock", "Block"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("catchClauses", "CatchClauses")
											),
											GExpansion.zeroOrOne(
													GExpansion.terminal(null, "finally"),
													GExpansion.nonTerminal("finallyBlock", "Block")
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("tryBlock", "Block"),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.nonTerminal("catchClauses", "CatchClauses"),
															GExpansion.zeroOrOne(
																	GExpansion.terminal(null, "finally"),
																	GExpansion.nonTerminal("finallyBlock", "Block")
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "finally"),
															GExpansion.nonTerminal("finallyBlock", "Block")
													)
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return dress(STryStmt.make(ensureNotNull(resources), trailingSemiColon.value, tryBlock, ensureNotNull(catchClauses), optionOf(finallyBlock)));").build()
									)
							)
					)
			),
			new GProduction("CatchClauses",
					(MethodDecl) memberDecl("BUTree<SNodeList> CatchClauses();").build(),
					listOf(
							stmt("BUTree<SNodeList> catchClauses = emptyList();").build(),
							stmt("BUTree<SCatchClause> catchClause;").build()
					),
					GExpansion.sequence(
							GExpansion.oneOrMore(
									GExpansion.nonTerminal("catchClause", "CatchClause"),
									GExpansion.action(
											listOf(
													stmt("catchClauses = append(catchClauses, catchClause);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return catchClauses;").build()
									)
							)
					)
			),
			new GProduction("CatchClause",
					(MethodDecl) memberDecl("BUTree<SCatchClause> CatchClause();").build(),
					listOf(
							stmt("BUTree<SFormalParameter> param;").build(),
							stmt("BUTree<SBlockStmt> catchBlock;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "catch"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("param", "CatchFormalParameter"),
							GExpansion.terminal(null, ")"),
							GExpansion.nonTerminal("catchBlock", "Block"),
							GExpansion.action(
									listOf(
											stmt("return dress(SCatchClause.make(param, catchBlock));").build()
									)
							)
					)
			),
			new GProduction("CatchFormalParameter",
					(MethodDecl) memberDecl("BUTree<SFormalParameter> CatchFormalParameter();").build(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SType> exceptType;").build(),
							stmt("BUTree<SNodeList> exceptTypes = emptyList();").build(),
							stmt("BUTree<SVariableDeclaratorId> exceptId;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("modifiers", "Modifiers"),
							GExpansion.nonTerminal("exceptType", "QualifiedType"),
							GExpansion.action(
									listOf(
											stmt("exceptTypes = append(exceptTypes, exceptType);").build()
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(
											GExpansion.terminal(null, "|")
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.oneOrMore(
											GExpansion.terminal(null, "|"),
											GExpansion.nonTerminal("exceptType", "AnnotatedQualifiedType"),
											GExpansion.action(
													listOf(
															stmt("exceptTypes = append(exceptTypes, exceptType);").build()
													)
											)
									),
									GExpansion.action(
											listOf(
													stmt("exceptType = dress(SUnionType.make(exceptTypes));").build()
											)
									)
							),
							GExpansion.nonTerminal("exceptId", "VariableDeclaratorId"),
							GExpansion.action(
									listOf(
											stmt("return dress(SFormalParameter.make(modifiers, exceptType, false, exceptId));").build()
									)
							)
					)
			),
			new GProduction("ResourceSpecification",
					(MethodDecl) memberDecl("BUTree<SNodeList> ResourceSpecification(ByRef<Boolean> trailingSemiColon);").build(),
					listOf(
							stmt("BUTree<SNodeList> vars = emptyList();").build(),
							stmt("BUTree<SVariableDeclarationExpr> var;").build()
					),
					GExpansion.sequence(
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("var", "VariableDeclExpression"),
							GExpansion.action(
									listOf(
											stmt("vars = append(vars, var);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, ";"),
									GExpansion.nonTerminal("var", "VariableDeclExpression"),
									GExpansion.action(
											listOf(
													stmt("vars = append(vars, var);").build()
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, ";"),
									GExpansion.action(
											listOf(
													stmt("trailingSemiColon.value = true;").build()
											)
									)
							),
							GExpansion.terminal(null, ")"),
							GExpansion.action(
									listOf(
											stmt("return vars;").build()
									)
							)
					)
			),
			new GProduction("RUNSIGNEDSHIFT",
					(MethodDecl) memberDecl("void RUNSIGNEDSHIFT();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.lookAhead(0),
							GExpansion.terminal(null, ">"),
							GExpansion.terminal(null, ">"),
							GExpansion.terminal(null, ">"),
							GExpansion.action(
									listOf(
											stmt("popNewWhitespaces();").build()
									)
							)
					)
			),
			new GProduction("RSIGNEDSHIFT",
					(MethodDecl) memberDecl("void RSIGNEDSHIFT();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.lookAhead(0),
							GExpansion.terminal(null, ">"),
							GExpansion.terminal(null, ">"),
							GExpansion.action(
									listOf(
											stmt("popNewWhitespaces();").build()
									)
							)
					)
			),
			new GProduction("Annotations",
					(MethodDecl) memberDecl("BUTree<SNodeList> Annotations();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> annotation;").build()
					),
					GExpansion.sequence(
							GExpansion.zeroOrMore(
									GExpansion.nonTerminal("annotation", "Annotation"),
									GExpansion.action(
											listOf(
													stmt("annotations = append(annotations, annotation);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return annotations;").build()
									)
							)
					)
			),
			new GProduction("Annotation",
					(MethodDecl) memberDecl("BUTree<? extends SAnnotationExpr> Annotation();").build(),
					listOf(
							stmt("BUTree<? extends SAnnotationExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "@"),
													GExpansion.nonTerminal(null, "QualifiedName"),
													GExpansion.terminal(null, "("),
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.nonTerminal(null, "Name"),
																	GExpansion.terminal(null, "=")
															),
															GExpansion.terminal(null, ")")
													)
											),
											GExpansion.nonTerminal("ret", "NormalAnnotation")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "@"),
													GExpansion.nonTerminal(null, "QualifiedName"),
													GExpansion.terminal(null, "(")
											),
											GExpansion.nonTerminal("ret", "SingleMemberAnnotation")
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "@"),
													GExpansion.nonTerminal(null, "QualifiedName")
											),
											GExpansion.nonTerminal("ret", "MarkerAnnotation")
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("NormalAnnotation",
					(MethodDecl) memberDecl("BUTree<SNormalAnnotationExpr> NormalAnnotation();").build(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("BUTree<SNodeList> pairs = null;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "@"),
							GExpansion.nonTerminal("name", "QualifiedName"),
							GExpansion.terminal(null, "("),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("pairs", "MemberValuePairs")
							),
							GExpansion.terminal(null, ")"),
							GExpansion.action(
									listOf(
											stmt("return dress(SNormalAnnotationExpr.make(name, ensureNotNull(pairs)));").build()
									)
							)
					)
			),
			new GProduction("MarkerAnnotation",
					(MethodDecl) memberDecl("BUTree<SMarkerAnnotationExpr> MarkerAnnotation();").build(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "@"),
							GExpansion.nonTerminal("name", "QualifiedName"),
							GExpansion.action(
									listOf(
											stmt("return dress(SMarkerAnnotationExpr.make(name));").build()
									)
							)
					)
			),
			new GProduction("SingleMemberAnnotation",
					(MethodDecl) memberDecl("BUTree<SSingleMemberAnnotationExpr> SingleMemberAnnotation();").build(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("BUTree<? extends SExpr> memberVal;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "@"),
							GExpansion.nonTerminal("name", "QualifiedName"),
							GExpansion.terminal(null, "("),
							GExpansion.nonTerminal("memberVal", "MemberValue"),
							GExpansion.terminal(null, ")"),
							GExpansion.action(
									listOf(
											stmt("return dress(SSingleMemberAnnotationExpr.make(name, memberVal));").build()
									)
							)
					)
			),
			new GProduction("MemberValuePairs",
					(MethodDecl) memberDecl("BUTree<SNodeList> MemberValuePairs();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SMemberValuePair> pair;").build()
					),
					GExpansion.sequence(
							GExpansion.nonTerminal("pair", "MemberValuePair"),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, pair);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, ","),
									GExpansion.nonTerminal("pair", "MemberValuePair"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, pair);").build()
											)
									)
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("MemberValuePair",
					(MethodDecl) memberDecl("BUTree<SMemberValuePair> MemberValuePair();").build(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("name", "Name"),
							GExpansion.terminal(null, "="),
							GExpansion.nonTerminal("value", "MemberValue"),
							GExpansion.action(
									listOf(
											stmt("return dress(SMemberValuePair.make(name, value));").build()
									)
							)
					)
			),
			new GProduction("MemberValue",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> MemberValue();").build(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					GExpansion.sequence(
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "Annotation"),
									GExpansion.nonTerminal("ret", "MemberValueArrayInitializer"),
									GExpansion.nonTerminal("ret", "ConditionalExpression")
							),
							GExpansion.action(
									listOf(
											stmt("return ret;").build()
									)
							)
					)
			),
			new GProduction("MemberValueArrayInitializer",
					(MethodDecl) memberDecl("BUTree<? extends SExpr> MemberValueArrayInitializer();").build(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> member;").build(),
							stmt("boolean trailingComma = false;").build()
					),
					GExpansion.sequence(
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "{"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("member", "MemberValue"),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, member);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.lookAhead(2),
											GExpansion.terminal(null, ","),
											GExpansion.nonTerminal("member", "MemberValue"),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, member);").build()
													)
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, ","),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "}"),
							GExpansion.action(
									listOf(
											stmt("return dress(SArrayInitializerExpr.make(ret, trailingComma));").build()
									)
							)
					)
			)
	};
}
