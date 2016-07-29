package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.tree.decl.MethodDecl;

import static org.jlato.rewrite.Quotes.*;
import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.listOf;

public class Grammar {

	public static GProductions productions = new GProductions(
			new GProduction("NodeListVar",
					(MethodDecl) memberDecl("BUTree<SNodeList> NodeListVar();").build(),
					emptyList(),
					GExpansion.sequence(
							GExpansion.terminal(null, "NODE_LIST_VARIABLE"),
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
							GExpansion.terminal(null, "NODE_VARIABLE"),
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
											GExpansion.nonTerminal(null, "PackageDecl", emptyList())
									),
									GExpansion.nonTerminal("packageDecl", "PackageDecl", emptyList())
							),
							GExpansion.nonTerminal("imports", "ImportDecls", emptyList()),
							GExpansion.nonTerminal("types", "TypeDecls", emptyList()),
							GExpansion.action(
									listOf(
											stmt("compilationUnit = dress(SCompilationUnit.make(packageDecl, imports, types));").build()
									)
							),
							GExpansion.nonTerminal(null, "Epilog", emptyList()),
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
							GExpansion.terminal(null, "EOF"),
							GExpansion.terminal(null, "EOF")
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
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.terminal(null, "PACKAGE"),
							GExpansion.nonTerminal("name", "QualifiedName", emptyList()),
							GExpansion.terminal(null, "SEMICOLON"),
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
									GExpansion.nonTerminal("importDecl", "ImportDecl", emptyList()),
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
							GExpansion.terminal(null, "IMPORT"),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "STATIC"),
									GExpansion.action(
											listOf(
													stmt("isStatic = true;").build()
											)
									)
							),
							GExpansion.nonTerminal("name", "QualifiedName", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "DOT"),
									GExpansion.terminal(null, "STAR"),
									GExpansion.action(
											listOf(
													stmt("isAsterisk = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
									GExpansion.nonTerminal("typeDecl", "TypeDecl", emptyList()),
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
													GExpansion.terminal(null, "PUBLIC"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "PROTECTED"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "PRIVATE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "ABSTRACT"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "_DEFAULT"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Default));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "STATIC"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "FINAL"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "TRANSIENT"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "VOLATILE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "SYNCHRONIZED"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "NATIVE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "STRICTFP"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal("ann", "Annotation", emptyList()),
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
													GExpansion.terminal(null, "PUBLIC"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "PROTECTED"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "PRIVATE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "ABSTRACT"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "STATIC"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "FINAL"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "TRANSIENT"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "VOLATILE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "SYNCHRONIZED"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "NATIVE"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "STRICTFP"),
													GExpansion.action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.nonTerminal("ann", "Annotation", emptyList()),
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
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
											GExpansion.choice(
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "EnumDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl", listOf(
															expr("modifiers").build()
													))
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
											GExpansion.terminal(null, "CLASS"),
											GExpansion.action(
													listOf(
															stmt("typeKind = TypeKind.Class;").build()
													)
											),
											GExpansion.nonTerminal("name", "Name", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeParams", "TypeParameters", emptyList())
											),
											GExpansion.zeroOrOne(
													GExpansion.terminal(null, "EXTENDS"),
													GExpansion.nonTerminal("superClassType", "AnnotatedQualifiedType", emptyList())
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("implementsClause", "ImplementsList", listOf(
															expr("typeKind").build(),
															expr("problem").build()
													))
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "INTERFACE"),
											GExpansion.action(
													listOf(
															stmt("typeKind = TypeKind.Interface;").build()
													)
											),
											GExpansion.nonTerminal("name", "Name", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeParams", "TypeParameters", emptyList())
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("extendsClause", "ExtendsList", emptyList())
											)
									)
							),
							GExpansion.nonTerminal("members", "ClassOrInterfaceBody", listOf(
									expr("typeKind").build()
							)),
							GExpansion.action(
									listOf(
											stmt("if (typeKind == TypeKind.Interface)\n" + "\treturn dress(SInterfaceDecl.make(modifiers, name, ensureNotNull(typeParams), ensureNotNull(extendsClause), members)).withProblem(problem.value);\n" + "else {\n" + "\treturn dress(SClassDecl.make(modifiers, name, ensureNotNull(typeParams), optionOf(superClassType), ensureNotNull(implementsClause), members));\n" + "}").build()
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
							GExpansion.terminal(null, "EXTENDS"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "COMMA"),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
							GExpansion.terminal(null, "IMPLEMENTS"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "COMMA"),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											),
											GExpansion.action(
													listOf(
															stmt("if (typeKind == TypeKind.Interface) problem.value = new BUProblem(Severity.ERROR, \"An interface cannot implement other interfaces\");\n" + "").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
							GExpansion.terminal(null, "ENUM"),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("implementsClause", "ImplementsList", listOf(
											expr("TypeKind.Enum").build(),
											expr("problem").build()
									))
							),
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.nonTerminal("entry", "EnumConstantDecl", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("constants = append(constants, entry);").build()
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.lookAhead(2),
															GExpansion.terminal(null, "COMMA"),
															GExpansion.nonTerminal("entry", "EnumConstantDecl", emptyList()),
															GExpansion.action(
																	listOf(
																			stmt("constants = append(constants, entry);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("constants", "NodeListVar", emptyList())
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "SEMICOLON"),
									GExpansion.nonTerminal("members", "ClassOrInterfaceBodyDecls", listOf(
											expr("TypeKind.Enum").build()
									))
							),
							GExpansion.terminal(null, "RBRACE"),
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
							GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("args", "Arguments", emptyList())
							),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("classBody", "ClassOrInterfaceBody", listOf(
											expr("TypeKind.Class").build()
									))
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
							GExpansion.terminal(null, "AT"),
							GExpansion.terminal(null, "INTERFACE"),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.nonTerminal("members", "AnnotationTypeBody", emptyList()),
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
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.oneOrMore(
													GExpansion.nonTerminal("member", "AnnotationTypeBodyDecl", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
											)
									)
							),
							GExpansion.terminal(null, "RBRACE"),
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
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Type", emptyList()),
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "LPAREN")
															),
															GExpansion.nonTerminal("ret", "AnnotationTypeMemberDecl", listOf(
																	expr("modifiers").build()
															))
													),
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "EnumDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "FieldDecl", listOf(
															expr("modifiers").build()
													))
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
							GExpansion.nonTerminal("type", "Type", listOf(
									expr("null").build()
							)),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("dims", "ArrayDims", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "_DEFAULT"),
									GExpansion.nonTerminal("val", "MemberValue", emptyList()),
									GExpansion.action(
											listOf(
													stmt("defaultVal = optionOf(val);").build()
											)
									)
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "LT"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("tp", "TypeParameter", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, tp);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "COMMA"),
													GExpansion.nonTerminal("tp", "TypeParameter", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, tp);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
									)
							),
							GExpansion.terminal(null, "GT"),
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
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeBounds", "TypeBounds", emptyList())
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
							GExpansion.terminal(null, "EXTENDS"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "BIT_AND"),
													GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.nonTerminal("ret", "ClassOrInterfaceBodyDecls", listOf(
									expr("typeKind").build()
							)),
							GExpansion.terminal(null, "RBRACE"),
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
													GExpansion.nonTerminal("member", "ClassOrInterfaceBodyDecl", listOf(
															expr("typeKind").build()
													)),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SEmptyMemberDecl.make());").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
											GExpansion.action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default)) && typeKind != TypeKind.Interface) problem = new BUProblem(Severity.ERROR, \"Only interfaces can have default members\");\n" + "").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.nonTerminal("ret", "InitializerDecl", listOf(
																	expr("modifiers").build()
															)),
															GExpansion.action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));\n" + "").build()
																	)
															)
													),
													GExpansion.nonTerminal("ret", "ClassOrInterfaceDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "EnumDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.nonTerminal("ret", "AnnotationTypeDecl", listOf(
															expr("modifiers").build()
													)),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.zeroOrOne(
																			GExpansion.nonTerminal(null, "TypeParameters", emptyList())
																	),
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "LPAREN")
															),
															GExpansion.nonTerminal("ret", "ConstructorDecl", listOf(
																	expr("modifiers").build()
															)),
															GExpansion.action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));\n" + "").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Type", emptyList()),
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.zeroOrMore(
																			GExpansion.terminal(null, "LBRACKET"),
																			GExpansion.terminal(null, "RBRACKET")
																	),
																	GExpansion.choice(
																			GExpansion.terminal(null, "COMMA"),
																			GExpansion.terminal(null, "ASSIGN"),
																			GExpansion.terminal(null, "SEMICOLON")
																	)
															),
															GExpansion.nonTerminal("ret", "FieldDecl", listOf(
																	expr("modifiers").build()
															))
													),
													GExpansion.nonTerminal("ret", "MethodDecl", listOf(
															expr("modifiers").build()
													))
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
							GExpansion.nonTerminal("type", "Type", listOf(
									expr("null").build()
							)),
							GExpansion.nonTerminal("variables", "VariableDeclarators", emptyList()),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.nonTerminal("type", "Type", listOf(
									expr("null").build()
							)),
							GExpansion.nonTerminal("variables", "VariableDeclarators", emptyList()),
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
							GExpansion.nonTerminal("val", "VariableDeclarator", emptyList()),
							GExpansion.action(
									listOf(
											stmt("variables = append(variables, val);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.nonTerminal("val", "VariableDeclarator", emptyList()),
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
							GExpansion.nonTerminal("id", "VariableDeclaratorId", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "ASSIGN"),
									GExpansion.nonTerminal("initExpr", "VariableInitializer", emptyList()),
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
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.nonTerminal("arrayDims", "ArrayDims", emptyList()),
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
											GExpansion.nonTerminal(null, "Annotations", emptyList()),
											GExpansion.terminal(null, "LBRACKET"),
											GExpansion.terminal(null, "RBRACKET")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
									GExpansion.terminal(null, "LBRACKET"),
									GExpansion.terminal(null, "RBRACKET"),
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
									GExpansion.nonTerminal("ret", "ArrayInitializer", emptyList()),
									GExpansion.nonTerminal("ret", "Expression", emptyList())
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
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("val", "VariableInitializer", emptyList()),
									GExpansion.action(
											listOf(
													stmt("values = append(values, val);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.lookAhead(2),
											GExpansion.terminal(null, "COMMA"),
											GExpansion.nonTerminal("val", "VariableInitializer", emptyList()),
											GExpansion.action(
													listOf(
															stmt("values = append(values, val);").build()
													)
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "RBRACE"),
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
									GExpansion.nonTerminal("typeParameters", "TypeParameters", emptyList())
							),
							GExpansion.nonTerminal("type", "ResultType", emptyList()),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.nonTerminal("parameters", "FormalParameters", emptyList()),
							GExpansion.nonTerminal("arrayDims", "ArrayDims", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("throwsClause", "ThrowsClause", emptyList())
							),
							GExpansion.choice(
									GExpansion.nonTerminal("block", "Block", emptyList()),
									GExpansion.sequence(
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default))) problem = new BUProblem(Severity.ERROR, \"Default methods must have a body\");\n" + "").build()
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
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "FormalParameterList", emptyList())
							),
							GExpansion.terminal(null, "RPAREN"),
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
											GExpansion.nonTerminal("par", "FormalParameter", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, par);").build()
													)
											),
											GExpansion.zeroOrMore(
													GExpansion.terminal(null, "COMMA"),
													GExpansion.nonTerminal("par", "FormalParameter", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, par);").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
							GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
							GExpansion.nonTerminal("type", "Type", listOf(
									expr("null").build()
							)),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "ELLIPSIS"),
									GExpansion.action(
											listOf(
													stmt("isVarArg = true;").build()
											)
									)
							),
							GExpansion.nonTerminal("id", "VariableDeclaratorId", emptyList()),
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
							GExpansion.terminal(null, "THROWS"),
							GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, cit);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.nonTerminal("cit", "AnnotatedQualifiedType", emptyList()),
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
									GExpansion.nonTerminal("typeParameters", "TypeParameters", emptyList())
							),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.nonTerminal("parameters", "FormalParameters", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("throwsClause", "ThrowsClause", emptyList())
							),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.lookAhead(2),
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.lookAhead(
																			GExpansion.nonTerminal(null, "ExplicitConstructorInvocation", emptyList())
																	),
																	GExpansion.nonTerminal("stmt", "ExplicitConstructorInvocation", emptyList()),
																	GExpansion.action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															),
															GExpansion.sequence(
																	GExpansion.lookAhead(2),
																	GExpansion.nonTerminal("stmt", "BlockStatement", emptyList()),
																	GExpansion.action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.lookAhead(2),
															GExpansion.nonTerminal("stmt", "BlockStatement", emptyList()),
															GExpansion.action(
																	listOf(
																			stmt("stmts = append(stmts, stmt);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("stmts", "NodeListVar", emptyList())
											)
									)
							),
							GExpansion.terminal(null, "RBRACE"),
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
															GExpansion.nonTerminal(null, "TypeArguments", emptyList())
													),
													GExpansion.terminal(null, "THIS"),
													GExpansion.terminal(null, "LPAREN")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeArgs", "TypeArguments", emptyList())
											),
											GExpansion.terminal(null, "THIS"),
											GExpansion.action(
													listOf(
															stmt("isThis = true;").build()
													)
											),
											GExpansion.nonTerminal("args", "Arguments", emptyList()),
											GExpansion.terminal(null, "SEMICOLON")
									),
									GExpansion.sequence(
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "PrimaryExpressionWithoutSuperSuffix", emptyList()),
															GExpansion.terminal(null, "DOT")
													),
													GExpansion.nonTerminal("expr", "PrimaryExpressionWithoutSuperSuffix", emptyList()),
													GExpansion.terminal(null, "DOT")
											),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("typeArgs", "TypeArguments", emptyList())
											),
											GExpansion.terminal(null, "SUPER"),
											GExpansion.nonTerminal("args", "Arguments", emptyList()),
											GExpansion.terminal(null, "SEMICOLON")
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
													GExpansion.nonTerminal("stmt", "BlockStatement", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, stmt);").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
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
							GExpansion.nonTerminal("block", "Block", emptyList()),
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
											GExpansion.nonTerminal("primitiveType", "PrimitiveType", listOf(
													expr("annotations").build()
											)),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations", emptyList()),
															GExpansion.terminal(null, "LBRACKET")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType", listOf(
													expr("annotations").build()
											)),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations", emptyList()),
															GExpansion.terminal(null, "LBRACKET")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
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
											GExpansion.nonTerminal("primitiveType", "PrimitiveType", listOf(
													expr("annotations").build()
											)),
											GExpansion.action(
													listOf(
															stmt("lateRun();").build()
													)
											),
											GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
											GExpansion.action(
													listOf(
															stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType", listOf(
													expr("annotations").build()
											)),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations", emptyList()),
															GExpansion.terminal(null, "LBRACKET")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
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
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.nonTerminal("typeArgs", "TypeArgumentsOrDiamond", emptyList())
							),
							GExpansion.action(
									listOf(
											stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, "DOT"),
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
									GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
									GExpansion.nonTerminal("name", "Name", emptyList()),
									GExpansion.zeroOrOne(
											GExpansion.lookAhead(2),
											GExpansion.nonTerminal("typeArgs", "TypeArgumentsOrDiamond", emptyList())
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
							GExpansion.terminal(null, "LT"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "TypeArgumentList", emptyList())
							),
							GExpansion.terminal(null, "GT"),
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
							GExpansion.terminal(null, "LT"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("ret", "TypeArgumentList", emptyList())
							),
							GExpansion.terminal(null, "GT"),
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
									GExpansion.nonTerminal("type", "TypeArgument", emptyList()),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, type);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.terminal(null, "COMMA"),
											GExpansion.nonTerminal("type", "TypeArgument", emptyList()),
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
									GExpansion.lookAhead(
											expr("quotesMode").build()
									),
									GExpansion.terminal(null, "NODE_LIST_VARIABLE"),
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
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.choice(
									GExpansion.nonTerminal("ret", "ReferenceType", listOf(
											expr("annotations").build()
									)),
									GExpansion.nonTerminal("ret", "Wildcard", listOf(
											expr("annotations").build()
									))
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
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							GExpansion.terminal(null, "HOOK"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "EXTENDS"),
													GExpansion.action(
															listOf(
																	stmt("run();").build()
															)
													),
													GExpansion.nonTerminal("boundAnnotations", "Annotations", emptyList()),
													GExpansion.nonTerminal("ext", "ReferenceType", listOf(
															expr("boundAnnotations").build()
													))
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "SUPER"),
													GExpansion.action(
															listOf(
																	stmt("run();").build()
															)
													),
													GExpansion.nonTerminal("boundAnnotations", "Annotations", emptyList()),
													GExpansion.nonTerminal("sup", "ReferenceType", listOf(
															expr("boundAnnotations").build()
													))
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
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.terminal(null, "BOOLEAN"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Boolean;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "CHAR"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Char;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "BYTE"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Byte;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "SHORT"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Short;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "INT"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Int;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "LONG"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Long;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "FLOAT"),
											GExpansion.action(
													listOf(
															stmt("primitive = Primitive.Float;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "DOUBLE"),
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
											GExpansion.terminal(null, "VOID"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SVoidType.make());").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "Type", listOf(
											expr("null").build()
									))
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
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.nonTerminal("ret", "QualifiedType", listOf(
									expr("annotations").build()
							)),
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
							GExpansion.nonTerminal("name", "Name", emptyList()),
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
									GExpansion.terminal(null, "DOT"),
									GExpansion.action(
											listOf(
													stmt("qualifier = optionOf(ret);").build()
											)
									),
									GExpansion.nonTerminal("name", "Name", emptyList()),
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
							stmt("Token id;").build(),
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
											GExpansion.terminal("id", "IDENTIFIER"),
											GExpansion.action(
													listOf(
															stmt("name = dress(SName.make(id.image));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													expr("quotesMode").build()
											),
											GExpansion.nonTerminal("name", "NodeVar", emptyList())
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
													GExpansion.nonTerminal(null, "Name", emptyList()),
													GExpansion.terminal(null, "ARROW")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("ret", "Name", emptyList()),
											GExpansion.terminal(null, "ARROW"),
											GExpansion.nonTerminal("ret", "LambdaBody", listOf(
													expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
													expr("false").build()
											))
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "LPAREN"),
													GExpansion.terminal(null, "RPAREN"),
													GExpansion.terminal(null, "ARROW")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "LPAREN"),
											GExpansion.terminal(null, "RPAREN"),
											GExpansion.terminal(null, "ARROW"),
											GExpansion.nonTerminal("ret", "LambdaBody", listOf(
													expr("emptyList()").build(),
													expr("true").build()
											))
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "LPAREN"),
													GExpansion.nonTerminal(null, "Name", emptyList()),
													GExpansion.terminal(null, "RPAREN"),
													GExpansion.terminal(null, "ARROW")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "LPAREN"),
											GExpansion.nonTerminal("ret", "Name", emptyList()),
											GExpansion.terminal(null, "RPAREN"),
											GExpansion.terminal(null, "ARROW"),
											GExpansion.nonTerminal("ret", "LambdaBody", listOf(
													expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
													expr("true").build()
											))
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "LPAREN"),
													GExpansion.nonTerminal(null, "Name", emptyList()),
													GExpansion.terminal(null, "COMMA")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "LPAREN"),
											GExpansion.nonTerminal("params", "InferredFormalParameterList", emptyList()),
											GExpansion.terminal(null, "RPAREN"),
											GExpansion.terminal(null, "ARROW"),
											GExpansion.nonTerminal("ret", "LambdaBody", listOf(
													expr("params").build(),
													expr("true").build()
											))
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("ret", "ConditionalExpression", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(2),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("op", "AssignmentOperator", emptyList()),
													GExpansion.nonTerminal("value", "Expression", emptyList()),
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
											GExpansion.nonTerminal("expr", "Expression", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, left(expr)));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("block", "Block", emptyList()),
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
							GExpansion.nonTerminal("param", "InferredFormalParameter", emptyList()),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, param);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.nonTerminal("param", "InferredFormalParameter", emptyList()),
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
							GExpansion.nonTerminal("name", "Name", emptyList()),
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
											GExpansion.terminal(null, "ASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Normal;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "STARASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Times;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "SLASHASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Divide;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "REMASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Remainder;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "PLUSASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Plus;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "MINUSASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.Minus;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "LSHIFTASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.LeftShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "RSIGNEDSHIFTASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.RightSignedShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "RUNSIGNEDSHIFTASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.RightUnsignedShift;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "ANDASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.And;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "XORASSIGN"),
											GExpansion.action(
													listOf(
															stmt("ret = AssignOp.XOr;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "ORASSIGN"),
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
							GExpansion.nonTerminal("ret", "ConditionalOrExpression", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "HOOK"),
									GExpansion.nonTerminal("left", "Expression", emptyList()),
									GExpansion.terminal(null, "COLON"),
									GExpansion.nonTerminal("right", "ConditionalExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "ConditionalAndExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "SC_OR"),
									GExpansion.nonTerminal("right", "ConditionalAndExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "InclusiveOrExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "SC_AND"),
									GExpansion.nonTerminal("right", "InclusiveOrExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "ExclusiveOrExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "BIT_OR"),
									GExpansion.nonTerminal("right", "ExclusiveOrExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "AndExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "XOR"),
									GExpansion.nonTerminal("right", "AndExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "EqualityExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "BIT_AND"),
									GExpansion.nonTerminal("right", "EqualityExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "InstanceOfExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "EQ"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Equal;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "NE"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.NotEqual;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "InstanceOfExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "RelationalExpression", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.terminal(null, "INSTANCEOF"),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
									GExpansion.nonTerminal("type", "Type", listOf(
											expr("annotations").build()
									)),
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
							GExpansion.nonTerminal("ret", "ShiftExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "LT"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Less;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "GT"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Greater;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "LE"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.LessOrEqual;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "GE"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.GreaterOrEqual;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "ShiftExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "AdditiveExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "LSHIFT"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.LeftShift;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(3),
													GExpansion.nonTerminal(null, "RUNSIGNEDSHIFT", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.RightUnsignedShift;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(2),
													GExpansion.nonTerminal(null, "RSIGNEDSHIFT", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.RightSignedShift;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "AdditiveExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "MultiplicativeExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "PLUS"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Plus;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "MINUS"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Minus;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "MultiplicativeExpression", emptyList()),
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
							GExpansion.nonTerminal("ret", "UnaryExpression", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "STAR"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Times;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "SLASH"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Divide;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "REM"),
													GExpansion.action(
															listOf(
																	stmt("op = BinaryOp.Remainder;").build()
															)
													)
											)
									),
									GExpansion.nonTerminal("right", "UnaryExpression", emptyList()),
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
									GExpansion.nonTerminal("ret", "PrefixExpression", emptyList()),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "PLUS"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Positive;").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "MINUS"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Negative;").build()
																	)
															)
													)
											),
											GExpansion.nonTerminal("ret", "UnaryExpression", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus", emptyList())
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
											GExpansion.terminal(null, "INCR"),
											GExpansion.action(
													listOf(
															stmt("op = UnaryOp.PreIncrement;").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "DECR"),
											GExpansion.action(
													listOf(
															stmt("op = UnaryOp.PreDecrement;").build()
													)
											)
									)
							),
							GExpansion.nonTerminal("ret", "UnaryExpression", emptyList()),
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
															GExpansion.terminal(null, "TILDE"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Inverse;").build()
																	)
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "BANG"),
															GExpansion.action(
																	listOf(
																			stmt("op = UnaryOp.Not;").build()
																	)
															)
													)
											),
											GExpansion.nonTerminal("ret", "UnaryExpression", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "CastExpression", emptyList())
											),
											GExpansion.nonTerminal("ret", "CastExpression", emptyList())
									),
									GExpansion.nonTerminal("ret", "PostfixExpression", emptyList())
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
							GExpansion.nonTerminal("ret", "PrimaryExpression", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.terminal(null, "INCR"),
													GExpansion.action(
															listOf(
																	stmt("op = UnaryOp.PostIncrement;").build()
															)
													)
											),
											GExpansion.sequence(
													GExpansion.terminal(null, "DECR"),
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
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("primitiveType", "PrimitiveType", listOf(
													expr("annotations").build()
											)),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.nonTerminal("ret", "UnaryExpression", emptyList()),
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
															GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
															GExpansion.action(
																	listOf(
																			stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
																	)
															),
															GExpansion.nonTerminal("type", "ReferenceCastTypeRest", listOf(
																	expr("type").build()
															)),
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus", emptyList()),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SCastExpr.make(type, ret));").build()
																	)
															)
													)
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType", listOf(
													expr("annotations").build()
											)),
											GExpansion.zeroOrOne(
													GExpansion.lookAhead(
															GExpansion.nonTerminal(null, "Annotations", emptyList()),
															GExpansion.terminal(null, "LBRACKET")
													),
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											),
											GExpansion.nonTerminal("type", "ReferenceCastTypeRest", listOf(
													expr("type").build()
											)),
											GExpansion.terminal(null, "RPAREN"),
											GExpansion.nonTerminal("ret", "UnaryExpressionNotPlusMinus", emptyList()),
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
											GExpansion.terminal(null, "BIT_AND")
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
											GExpansion.terminal(null, "BIT_AND"),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
											GExpansion.nonTerminal("type", "ReferenceType", listOf(
													expr("annotations").build()
											)),
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
							stmt("Token literal;").build(),
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
											GExpansion.terminal("literal", "INTEGER_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Integer.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "LONG_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Long.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "FLOAT_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Float.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "DOUBLE_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Double.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "CHARACTER_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Character.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "STRING_LITERAL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(String.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "TRUE"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "FALSE"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.terminal("literal", "NULL"),
											GExpansion.action(
													listOf(
															stmt("ret = SLiteralExpr.make(Void.class, literal.image);").build()
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
							GExpansion.nonTerminal("ret", "PrimaryPrefix", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.nonTerminal("ret", "PrimarySuffix", listOf(
											expr("ret").build()
									))
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
							GExpansion.nonTerminal("ret", "PrimaryPrefix", emptyList()),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(
											GExpansion.nonTerminal(null, "PrimarySuffixWithoutSuper", emptyList())
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.nonTerminal("ret", "PrimarySuffixWithoutSuper", listOf(
											expr("ret").build()
									))
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
									GExpansion.nonTerminal("ret", "Literal", emptyList()),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "THIS"),
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
											GExpansion.terminal(null, "SUPER"),
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
															GExpansion.terminal(null, "DOT"),
															GExpansion.choice(
																	GExpansion.sequence(
																			GExpansion.lookAhead(
																					GExpansion.zeroOrOne(
																							GExpansion.nonTerminal(null, "TypeArguments", emptyList())
																					),
																					GExpansion.nonTerminal(null, "Name", emptyList()),
																					GExpansion.terminal(null, "LPAREN")
																			),
																			GExpansion.nonTerminal("ret", "MethodInvocation", listOf(
																					expr("ret").build()
																			))
																	),
																	GExpansion.nonTerminal("ret", "FieldAccess", listOf(
																			expr("ret").build()
																	))
															)
													),
													GExpansion.sequence(
															GExpansion.action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															GExpansion.nonTerminal("ret", "MethodReferenceSuffix", listOf(
																	expr("ret").build()
															))
													)
											)
									),
									GExpansion.nonTerminal("ret", "AllocationExpression", listOf(
											expr("null").build()
									)),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "ResultType", emptyList()),
													GExpansion.terminal(null, "DOT"),
													GExpansion.terminal(null, "CLASS")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("type", "ResultType", emptyList()),
											GExpansion.terminal(null, "DOT"),
											GExpansion.terminal(null, "CLASS"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SClassExpr.make(type));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "ResultType", emptyList()),
													GExpansion.terminal(null, "DOUBLECOLON")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("type", "ResultType", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = STypeExpr.make(type);").build()
													)
											),
											GExpansion.nonTerminal("ret", "MethodReferenceSuffix", listOf(
													expr("ret").build()
											))
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.zeroOrOne(
															GExpansion.nonTerminal(null, "TypeArguments", emptyList())
													),
													GExpansion.nonTerminal(null, "Name", emptyList()),
													GExpansion.terminal(null, "LPAREN")
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("ret", "MethodInvocation", listOf(
													expr("null").build()
											))
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("ret", "Name", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													GExpansion.terminal(null, "ARROW"),
													GExpansion.nonTerminal("ret", "LambdaBody", listOf(
															expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
															expr("false").build()
													))
											)
									),
									GExpansion.sequence(
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.terminal(null, "LPAREN"),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.terminal(null, "ARROW"),
															GExpansion.nonTerminal("ret", "LambdaBody", listOf(
																	expr("emptyList()").build(),
																	expr("true").build()
															))
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "RPAREN"),
																	GExpansion.terminal(null, "ARROW")
															),
															GExpansion.nonTerminal("ret", "Name", emptyList()),
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.terminal(null, "ARROW"),
															GExpansion.nonTerminal("ret", "LambdaBody", listOf(
																	expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
																	expr("true").build()
															))
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "COMMA")
															),
															GExpansion.nonTerminal("params", "InferredFormalParameterList", emptyList()),
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.terminal(null, "ARROW"),
															GExpansion.nonTerminal("ret", "LambdaBody", listOf(
																	expr("params").build(),
																	expr("true").build()
															))
													),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	expr("isLambda()").build()
															),
															GExpansion.nonTerminal("params", "FormalParameterList", emptyList()),
															GExpansion.terminal(null, "RPAREN"),
															GExpansion.terminal(null, "ARROW"),
															GExpansion.nonTerminal("ret", "LambdaBody", listOf(
																	expr("params").build(),
																	expr("true").build()
															))
													),
													GExpansion.sequence(
															GExpansion.nonTerminal("ret", "Expression", emptyList()),
															GExpansion.terminal(null, "RPAREN"),
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
											GExpansion.nonTerminal("ret", "PrimarySuffixWithoutSuper", listOf(
													expr("scope").build()
											))
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "DOT"),
											GExpansion.terminal(null, "SUPER"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SSuperExpr.make(optionOf(scope)));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "MethodReferenceSuffix", listOf(
											expr("scope").build()
									))
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
											GExpansion.terminal(null, "DOT"),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.terminal(null, "THIS"),
															GExpansion.action(
																	listOf(
																			stmt("ret = dress(SThisExpr.make(optionOf(scope)));").build()
																	)
															)
													),
													GExpansion.nonTerminal("ret", "AllocationExpression", listOf(
															expr("scope").build()
													)),
													GExpansion.sequence(
															GExpansion.lookAhead(
																	GExpansion.zeroOrOne(
																			GExpansion.nonTerminal(null, "TypeArguments", emptyList())
																	),
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "LPAREN")
															),
															GExpansion.nonTerminal("ret", "MethodInvocation", listOf(
																	expr("scope").build()
															))
													),
													GExpansion.nonTerminal("ret", "FieldAccess", listOf(
															expr("scope").build()
													))
											)
									),
									GExpansion.sequence(
											GExpansion.terminal(null, "LBRACKET"),
											GExpansion.nonTerminal("ret", "Expression", emptyList()),
											GExpansion.terminal(null, "RBRACKET"),
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
							GExpansion.nonTerminal("name", "Name", emptyList()),
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
									GExpansion.nonTerminal("typeArgs", "TypeArguments", emptyList())
							),
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.nonTerminal("args", "Arguments", emptyList()),
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
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.zeroOrOne(
									GExpansion.choice(
											GExpansion.sequence(
													GExpansion.lookAhead(1),
													GExpansion.nonTerminal("expr", "Expression", emptyList()),
													GExpansion.action(
															listOf(
																	stmt("ret = append(ret, expr);").build()
															)
													),
													GExpansion.zeroOrMore(
															GExpansion.terminal(null, "COMMA"),
															GExpansion.nonTerminal("expr", "Expression", emptyList()),
															GExpansion.action(
																	listOf(
																			stmt("ret = append(ret, expr);").build()
																	)
															)
													)
											),
											GExpansion.sequence(
													GExpansion.lookAhead(
															expr("quotesMode").build()
													),
													GExpansion.nonTerminal("ret", "NodeListVar", emptyList())
											)
									)
							),
							GExpansion.terminal(null, "RPAREN"),
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
							GExpansion.terminal(null, "DOUBLECOLON"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeArgs", "TypeArguments", emptyList())
							),
							GExpansion.choice(
									GExpansion.nonTerminal("name", "Name", emptyList()),
									GExpansion.sequence(
											GExpansion.terminal(null, "NEW"),
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
											stmt("if (scope == null) run();\n" + "").build()
									)
							),
							GExpansion.terminal(null, "NEW"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("typeArgs", "TypeArguments", emptyList())
							),
							GExpansion.action(
									listOf(
											stmt("run();").build()
									)
							),
							GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "PrimitiveType", listOf(
													expr("annotations").build()
											)),
											GExpansion.nonTerminal("ret", "ArrayCreationExpr", listOf(
													expr("type").build()
											))
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("type", "QualifiedType", listOf(
													expr("annotations").build()
											)),
											GExpansion.choice(
													GExpansion.nonTerminal("ret", "ArrayCreationExpr", listOf(
															expr("type").build()
													)),
													GExpansion.sequence(
															GExpansion.nonTerminal("args", "Arguments", emptyList()),
															GExpansion.zeroOrOne(
																	GExpansion.lookAhead(
																			GExpansion.terminal(null, "LBRACE")
																	),
																	GExpansion.nonTerminal("anonymousBody", "ClassOrInterfaceBody", listOf(
																			expr("TypeKind.Class").build()
																	))
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
											GExpansion.nonTerminal(null, "Annotations", emptyList()),
											GExpansion.terminal(null, "LBRACKET"),
											GExpansion.nonTerminal(null, "Expression", emptyList()),
											GExpansion.terminal(null, "RBRACKET")
									),
									GExpansion.nonTerminal("arrayDimExprs", "ArrayDimExprsMandatory", emptyList()),
									GExpansion.nonTerminal("arrayDims", "ArrayDims", emptyList()),
									GExpansion.action(
											listOf(
													stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, none()));").build()
											)
									)
							),
							GExpansion.sequence(
									GExpansion.nonTerminal("arrayDims", "ArrayDimsMandatory", emptyList()),
									GExpansion.nonTerminal("initializer", "ArrayInitializer", emptyList()),
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
											GExpansion.nonTerminal(null, "Annotations", emptyList()),
											GExpansion.terminal(null, "LBRACKET"),
											GExpansion.nonTerminal(null, "Expression", emptyList()),
											GExpansion.terminal(null, "RBRACKET")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
									GExpansion.terminal(null, "LBRACKET"),
									GExpansion.nonTerminal("expr", "Expression", emptyList()),
									GExpansion.terminal(null, "RBRACKET"),
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
											GExpansion.nonTerminal(null, "Annotations", emptyList()),
											GExpansion.terminal(null, "LBRACKET"),
											GExpansion.terminal(null, "RBRACKET")
									),
									GExpansion.action(
											listOf(
													stmt("run();").build()
											)
									),
									GExpansion.nonTerminal("annotations", "Annotations", emptyList()),
									GExpansion.terminal(null, "LBRACKET"),
									GExpansion.terminal(null, "RBRACKET"),
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
											GExpansion.nonTerminal("ret", "LabeledStatement", emptyList())
									),
									GExpansion.nonTerminal("ret", "AssertStatement", emptyList()),
									GExpansion.nonTerminal("ret", "Block", emptyList()),
									GExpansion.nonTerminal("ret", "EmptyStatement", emptyList()),
									GExpansion.nonTerminal("ret", "StatementExpression", emptyList()),
									GExpansion.nonTerminal("ret", "SwitchStatement", emptyList()),
									GExpansion.nonTerminal("ret", "IfStatement", emptyList()),
									GExpansion.nonTerminal("ret", "WhileStatement", emptyList()),
									GExpansion.nonTerminal("ret", "DoStatement", emptyList()),
									GExpansion.nonTerminal("ret", "ForStatement", emptyList()),
									GExpansion.nonTerminal("ret", "BreakStatement", emptyList()),
									GExpansion.nonTerminal("ret", "ContinueStatement", emptyList()),
									GExpansion.nonTerminal("ret", "ReturnStatement", emptyList()),
									GExpansion.nonTerminal("ret", "ThrowStatement", emptyList()),
									GExpansion.nonTerminal("ret", "SynchronizedStatement", emptyList()),
									GExpansion.nonTerminal("ret", "TryStatement", emptyList())
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
							GExpansion.terminal(null, "ASSERT"),
							GExpansion.nonTerminal("check", "Expression", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "COLON"),
									GExpansion.nonTerminal("msg", "Expression", emptyList())
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.nonTerminal("label", "Name", emptyList()),
							GExpansion.terminal(null, "COLON"),
							GExpansion.nonTerminal("stmt", "Statement", emptyList()),
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
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.nonTerminal("stmts", "Statements", emptyList()),
							GExpansion.terminal(null, "RBRACE"),
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
													GExpansion.nonTerminal(null, "ModifiersNoDefault", emptyList()),
													GExpansion.choice(
															GExpansion.terminal(null, "CLASS"),
															GExpansion.terminal(null, "INTERFACE")
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
											GExpansion.nonTerminal("modifiers", "ModifiersNoDefault", emptyList()),
											GExpansion.nonTerminal("typeDecl", "ClassOrInterfaceDecl", listOf(
													expr("modifiers").build()
											)),
											GExpansion.action(
													listOf(
															stmt("ret = dress(STypeDeclarationStmt.make(typeDecl));").build()
													)
											)
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "VariableDeclExpression", emptyList())
											),
											GExpansion.action(
													listOf(
															stmt("run();").build()
													)
											),
											GExpansion.nonTerminal("expr", "VariableDeclExpression", emptyList()),
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.action(
													listOf(
															stmt("ret = dress(SExpressionStmt.make(expr));").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "Statement", emptyList())
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
							GExpansion.nonTerminal("modifiers", "ModifiersNoDefault", emptyList()),
							GExpansion.nonTerminal("variableDecl", "VariableDecl", listOf(
									expr("modifiers").build()
							)),
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
							GExpansion.terminal(null, "SEMICOLON"),
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
									GExpansion.nonTerminal("expr", "PrefixExpression", emptyList()),
									GExpansion.sequence(
											GExpansion.nonTerminal("expr", "PrimaryExpression", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	GExpansion.terminal(null, "INCR"),
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
																	GExpansion.terminal(null, "DECR"),
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
																	GExpansion.nonTerminal("op", "AssignmentOperator", emptyList()),
																	GExpansion.nonTerminal("value", "Expression", emptyList()),
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
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "SWITCH"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("selector", "Expression", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrMore(
									GExpansion.nonTerminal("entry", "SwitchEntry", emptyList()),
									GExpansion.action(
											listOf(
													stmt("entries = append(entries, entry);").build()
											)
									)
							),
							GExpansion.terminal(null, "RBRACE"),
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
											GExpansion.terminal(null, "CASE"),
											GExpansion.nonTerminal("label", "Expression", emptyList())
									),
									GExpansion.terminal(null, "_DEFAULT")
							),
							GExpansion.terminal(null, "COLON"),
							GExpansion.nonTerminal("stmts", "Statements", emptyList()),
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
							GExpansion.terminal(null, "IF"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("condition", "Expression", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("thenStmt", "Statement", emptyList()),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(1),
									GExpansion.terminal(null, "ELSE"),
									GExpansion.nonTerminal("elseStmt", "Statement", emptyList())
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
							GExpansion.terminal(null, "WHILE"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("condition", "Expression", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("body", "Statement", emptyList()),
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
							GExpansion.terminal(null, "DO"),
							GExpansion.nonTerminal("body", "Statement", emptyList()),
							GExpansion.terminal(null, "WHILE"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("condition", "Expression", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "FOR"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.nonTerminal(null, "VariableDeclExpression", emptyList()),
													GExpansion.terminal(null, "COLON")
											),
											GExpansion.nonTerminal("varExpr", "VariableDeclExpression", emptyList()),
											GExpansion.terminal(null, "COLON"),
											GExpansion.nonTerminal("expr", "Expression", emptyList())
									),
									GExpansion.sequence(
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("init", "ForInit", emptyList())
											),
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("expr", "Expression", emptyList())
											),
											GExpansion.terminal(null, "SEMICOLON"),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("update", "ForUpdate", emptyList())
											)
									)
							),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("body", "Statement", emptyList()),
							GExpansion.action(
									listOf(
											stmt("if (varExpr != null)\n" + "\treturn dress(SForeachStmt.make(varExpr, expr, body));\n" + "else\n" + "\treturn dress(SForStmt.make(init, expr, update, body));\n" + "").build()
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
													GExpansion.nonTerminal(null, "Modifiers", emptyList()),
													GExpansion.nonTerminal(null, "Type", emptyList()),
													GExpansion.nonTerminal(null, "Name", emptyList())
											),
											GExpansion.nonTerminal("expr", "VariableDeclExpression", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = emptyList();").build(),
															stmt("ret = append(ret, expr);").build()
													)
											)
									),
									GExpansion.nonTerminal("ret", "ExpressionList", emptyList())
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
							GExpansion.nonTerminal("expr", "Expression", emptyList()),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, expr);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.nonTerminal("expr", "Expression", emptyList()),
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
							GExpansion.nonTerminal("ret", "ExpressionList", emptyList()),
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
							GExpansion.terminal(null, "BREAK"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("id", "Name", emptyList())
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "CONTINUE"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("id", "Name", emptyList())
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "RETURN"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("expr", "Expression", emptyList())
							),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "THROW"),
							GExpansion.nonTerminal("expr", "Expression", emptyList()),
							GExpansion.terminal(null, "SEMICOLON"),
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
							GExpansion.terminal(null, "SYNCHRONIZED"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("expr", "Expression", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("block", "Block", emptyList()),
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
							GExpansion.terminal(null, "TRY"),
							GExpansion.choice(
									GExpansion.sequence(
											GExpansion.nonTerminal("resources", "ResourceSpecification", listOf(
													expr("trailingSemiColon").build()
											)),
											GExpansion.nonTerminal("tryBlock", "Block", emptyList()),
											GExpansion.zeroOrOne(
													GExpansion.nonTerminal("catchClauses", "CatchClauses", emptyList())
											),
											GExpansion.zeroOrOne(
													GExpansion.terminal(null, "FINALLY"),
													GExpansion.nonTerminal("finallyBlock", "Block", emptyList())
											)
									),
									GExpansion.sequence(
											GExpansion.nonTerminal("tryBlock", "Block", emptyList()),
											GExpansion.choice(
													GExpansion.sequence(
															GExpansion.nonTerminal("catchClauses", "CatchClauses", emptyList()),
															GExpansion.zeroOrOne(
																	GExpansion.terminal(null, "FINALLY"),
																	GExpansion.nonTerminal("finallyBlock", "Block", emptyList())
															)
													),
													GExpansion.sequence(
															GExpansion.terminal(null, "FINALLY"),
															GExpansion.nonTerminal("finallyBlock", "Block", emptyList())
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
									GExpansion.nonTerminal("catchClause", "CatchClause", emptyList()),
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
							GExpansion.terminal(null, "CATCH"),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("param", "CatchFormalParameter", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
							GExpansion.nonTerminal("catchBlock", "Block", emptyList()),
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
							GExpansion.nonTerminal("modifiers", "Modifiers", emptyList()),
							GExpansion.nonTerminal("exceptType", "QualifiedType", listOf(
									expr("null").build()
							)),
							GExpansion.action(
									listOf(
											stmt("exceptTypes = append(exceptTypes, exceptType);").build()
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(
											GExpansion.terminal(null, "BIT_OR")
									),
									GExpansion.action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									GExpansion.oneOrMore(
											GExpansion.terminal(null, "BIT_OR"),
											GExpansion.nonTerminal("exceptType", "AnnotatedQualifiedType", emptyList()),
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
							GExpansion.nonTerminal("exceptId", "VariableDeclaratorId", emptyList()),
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
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("var", "VariableDeclExpression", emptyList()),
							GExpansion.action(
									listOf(
											stmt("vars = append(vars, var);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, "SEMICOLON"),
									GExpansion.nonTerminal("var", "VariableDeclExpression", emptyList()),
									GExpansion.action(
											listOf(
													stmt("vars = append(vars, var);").build()
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.lookAhead(2),
									GExpansion.terminal(null, "SEMICOLON"),
									GExpansion.action(
											listOf(
													stmt("trailingSemiColon.value = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "RPAREN"),
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
							GExpansion.lookAhead(
									expr("getToken(1).kind == GT && getToken(1).realKind == RUNSIGNEDSHIFT").build()
							),
							GExpansion.terminal(null, "GT"),
							GExpansion.terminal(null, "GT"),
							GExpansion.terminal(null, "GT"),
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
							GExpansion.lookAhead(
									expr("getToken(1).kind == GT && getToken(1).realKind == RSIGNEDSHIFT").build()
							),
							GExpansion.terminal(null, "GT"),
							GExpansion.terminal(null, "GT"),
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
									GExpansion.nonTerminal("annotation", "Annotation", emptyList()),
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
													GExpansion.terminal(null, "AT"),
													GExpansion.nonTerminal(null, "QualifiedName", emptyList()),
													GExpansion.terminal(null, "LPAREN"),
													GExpansion.choice(
															GExpansion.sequence(
																	GExpansion.nonTerminal(null, "Name", emptyList()),
																	GExpansion.terminal(null, "ASSIGN")
															),
															GExpansion.terminal(null, "RPAREN")
													)
											),
											GExpansion.nonTerminal("ret", "NormalAnnotation", emptyList())
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "AT"),
													GExpansion.nonTerminal(null, "QualifiedName", emptyList()),
													GExpansion.terminal(null, "LPAREN")
											),
											GExpansion.nonTerminal("ret", "SingleMemberAnnotation", emptyList())
									),
									GExpansion.sequence(
											GExpansion.lookAhead(
													GExpansion.terminal(null, "AT"),
													GExpansion.nonTerminal(null, "QualifiedName", emptyList())
											),
											GExpansion.nonTerminal("ret", "MarkerAnnotation", emptyList())
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
							GExpansion.terminal(null, "AT"),
							GExpansion.nonTerminal("name", "QualifiedName", emptyList()),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("pairs", "MemberValuePairs", emptyList())
							),
							GExpansion.terminal(null, "RPAREN"),
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
							GExpansion.terminal(null, "AT"),
							GExpansion.nonTerminal("name", "QualifiedName", emptyList()),
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
							GExpansion.terminal(null, "AT"),
							GExpansion.nonTerminal("name", "QualifiedName", emptyList()),
							GExpansion.terminal(null, "LPAREN"),
							GExpansion.nonTerminal("memberVal", "MemberValue", emptyList()),
							GExpansion.terminal(null, "RPAREN"),
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
							GExpansion.nonTerminal("pair", "MemberValuePair", emptyList()),
							GExpansion.action(
									listOf(
											stmt("ret = append(ret, pair);").build()
									)
							),
							GExpansion.zeroOrMore(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.nonTerminal("pair", "MemberValuePair", emptyList()),
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
							GExpansion.nonTerminal("name", "Name", emptyList()),
							GExpansion.terminal(null, "ASSIGN"),
							GExpansion.nonTerminal("value", "MemberValue", emptyList()),
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
									GExpansion.nonTerminal("ret", "Annotation", emptyList()),
									GExpansion.nonTerminal("ret", "MemberValueArrayInitializer", emptyList()),
									GExpansion.nonTerminal("ret", "ConditionalExpression", emptyList())
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
							GExpansion.terminal(null, "LBRACE"),
							GExpansion.zeroOrOne(
									GExpansion.nonTerminal("member", "MemberValue", emptyList()),
									GExpansion.action(
											listOf(
													stmt("ret = append(ret, member);").build()
											)
									),
									GExpansion.zeroOrMore(
											GExpansion.lookAhead(2),
											GExpansion.terminal(null, "COMMA"),
											GExpansion.nonTerminal("member", "MemberValue", emptyList()),
											GExpansion.action(
													listOf(
															stmt("ret = append(ret, member);").build()
													)
											)
									)
							),
							GExpansion.zeroOrOne(
									GExpansion.terminal(null, "COMMA"),
									GExpansion.action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							GExpansion.terminal(null, "RBRACE"),
							GExpansion.action(
									listOf(
											stmt("return dress(SArrayInitializerExpr.make(ret, trailingComma));").build()
									)
							)
					)
			)
	);
}
