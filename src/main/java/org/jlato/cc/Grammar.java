package org.jlato.cc;

import org.jlato.cc.grammar.GExpansion;
import org.jlato.cc.grammar.GProduction;
import org.jlato.cc.grammar.GProductions;
import org.jlato.tree.decl.MethodDecl;

import static org.jlato.rewrite.Quotes.expr;
import static org.jlato.rewrite.Quotes.memberDecl;
import static org.jlato.rewrite.Quotes.stmt;
import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.listOf;
import static org.jlato.cc.grammar.GExpansion.*;

public class Grammar {

	public static GProductions productions = new GProductions(
			new GProduction("NodeListVar",
					(MethodDecl) memberDecl("BUTree<SNodeList> NodeListVar();").build(),
					listOf(
							stmt("Token id;").build()
					),
					sequence(
							terminal("id", "NODE_LIST_VARIABLE"),
							action(
									listOf(
											stmt("return makeVar(id);").build()
									)
							)
					)
			),
			new GProduction("NodeVar",
					(MethodDecl) memberDecl("BUTree<SName> NodeVar();").build(),
					listOf(
							stmt("Token id;").build()
					),
					sequence(
							terminal("id", "NODE_VARIABLE"),
							action(
									listOf(
											stmt("return makeVar(id);").build()
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							zeroOrOne(
									lookAhead(
											nonTerminal(null, "PackageDecl")
									),
									nonTerminal("packageDecl", "PackageDecl")
							),
							nonTerminal("imports", "ImportDecls"),
							nonTerminal("types", "TypeDecls"),
							action(
									listOf(
											stmt("compilationUnit = dress(SCompilationUnit.make(packageDecl, imports, types));").build()
									)
							),
							nonTerminal(null, "Epilog"),
							action(
									listOf(
											stmt("return dressWithPrologAndEpilog(compilationUnit);").build()
									)
							)
					)
			),
			new GProduction("Epilog",
					(MethodDecl) memberDecl("void Epilog();").build(),
					emptyList(),
					choice(
							terminal(null, "EOF"),
							terminal(null, "EOF")
					)
			),
			new GProduction("PackageDecl",
					(MethodDecl) memberDecl("BUTree<SPackageDecl> PackageDecl();").build(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SQualifiedName> name;").build()
					),
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							terminal(null, "PACKAGE"),
							nonTerminal("name", "QualifiedName"),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							zeroOrMore(
									nonTerminal("importDecl", "ImportDecl"),
									action(
											listOf(
													stmt("imports = append(imports, importDecl);").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "IMPORT"),
							zeroOrOne(
									terminal(null, "STATIC"),
									action(
											listOf(
													stmt("isStatic = true;").build()
											)
									)
							),
							nonTerminal("name", "QualifiedName"),
							zeroOrOne(
									terminal(null, "DOT"),
									terminal(null, "STAR"),
									action(
											listOf(
													stmt("isAsterisk = true;").build()
											)
									)
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							zeroOrMore(
									nonTerminal("typeDecl", "TypeDecl"),
									action(
											listOf(
													stmt("types = append(types, typeDecl);").build()
											)
									)
							),
							action(
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
					sequence(
							zeroOrMore(
									lookAhead(2),
									choice(
											sequence(
													terminal(null, "PUBLIC"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											sequence(
													terminal(null, "PROTECTED"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											sequence(
													terminal(null, "PRIVATE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											sequence(
													terminal(null, "ABSTRACT"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											sequence(
													terminal(null, "DEFAULT"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Default));").build()
															)
													)
											),
											sequence(
													terminal(null, "STATIC"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											sequence(
													terminal(null, "FINAL"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											sequence(
													terminal(null, "TRANSIENT"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											sequence(
													terminal(null, "VOLATILE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											sequence(
													terminal(null, "SYNCHRONIZED"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											sequence(
													terminal(null, "NATIVE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											sequence(
													terminal(null, "STRICTFP"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, ann);").build()
															)
													)
											)
									)
							),
							action(
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
					sequence(
							zeroOrMore(
									lookAhead(2),
									choice(
											sequence(
													terminal(null, "PUBLIC"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
															)
													)
											),
											sequence(
													terminal(null, "PROTECTED"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
															)
													)
											),
											sequence(
													terminal(null, "PRIVATE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
															)
													)
											),
											sequence(
													terminal(null, "ABSTRACT"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
															)
													)
											),
											sequence(
													terminal(null, "STATIC"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
															)
													)
											),
											sequence(
													terminal(null, "FINAL"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
															)
													)
											),
											sequence(
													terminal(null, "TRANSIENT"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
															)
													)
											),
											sequence(
													terminal(null, "VOLATILE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
															)
													)
											),
											sequence(
													terminal(null, "SYNCHRONIZED"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
															)
													)
											),
											sequence(
													terminal(null, "NATIVE"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
															)
													)
											),
											sequence(
													terminal(null, "STRICTFP"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
															)
													)
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action(
															listOf(
																	stmt("modifiers = append(modifiers, ann);").build()
															)
													)
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal(null, "SEMICOLON"),
											action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											choice(
													nonTerminal("ret", "ClassOrInterfaceDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "EnumDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "AnnotationTypeDecl", null, listOf(
															expr("modifiers").build()
													))
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											terminal(null, "CLASS"),
											action(
													listOf(
															stmt("typeKind = TypeKind.Class;").build()
													)
											),
											nonTerminal("name", "Name"),
											zeroOrOne(
													nonTerminal("typeParams", "TypeParameters")
											),
											zeroOrOne(
													terminal(null, "EXTENDS"),
													nonTerminal("superClassType", "AnnotatedQualifiedType")
											),
											zeroOrOne(
													nonTerminal("implementsClause", "ImplementsList", null, listOf(
															expr("typeKind").build(),
															expr("problem").build()
													))
											)
									),
									sequence(
											terminal(null, "INTERFACE"),
											action(
													listOf(
															stmt("typeKind = TypeKind.Interface;").build()
													)
											),
											nonTerminal("name", "Name"),
											zeroOrOne(
													nonTerminal("typeParams", "TypeParameters")
											),
											zeroOrOne(
													nonTerminal("extendsClause", "ExtendsList")
											)
									)
							),
							nonTerminal("members", "ClassOrInterfaceBody", null, listOf(
									expr("typeKind").build()
							)),
							action(
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
					sequence(
							terminal(null, "EXTENDS"),
							choice(
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											zeroOrMore(
													terminal(null, "COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									)
							),
							action(
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
					sequence(
							terminal(null, "IMPLEMENTS"),
							choice(
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											zeroOrMore(
													terminal(null, "COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											),
											action(
													listOf(
															stmt("if (typeKind == TypeKind.Interface) problem.value = new BUProblem(Severity.ERROR, \"An interface cannot implement other interfaces\");\n" + "").build()
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									)
							),
							action(
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
					sequence(
							terminal(null, "ENUM"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("implementsClause", "ImplementsList", null, listOf(
											expr("TypeKind.Enum").build(),
											expr("problem").build()
									))
							),
							terminal(null, "LBRACE"),
							zeroOrOne(
									choice(
											sequence(
													nonTerminal("entry", "EnumConstantDecl"),
													action(
															listOf(
																	stmt("constants = append(constants, entry);").build()
															)
													),
													zeroOrMore(
															lookAhead(2),
															terminal(null, "COMMA"),
															nonTerminal("entry", "EnumConstantDecl"),
															action(
																	listOf(
																			stmt("constants = append(constants, entry);").build()
																	)
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("constants", "NodeListVar")
											)
									)
							),
							zeroOrOne(
									terminal(null, "COMMA"),
									action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							zeroOrOne(
									terminal(null, "SEMICOLON"),
									nonTerminal("members", "ClassOrInterfaceBodyDecls", null, listOf(
											expr("TypeKind.Enum").build()
									))
							),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("args", "Arguments")
							),
							zeroOrOne(
									nonTerminal("classBody", "ClassOrInterfaceBody", null, listOf(
											expr("TypeKind.Class").build()
									))
							),
							action(
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
					sequence(
							terminal(null, "AT"),
							terminal(null, "INTERFACE"),
							nonTerminal("name", "Name"),
							nonTerminal("members", "AnnotationTypeBody"),
							action(
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
					sequence(
							terminal(null, "LBRACE"),
							zeroOrOne(
									choice(
											oneOrMore(
													nonTerminal("member", "AnnotationTypeBodyDecl"),
													action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											)
									)
							),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal(null, "SEMICOLON"),
											action(
													listOf(
															stmt("ret = dress(SEmptyTypeDecl.make());").build()
													)
											)
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											choice(
													sequence(
															lookAhead(
																	nonTerminal(null, "Type"),
																	nonTerminal(null, "Name"),
																	terminal(null, "LPAREN")
															),
															nonTerminal("ret", "AnnotationTypeMemberDecl", null, listOf(
																	expr("modifiers").build()
															))
													),
													nonTerminal("ret", "ClassOrInterfaceDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "EnumDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "AnnotationTypeDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "FieldDecl", null, listOf(
															expr("modifiers").build()
													))
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							nonTerminal("name", "Name"),
							terminal(null, "LPAREN"),
							terminal(null, "RPAREN"),
							nonTerminal("dims", "ArrayDims"),
							zeroOrOne(
									terminal(null, "DEFAULT"),
									nonTerminal("val", "MemberValue"),
									action(
											listOf(
													stmt("defaultVal = optionOf(val);").build()
											)
									)
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							terminal(null, "LT"),
							choice(
									sequence(
											nonTerminal("tp", "TypeParameter"),
											action(
													listOf(
															stmt("ret = append(ret, tp);").build()
													)
											),
											zeroOrMore(
													terminal(null, "COMMA"),
													nonTerminal("tp", "TypeParameter"),
													action(
															listOf(
																	stmt("ret = append(ret, tp);").build()
															)
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									)
							),
							terminal(null, "GT"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("typeBounds", "TypeBounds")
							),
							action(
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
					sequence(
							terminal(null, "EXTENDS"),
							choice(
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(
													listOf(
															stmt("ret = append(ret, cit);").build()
													)
											),
											zeroOrMore(
													terminal(null, "BIT_AND"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(
															listOf(
																	stmt("ret = append(ret, cit);").build()
															)
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									)
							),
							action(
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
					sequence(
							terminal(null, "LBRACE"),
							nonTerminal("ret", "ClassOrInterfaceBodyDecls", null, listOf(
									expr("typeKind").build()
							)),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							zeroOrOne(
									choice(
											oneOrMore(
													nonTerminal("member", "ClassOrInterfaceBodyDecl", null, listOf(
															expr("typeKind").build()
													)),
													action(
															listOf(
																	stmt("ret = append(ret, member);").build()
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal(null, "SEMICOLON"),
											action(
													listOf(
															stmt("ret = dress(SEmptyMemberDecl.make());").build()
													)
											)
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default)) && typeKind != TypeKind.Interface) problem = new BUProblem(Severity.ERROR, \"Only interfaces can have default members\");\n" + "").build()
													)
											),
											choice(
													sequence(
															nonTerminal("ret", "InitializerDecl", null, listOf(
																	expr("modifiers").build()
															)),
															action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));\n" + "").build()
																	)
															)
													),
													nonTerminal("ret", "ClassOrInterfaceDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "EnumDecl", null, listOf(
															expr("modifiers").build()
													)),
													nonTerminal("ret", "AnnotationTypeDecl", null, listOf(
															expr("modifiers").build()
													)),
													sequence(
															lookAhead(
																	zeroOrOne(
																			nonTerminal(null, "TypeParameters")
																	),
																	nonTerminal(null, "Name"),
																	terminal(null, "LPAREN")
															),
															nonTerminal("ret", "ConstructorDecl", null, listOf(
																	expr("modifiers").build()
															)),
															action(
																	listOf(
																			stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));\n" + "").build()
																	)
															)
													),
													sequence(
															lookAhead(
																	nonTerminal(null, "Type"),
																	nonTerminal(null, "Name"),
																	zeroOrMore(
																			terminal(null, "LBRACKET"),
																			terminal(null, "RBRACKET")
																	),
																	choice(
																			terminal(null, "COMMA"),
																			terminal(null, "ASSIGN"),
																			terminal(null, "SEMICOLON")
																	)
															),
															nonTerminal("ret", "FieldDecl", null, listOf(
																	expr("modifiers").build()
															))
													),
													nonTerminal("ret", "MethodDecl", null, listOf(
															expr("modifiers").build()
													))
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							nonTerminal("variables", "VariableDeclarators"),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							nonTerminal("variables", "VariableDeclarators"),
							action(
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
					sequence(
							nonTerminal("val", "VariableDeclarator"),
							action(
									listOf(
											stmt("variables = append(variables, val);").build()
									)
							),
							zeroOrMore(
									terminal(null, "COMMA"),
									nonTerminal("val", "VariableDeclarator"),
									action(
											listOf(
													stmt("variables = append(variables, val);").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("id", "VariableDeclaratorId"),
							zeroOrOne(
									terminal(null, "ASSIGN"),
									nonTerminal("initExpr", "VariableInitializer"),
									action(
											listOf(
													stmt("init = optionOf(initExpr);").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("name", "Name"),
							nonTerminal("arrayDims", "ArrayDims"),
							action(
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
					sequence(
							zeroOrMore(
									lookAhead(
											nonTerminal(null, "Annotations"),
											terminal(null, "LBRACKET"),
											terminal(null, "RBRACKET")
									),
									action(
											listOf(
													stmt("run();").build()
											)
									),
									nonTerminal("annotations", "Annotations"),
									terminal(null, "LBRACKET"),
									terminal(null, "RBRACKET"),
									action(
											listOf(
													stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
											)
									)
							),
							action(
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
					sequence(
							choice(
									nonTerminal("ret", "ArrayInitializer"),
									nonTerminal("ret", "Expression")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "LBRACE"),
							zeroOrOne(
									nonTerminal("val", "VariableInitializer"),
									action(
											listOf(
													stmt("values = append(values, val);").build()
											)
									),
									zeroOrMore(
											lookAhead(2),
											terminal(null, "COMMA"),
											nonTerminal("val", "VariableInitializer"),
											action(
													listOf(
															stmt("values = append(values, val);").build()
													)
											)
									)
							),
							zeroOrOne(
									terminal(null, "COMMA"),
									action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							zeroOrOne(
									nonTerminal("typeParameters", "TypeParameters")
							),
							nonTerminal("type", "ResultType"),
							nonTerminal("name", "Name"),
							nonTerminal("parameters", "FormalParameters"),
							nonTerminal("arrayDims", "ArrayDims"),
							zeroOrOne(
									nonTerminal("throwsClause", "ThrowsClause")
							),
							choice(
									nonTerminal("block", "Block"),
									sequence(
											terminal(null, "SEMICOLON"),
											action(
													listOf(
															stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default))) problem = new BUProblem(Severity.ERROR, \"Default methods must have a body\");\n" + "").build()
													)
											)
									)
							),
							action(
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
					sequence(
							terminal(null, "LPAREN"),
							zeroOrOne(
									nonTerminal("ret", "FormalParameterList")
							),
							terminal(null, "RPAREN"),
							action(
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
					sequence(
							choice(
									sequence(
											nonTerminal("par", "FormalParameter"),
											action(
													listOf(
															stmt("ret = append(ret, par);").build()
													)
											),
											zeroOrMore(
													terminal(null, "COMMA"),
													nonTerminal("par", "FormalParameter"),
													action(
															listOf(
																	stmt("ret = append(ret, par);").build()
															)
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							zeroOrOne(
									terminal(null, "ELLIPSIS"),
									action(
											listOf(
													stmt("isVarArg = true;").build()
											)
									)
							),
							nonTerminal("id", "VariableDeclaratorId"),
							action(
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
					sequence(
							terminal(null, "THROWS"),
							nonTerminal("cit", "AnnotatedQualifiedType"),
							action(
									listOf(
											stmt("ret = append(ret, cit);").build()
									)
							),
							zeroOrMore(
									terminal(null, "COMMA"),
									nonTerminal("cit", "AnnotatedQualifiedType"),
									action(
											listOf(
													stmt("ret = append(ret, cit);").build()
											)
									)
							),
							action(
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
					sequence(
							zeroOrOne(
									nonTerminal("typeParameters", "TypeParameters")
							),
							nonTerminal("name", "Name"),
							nonTerminal("parameters", "FormalParameters"),
							zeroOrOne(
									nonTerminal("throwsClause", "ThrowsClause")
							),
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "LBRACE"),
							zeroOrOne(
									choice(
											sequence(
													lookAhead(2),
													choice(
															sequence(
																	lookAhead(
																			nonTerminal(null, "ExplicitConstructorInvocation")
																	),
																	nonTerminal("stmt", "ExplicitConstructorInvocation"),
																	action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															),
															sequence(
																	lookAhead(2),
																	nonTerminal("stmt", "BlockStatement"),
																	action(
																			listOf(
																					stmt("stmts = append(stmts, stmt);").build()
																			)
																	)
															)
													),
													zeroOrMore(
															lookAhead(2),
															nonTerminal("stmt", "BlockStatement"),
															action(
																	listOf(
																			stmt("stmts = append(stmts, stmt);").build()
																	)
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("stmts", "NodeListVar")
											)
									)
							),
							terminal(null, "RBRACE"),
							action(
									listOf(
											stmt("block = dress(SBlockStmt.make(stmts));").build()
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											lookAhead(
													zeroOrOne(
															nonTerminal(null, "TypeArguments")
													),
													terminal(null, "THIS"),
													terminal(null, "LPAREN")
											),
											zeroOrOne(
													nonTerminal("typeArgs", "TypeArguments")
											),
											terminal(null, "THIS"),
											action(
													listOf(
															stmt("isThis = true;").build()
													)
											),
											nonTerminal("args", "Arguments"),
											terminal(null, "SEMICOLON")
									),
									sequence(
											zeroOrOne(
													lookAhead(
															nonTerminal(null, "PrimaryExpressionWithoutSuperSuffix"),
															terminal(null, "DOT")
													),
													nonTerminal("expr", "PrimaryExpressionWithoutSuperSuffix"),
													terminal(null, "DOT")
											),
											zeroOrOne(
													nonTerminal("typeArgs", "TypeArguments")
											),
											terminal(null, "SUPER"),
											nonTerminal("args", "Arguments"),
											terminal(null, "SEMICOLON")
									)
							),
							action(
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
					sequence(
							zeroOrOne(
									lookAhead(2),
									choice(
											oneOrMore(
													nonTerminal("stmt", "BlockStatement"),
													action(
															listOf(
																	stmt("ret = append(ret, stmt);").build()
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("block", "Block"),
							action(
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
					sequence(
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													lookAhead(
															nonTerminal(null, "Annotations"),
															terminal(null, "LBRACKET")
													),
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(
															listOf(
																	stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
															)
													)
											)
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													lookAhead(
															nonTerminal(null, "Annotations"),
															terminal(null, "LBRACKET")
													),
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											action(
													listOf(
															stmt("lateRun();").build()
													)
											),
											nonTerminal("arrayDims", "ArrayDimsMandatory"),
											action(
													listOf(
															stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
													)
											)
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													lookAhead(
															nonTerminal(null, "Annotations"),
															terminal(null, "LBRACKET")
													),
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							nonTerminal("name", "Name"),
							zeroOrOne(
									lookAhead(2),
									nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
							),
							action(
									listOf(
											stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
									)
							),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "DOT"),
									action(
											listOf(
													stmt("scope = optionOf(ret);").build()
											)
									),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("name", "Name"),
									zeroOrOne(
											lookAhead(2),
											nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
									),
									action(
											listOf(
													stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
											)
									)
							),
							action(
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
					sequence(
							terminal(null, "LT"),
							zeroOrOne(
									nonTerminal("ret", "TypeArgumentList")
							),
							terminal(null, "GT"),
							action(
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
					sequence(
							terminal(null, "LT"),
							zeroOrOne(
									nonTerminal("ret", "TypeArgumentList")
							),
							terminal(null, "GT"),
							action(
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
							stmt("BUTree<? extends SType> type;").build(),
							stmt("Token id;").build()
					),
					choice(
							sequence(
									nonTerminal("type", "TypeArgument"),
									action(
											listOf(
													stmt("ret = append(ret, type);").build()
											)
									),
									zeroOrMore(
											terminal(null, "COMMA"),
											nonTerminal("type", "TypeArgument"),
											action(
													listOf(
															stmt("ret = append(ret, type);").build()
													)
											)
									),
									action(
											listOf(
													stmt("return ret;").build()
											)
									)
							),
							sequence(
									lookAhead(
											expr("quotesMode").build()
									),
									terminal("id", "NODE_LIST_VARIABLE"),
									action(
											listOf(
													stmt("return makeVar(id);").build()
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							choice(
									nonTerminal("ret", "ReferenceType", null, listOf(
											expr("annotations").build()
									)),
									nonTerminal("ret", "Wildcard", null, listOf(
											expr("annotations").build()
									))
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							terminal(null, "HOOK"),
							zeroOrOne(
									choice(
											sequence(
													terminal(null, "EXTENDS"),
													action(
															listOf(
																	stmt("run();").build()
															)
													),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("ext", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											),
											sequence(
													terminal(null, "SUPER"),
													action(
															listOf(
																	stmt("run();").build()
															)
													),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("sup", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
									)
							),
							choice(
									sequence(
											terminal(null, "BOOLEAN"),
											action(
													listOf(
															stmt("primitive = Primitive.Boolean;").build()
													)
											)
									),
									sequence(
											terminal(null, "CHAR"),
											action(
													listOf(
															stmt("primitive = Primitive.Char;").build()
													)
											)
									),
									sequence(
											terminal(null, "BYTE"),
											action(
													listOf(
															stmt("primitive = Primitive.Byte;").build()
													)
											)
									),
									sequence(
											terminal(null, "SHORT"),
											action(
													listOf(
															stmt("primitive = Primitive.Short;").build()
													)
											)
									),
									sequence(
											terminal(null, "INT"),
											action(
													listOf(
															stmt("primitive = Primitive.Int;").build()
													)
											)
									),
									sequence(
											terminal(null, "LONG"),
											action(
													listOf(
															stmt("primitive = Primitive.Long;").build()
													)
											)
									),
									sequence(
											terminal(null, "FLOAT"),
											action(
													listOf(
															stmt("primitive = Primitive.Float;").build()
													)
											)
									),
									sequence(
											terminal(null, "DOUBLE"),
											action(
													listOf(
															stmt("primitive = Primitive.Double;").build()
													)
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "VOID"),
											action(
													listOf(
															stmt("ret = dress(SVoidType.make());").build()
													)
											)
									),
									nonTerminal("ret", "Type", null, listOf(
											expr("null").build()
									))
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "QualifiedType", null, listOf(
									expr("annotations").build()
							)),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("name", "Name"),
							action(
									listOf(
											stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
									)
							),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "DOT"),
									action(
											listOf(
													stmt("qualifier = optionOf(ret);").build()
											)
									),
									nonTerminal("name", "Name"),
									action(
											listOf(
													stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal("id", "IDENTIFIER"),
											action(
													listOf(
															stmt("name = dress(SName.make(id.image));").build()
													)
											)
									),
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("name", "NodeVar")
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(
													nonTerminal(null, "Name"),
													terminal(null, "ARROW")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("ret", "Name"),
											terminal(null, "ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
													expr("false").build()
											))
									),
									sequence(
											lookAhead(
													terminal(null, "LPAREN"),
													terminal(null, "RPAREN"),
													terminal(null, "ARROW")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "LPAREN"),
											terminal(null, "RPAREN"),
											terminal(null, "ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("emptyList()").build(),
													expr("true").build()
											))
									),
									sequence(
											lookAhead(
													terminal(null, "LPAREN"),
													nonTerminal(null, "Name"),
													terminal(null, "RPAREN"),
													terminal(null, "ARROW")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "LPAREN"),
											nonTerminal("ret", "Name"),
											terminal(null, "RPAREN"),
											terminal(null, "ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
													expr("true").build()
											))
									),
									sequence(
											lookAhead(
													terminal(null, "LPAREN"),
													nonTerminal(null, "Name"),
													terminal(null, "COMMA")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "LPAREN"),
											nonTerminal("params", "InferredFormalParameterList"),
											terminal(null, "RPAREN"),
											terminal(null, "ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("params").build(),
													expr("true").build()
											))
									),
									sequence(
											nonTerminal("ret", "ConditionalExpression"),
											zeroOrOne(
													lookAhead(2),
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													nonTerminal("op", "AssignmentOperator"),
													nonTerminal("value", "Expression"),
													action(
															listOf(
																	stmt("ret = dress(SAssignExpr.make(ret, op, value));").build()
															)
													)
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											nonTerminal("expr", "Expression"),
											action(
													listOf(
															stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, left(expr)));").build()
													)
											)
									),
									sequence(
											nonTerminal("block", "Block"),
											action(
													listOf(
															stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, right(block)));").build()
													)
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("param", "InferredFormalParameter"),
							action(
									listOf(
											stmt("ret = append(ret, param);").build()
									)
							),
							zeroOrMore(
									terminal(null, "COMMA"),
									nonTerminal("param", "InferredFormalParameter"),
									action(
											listOf(
													stmt("ret = append(ret, param);").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("name", "Name"),
							action(
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
					sequence(
							choice(
									sequence(
											terminal(null, "ASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Normal;").build()
													)
											)
									),
									sequence(
											terminal(null, "STARASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Times;").build()
													)
											)
									),
									sequence(
											terminal(null, "SLASHASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Divide;").build()
													)
											)
									),
									sequence(
											terminal(null, "REMASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Remainder;").build()
													)
											)
									),
									sequence(
											terminal(null, "PLUSASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Plus;").build()
													)
											)
									),
									sequence(
											terminal(null, "MINUSASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Minus;").build()
													)
											)
									),
									sequence(
											terminal(null, "LSHIFTASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.LeftShift;").build()
													)
											)
									),
									sequence(
											terminal(null, "RSIGNEDSHIFTASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.RightSignedShift;").build()
													)
											)
									),
									sequence(
											terminal(null, "RUNSIGNEDSHIFTASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.RightUnsignedShift;").build()
													)
											)
									),
									sequence(
											terminal(null, "ANDASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.And;").build()
													)
											)
									),
									sequence(
											terminal(null, "XORASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.XOr;").build()
													)
											)
									),
									sequence(
											terminal(null, "ORASSIGN"),
											action(
													listOf(
															stmt("ret = AssignOp.Or;").build()
													)
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "ConditionalOrExpression"),
							zeroOrOne(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "HOOK"),
									nonTerminal("left", "Expression"),
									terminal(null, "COLON"),
									nonTerminal("right", "ConditionalExpression"),
									action(
											listOf(
													stmt("ret = dress(SConditionalExpr.make(ret, left, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "ConditionalAndExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "SC_OR"),
									nonTerminal("right", "ConditionalAndExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.Or, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "InclusiveOrExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "SC_AND"),
									nonTerminal("right", "InclusiveOrExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.And, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "ExclusiveOrExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "BIT_OR"),
									nonTerminal("right", "ExclusiveOrExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinOr, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "AndExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "XOR"),
									nonTerminal("right", "AndExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.XOr, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "EqualityExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "BIT_AND"),
									nonTerminal("right", "EqualityExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinAnd, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "InstanceOfExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "EQ"),
													action(
															listOf(
																	stmt("op = BinaryOp.Equal;").build()
															)
													)
											),
											sequence(
													terminal(null, "NE"),
													action(
															listOf(
																	stmt("op = BinaryOp.NotEqual;").build()
															)
													)
											)
									),
									nonTerminal("right", "InstanceOfExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "RelationalExpression"),
							zeroOrOne(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									terminal(null, "INSTANCEOF"),
									action(
											listOf(
													stmt("run();").build()
											)
									),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("type", "Type", null, listOf(
											expr("annotations").build()
									)),
									action(
											listOf(
													stmt("ret = dress(SInstanceOfExpr.make(ret, type));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "ShiftExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "LT"),
													action(
															listOf(
																	stmt("op = BinaryOp.Less;").build()
															)
													)
											),
											sequence(
													terminal(null, "GT"),
													action(
															listOf(
																	stmt("op = BinaryOp.Greater;").build()
															)
													)
											),
											sequence(
													terminal(null, "LE"),
													action(
															listOf(
																	stmt("op = BinaryOp.LessOrEqual;").build()
															)
													)
											),
											sequence(
													terminal(null, "GE"),
													action(
															listOf(
																	stmt("op = BinaryOp.GreaterOrEqual;").build()
															)
													)
											)
									),
									nonTerminal("right", "ShiftExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "AdditiveExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "LSHIFT"),
													action(
															listOf(
																	stmt("op = BinaryOp.LeftShift;").build()
															)
													)
											),
											sequence(
													lookAhead(3),
													nonTerminal(null, "RUNSIGNEDSHIFT"),
													action(
															listOf(
																	stmt("op = BinaryOp.RightUnsignedShift;").build()
															)
													)
											),
											sequence(
													lookAhead(2),
													nonTerminal(null, "RSIGNEDSHIFT"),
													action(
															listOf(
																	stmt("op = BinaryOp.RightSignedShift;").build()
															)
													)
											)
									),
									nonTerminal("right", "AdditiveExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "MultiplicativeExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "PLUS"),
													action(
															listOf(
																	stmt("op = BinaryOp.Plus;").build()
															)
													)
											),
											sequence(
													terminal(null, "MINUS"),
													action(
															listOf(
																	stmt("op = BinaryOp.Minus;").build()
															)
													)
											)
									),
									nonTerminal("right", "MultiplicativeExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "UnaryExpression"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "STAR"),
													action(
															listOf(
																	stmt("op = BinaryOp.Times;").build()
															)
													)
											),
											sequence(
													terminal(null, "SLASH"),
													action(
															listOf(
																	stmt("op = BinaryOp.Divide;").build()
															)
													)
											),
											sequence(
													terminal(null, "REM"),
													action(
															listOf(
																	stmt("op = BinaryOp.Remainder;").build()
															)
													)
											)
									),
									nonTerminal("right", "UnaryExpression"),
									action(
											listOf(
													stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
											)
									)
							),
							action(
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
					sequence(
							choice(
									nonTerminal("ret", "PrefixExpression"),
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											choice(
													sequence(
															terminal(null, "PLUS"),
															action(
																	listOf(
																			stmt("op = UnaryOp.Positive;").build()
																	)
															)
													),
													sequence(
															terminal(null, "MINUS"),
															action(
																	listOf(
																			stmt("op = UnaryOp.Negative;").build()
																	)
															)
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									nonTerminal("ret", "UnaryExpressionNotPlusMinus")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal(null, "INCR"),
											action(
													listOf(
															stmt("op = UnaryOp.PreIncrement;").build()
													)
											)
									),
									sequence(
											terminal(null, "DECR"),
											action(
													listOf(
															stmt("op = UnaryOp.PreDecrement;").build()
													)
											)
									)
							),
							nonTerminal("ret", "UnaryExpression"),
							action(
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
					sequence(
							choice(
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											choice(
													sequence(
															terminal(null, "TILDE"),
															action(
																	listOf(
																			stmt("op = UnaryOp.Inverse;").build()
																	)
															)
													),
													sequence(
															terminal(null, "BANG"),
															action(
																	listOf(
																			stmt("op = UnaryOp.Not;").build()
																	)
															)
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action(
													listOf(
															stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
													)
											)
									),
									sequence(
											lookAhead(
													nonTerminal(null, "CastExpression")
											),
											nonTerminal("ret", "CastExpression")
									),
									nonTerminal("ret", "PostfixExpression")
							),
							action(
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
					sequence(
							nonTerminal("ret", "PrimaryExpression"),
							zeroOrOne(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									choice(
											sequence(
													terminal(null, "INCR"),
													action(
															listOf(
																	stmt("op = UnaryOp.PostIncrement;").build()
															)
													)
											),
											sequence(
													terminal(null, "DECR"),
													action(
															listOf(
																	stmt("op = UnaryOp.PostDecrement;").build()
															)
													)
											)
									),
									action(
											listOf(
													stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "LPAREN"),
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											choice(
													sequence(
															terminal(null, "RPAREN"),
															nonTerminal("ret", "UnaryExpression"),
															action(
																	listOf(
																			stmt("ret = dress(SCastExpr.make(primitiveType, ret));").build()
																	)
															)
													),
													sequence(
															action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															nonTerminal("arrayDims", "ArrayDimsMandatory"),
															action(
																	listOf(
																			stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
																	)
															),
															nonTerminal("type", "ReferenceCastTypeRest", null, listOf(
																	expr("type").build()
															)),
															terminal(null, "RPAREN"),
															nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
															action(
																	listOf(
																			stmt("ret = dress(SCastExpr.make(type, ret));").build()
																	)
															)
													)
											)
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													lookAhead(
															nonTerminal(null, "Annotations"),
															terminal(null, "LBRACKET")
													),
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(
															listOf(
																	stmt("type = dress(SArrayType.make(type, arrayDims));").build()
															)
													)
											),
											nonTerminal("type", "ReferenceCastTypeRest", null, listOf(
													expr("type").build()
											)),
											terminal(null, "RPAREN"),
											nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
											action(
													listOf(
															stmt("ret = dress(SCastExpr.make(type, ret));").build()
													)
											)
									)
							),
							action(
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
					sequence(
							zeroOrOne(
									lookAhead(
											terminal(null, "BIT_AND")
									),
									action(
											listOf(
													stmt("types = append(types, type);").build()
											)
									),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									oneOrMore(
											terminal(null, "BIT_AND"),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											action(
													listOf(
															stmt("types = append(types, type);").build()
													)
											)
									),
									action(
											listOf(
													stmt("type = dress(SIntersectionType.make(types));").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal("literal", "INTEGER_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Integer.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "LONG_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Long.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "FLOAT_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Float.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "DOUBLE_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Double.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "CHARACTER_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Character.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "STRING_LITERAL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(String.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "TRUE"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "FALSE"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
													)
											)
									),
									sequence(
											terminal("literal", "NULL"),
											action(
													listOf(
															stmt("ret = SLiteralExpr.make(Void.class, literal.image);").build()
													)
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									lookAhead(2),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									nonTerminal("ret", "PrimarySuffix", null, listOf(
											expr("ret").build()
									))
							),
							action(
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
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									lookAhead(
											nonTerminal(null, "PrimarySuffixWithoutSuper")
									),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									nonTerminal("ret", "PrimarySuffixWithoutSuper", null, listOf(
											expr("ret").build()
									))
							),
							action(
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
					sequence(
							choice(
									nonTerminal("ret", "Literal"),
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "THIS"),
											action(
													listOf(
															stmt("ret = dress(SThisExpr.make(none()));").build()
													)
											)
									),
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "SUPER"),
											action(
													listOf(
															stmt("ret = dress(SSuperExpr.make(none()));").build()
													)
											),
											choice(
													sequence(
															action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															terminal(null, "DOT"),
															choice(
																	sequence(
																			lookAhead(
																					zeroOrOne(
																							nonTerminal(null, "TypeArguments")
																					),
																					nonTerminal(null, "Name"),
																					terminal(null, "LPAREN")
																			),
																			nonTerminal("ret", "MethodInvocation", null, listOf(
																					expr("ret").build()
																			))
																	),
																	nonTerminal("ret", "FieldAccess", null, listOf(
																			expr("ret").build()
																	))
															)
													),
													sequence(
															action(
																	listOf(
																			stmt("lateRun();").build()
																	)
															),
															nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
																	expr("ret").build()
															))
													)
											)
									),
									nonTerminal("ret", "AllocationExpression", null, listOf(
											expr("null").build()
									)),
									sequence(
											lookAhead(
													nonTerminal(null, "ResultType"),
													terminal(null, "DOT"),
													terminal(null, "CLASS")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("type", "ResultType"),
											terminal(null, "DOT"),
											terminal(null, "CLASS"),
											action(
													listOf(
															stmt("ret = dress(SClassExpr.make(type));").build()
													)
											)
									),
									sequence(
											lookAhead(
													nonTerminal(null, "ResultType"),
													terminal(null, "DOUBLECOLON")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("type", "ResultType"),
											action(
													listOf(
															stmt("ret = STypeExpr.make(type);").build()
													)
											),
											nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
													expr("ret").build()
											))
									),
									sequence(
											lookAhead(
													zeroOrOne(
															nonTerminal(null, "TypeArguments")
													),
													nonTerminal(null, "Name"),
													terminal(null, "LPAREN")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("ret", "MethodInvocation", null, listOf(
													expr("null").build()
											))
									),
									sequence(
											nonTerminal("ret", "Name"),
											zeroOrOne(
													action(
															listOf(
																	stmt("lateRun();").build()
															)
													),
													terminal(null, "ARROW"),
													nonTerminal("ret", "LambdaBody", null, listOf(
															expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
															expr("false").build()
													))
											)
									),
									sequence(
											action(
													listOf(
															stmt("run();").build()
													)
											),
											terminal(null, "LPAREN"),
											choice(
													sequence(
															terminal(null, "RPAREN"),
															terminal(null, "ARROW"),
															nonTerminal("ret", "LambdaBody", null, listOf(
																	expr("emptyList()").build(),
																	expr("true").build()
															))
													),
													sequence(
															lookAhead(
																	nonTerminal(null, "Name"),
																	terminal(null, "RPAREN"),
																	terminal(null, "ARROW")
															),
															nonTerminal("ret", "Name"),
															terminal(null, "RPAREN"),
															terminal(null, "ARROW"),
															nonTerminal("ret", "LambdaBody", null, listOf(
																	expr("singletonList(makeFormalParameter((BUTree<SName>) ret))").build(),
																	expr("true").build()
															))
													),
													sequence(
															lookAhead(
																	nonTerminal(null, "Name"),
																	terminal(null, "COMMA")
															),
															nonTerminal("params", "InferredFormalParameterList"),
															terminal(null, "RPAREN"),
															terminal(null, "ARROW"),
															nonTerminal("ret", "LambdaBody", null, listOf(
																	expr("params").build(),
																	expr("true").build()
															))
													),
													sequence(
															lookAhead(
																	expr("isLambda()").build()
															),
															nonTerminal("params", "FormalParameterList"),
															terminal(null, "RPAREN"),
															terminal(null, "ARROW"),
															nonTerminal("ret", "LambdaBody", null, listOf(
																	expr("params").build(),
																	expr("true").build()
															))
													),
													sequence(
															nonTerminal("ret", "Expression"),
															terminal(null, "RPAREN"),
															action(
																	listOf(
																			stmt("ret = dress(SParenthesizedExpr.make(ret));").build()
																	)
															)
													)
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(2),
											nonTerminal("ret", "PrimarySuffixWithoutSuper", null, listOf(
													expr("scope").build()
											))
									),
									sequence(
											terminal(null, "DOT"),
											terminal(null, "SUPER"),
											action(
													listOf(
															stmt("ret = dress(SSuperExpr.make(optionOf(scope)));").build()
													)
											)
									),
									nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
											expr("scope").build()
									))
							),
							action(
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
					sequence(
							choice(
									sequence(
											terminal(null, "DOT"),
											choice(
													sequence(
															terminal(null, "THIS"),
															action(
																	listOf(
																			stmt("ret = dress(SThisExpr.make(optionOf(scope)));").build()
																	)
															)
													),
													nonTerminal("ret", "AllocationExpression", null, listOf(
															expr("scope").build()
													)),
													sequence(
															lookAhead(
																	zeroOrOne(
																			nonTerminal(null, "TypeArguments")
																	),
																	nonTerminal(null, "Name"),
																	terminal(null, "LPAREN")
															),
															nonTerminal("ret", "MethodInvocation", null, listOf(
																	expr("scope").build()
															))
													),
													nonTerminal("ret", "FieldAccess", null, listOf(
															expr("scope").build()
													))
											)
									),
									sequence(
											terminal(null, "LBRACKET"),
											nonTerminal("ret", "Expression"),
											terminal(null, "RBRACKET"),
											action(
													listOf(
															stmt("ret = dress(SArrayAccessExpr.make(scope, ret));").build()
													)
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("name", "Name"),
							action(
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
					sequence(
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							nonTerminal("name", "Name"),
							nonTerminal("args", "Arguments"),
							action(
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
					sequence(
							terminal(null, "LPAREN"),
							zeroOrOne(
									choice(
											sequence(
													lookAhead(1),
													nonTerminal("expr", "Expression"),
													action(
															listOf(
																	stmt("ret = append(ret, expr);").build()
															)
													),
													zeroOrMore(
															terminal(null, "COMMA"),
															nonTerminal("expr", "Expression"),
															action(
																	listOf(
																			stmt("ret = append(ret, expr);").build()
																	)
															)
													)
											),
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											)
									)
							),
							terminal(null, "RPAREN"),
							action(
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
					sequence(
							terminal(null, "DOUBLECOLON"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							choice(
									nonTerminal("name", "Name"),
									sequence(
											terminal(null, "NEW"),
											action(
													listOf(
															stmt("name = SName.make(\"new\");").build()
													)
											)
									)
							),
							action(
									listOf(
											stmt("ret = dress(SMethodReferenceExpr.make(scope, ensureNotNull(typeArgs), name));").build()
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("if (scope == null) run();\n" + "").build()
									)
							),
							terminal(null, "NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("annotations", "Annotations"),
							choice(
									sequence(
											nonTerminal("type", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											nonTerminal("ret", "ArrayCreationExpr", null, listOf(
													expr("type").build()
											))
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											choice(
													nonTerminal("ret", "ArrayCreationExpr", null, listOf(
															expr("type").build()
													)),
													sequence(
															nonTerminal("args", "Arguments"),
															zeroOrOne(
																	lookAhead(
																			terminal(null, "LBRACE")
																	),
																	nonTerminal("anonymousBody", "ClassOrInterfaceBody", null, listOf(
																			expr("TypeKind.Class").build()
																	))
															),
															action(
																	listOf(
																			stmt("ret = dress(SObjectCreationExpr.make(optionOf(scope), ensureNotNull(typeArgs), (BUTree<SQualifiedType>) type, args, optionOf(anonymousBody)));").build()
																	)
															)
													)
											)
									)
							),
							action(
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
					choice(
							sequence(
									lookAhead(
											nonTerminal(null, "Annotations"),
											terminal(null, "LBRACKET"),
											nonTerminal(null, "Expression"),
											terminal(null, "RBRACKET")
									),
									nonTerminal("arrayDimExprs", "ArrayDimExprsMandatory"),
									nonTerminal("arrayDims", "ArrayDims"),
									action(
											listOf(
													stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, none()));").build()
											)
									)
							),
							sequence(
									nonTerminal("arrayDims", "ArrayDimsMandatory"),
									nonTerminal("initializer", "ArrayInitializer"),
									action(
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
					sequence(
							oneOrMore(
									lookAhead(
											nonTerminal(null, "Annotations"),
											terminal(null, "LBRACKET"),
											nonTerminal(null, "Expression"),
											terminal(null, "RBRACKET")
									),
									action(
											listOf(
													stmt("run();").build()
											)
									),
									nonTerminal("annotations", "Annotations"),
									terminal(null, "LBRACKET"),
									nonTerminal("expr", "Expression"),
									terminal(null, "RBRACKET"),
									action(
											listOf(
													stmt("arrayDimExprs = append(arrayDimExprs, dress(SArrayDimExpr.make(annotations, expr)));").build()
											)
									)
							),
							action(
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
					sequence(
							oneOrMore(
									lookAhead(
											nonTerminal(null, "Annotations"),
											terminal(null, "LBRACKET"),
											terminal(null, "RBRACKET")
									),
									action(
											listOf(
													stmt("run();").build()
											)
									),
									nonTerminal("annotations", "Annotations"),
									terminal(null, "LBRACKET"),
									terminal(null, "RBRACKET"),
									action(
											listOf(
													stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(2),
											nonTerminal("ret", "LabeledStatement")
									),
									nonTerminal("ret", "AssertStatement"),
									nonTerminal("ret", "Block"),
									nonTerminal("ret", "EmptyStatement"),
									nonTerminal("ret", "StatementExpression"),
									nonTerminal("ret", "SwitchStatement"),
									nonTerminal("ret", "IfStatement"),
									nonTerminal("ret", "WhileStatement"),
									nonTerminal("ret", "DoStatement"),
									nonTerminal("ret", "ForStatement"),
									nonTerminal("ret", "BreakStatement"),
									nonTerminal("ret", "ContinueStatement"),
									nonTerminal("ret", "ReturnStatement"),
									nonTerminal("ret", "ThrowStatement"),
									nonTerminal("ret", "SynchronizedStatement"),
									nonTerminal("ret", "TryStatement")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "ASSERT"),
							nonTerminal("check", "Expression"),
							zeroOrOne(
									terminal(null, "COLON"),
									nonTerminal("msg", "Expression")
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("label", "Name"),
							terminal(null, "COLON"),
							nonTerminal("stmt", "Statement"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "LBRACE"),
							nonTerminal("stmts", "Statements"),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(
													nonTerminal(null, "ModifiersNoDefault"),
													choice(
															terminal(null, "CLASS"),
															terminal(null, "INTERFACE")
													)
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("modifiers", "ModifiersNoDefault"),
											nonTerminal("typeDecl", "ClassOrInterfaceDecl", null, listOf(
													expr("modifiers").build()
											)),
											action(
													listOf(
															stmt("ret = dress(STypeDeclarationStmt.make(typeDecl));").build()
													)
											)
									),
									sequence(
											lookAhead(
													nonTerminal(null, "VariableDeclExpression")
											),
											action(
													listOf(
															stmt("run();").build()
													)
											),
											nonTerminal("expr", "VariableDeclExpression"),
											terminal(null, "SEMICOLON"),
											action(
													listOf(
															stmt("ret = dress(SExpressionStmt.make(expr));").build()
													)
											)
									),
									nonTerminal("ret", "Statement")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("modifiers", "ModifiersNoDefault"),
							nonTerminal("variableDecl", "VariableDecl", null, listOf(
									expr("modifiers").build()
							)),
							action(
									listOf(
											stmt("return dress(SVariableDeclarationExpr.make(variableDecl));").build()
									)
							)
					)
			),
			new GProduction("EmptyStatement",
					(MethodDecl) memberDecl("BUTree<SEmptyStmt> EmptyStatement();").build(),
					emptyList(),
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									nonTerminal("expr", "PrefixExpression"),
									sequence(
											nonTerminal("expr", "PrimaryExpression"),
											zeroOrOne(
													choice(
															sequence(
																	action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	terminal(null, "INCR"),
																	action(
																			listOf(
																					stmt("expr = dress(SUnaryExpr.make(UnaryOp.PostIncrement, expr));").build()
																			)
																	)
															),
															sequence(
																	action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	terminal(null, "DECR"),
																	action(
																			listOf(
																					stmt("expr = dress(SUnaryExpr.make(UnaryOp.PostDecrement, expr));").build()
																			)
																	)
															),
															sequence(
																	action(
																			listOf(
																					stmt("lateRun();").build()
																			)
																	),
																	nonTerminal("op", "AssignmentOperator"),
																	nonTerminal("value", "Expression"),
																	action(
																			listOf(
																					stmt("expr = dress(SAssignExpr.make(expr, op, value));").build()
																			)
																	)
															)
													)
											)
									)
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "SWITCH"),
							terminal(null, "LPAREN"),
							nonTerminal("selector", "Expression"),
							terminal(null, "RPAREN"),
							terminal(null, "LBRACE"),
							zeroOrMore(
									nonTerminal("entry", "SwitchEntry"),
									action(
											listOf(
													stmt("entries = append(entries, entry);").build()
											)
									)
							),
							terminal(null, "RBRACE"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							choice(
									sequence(
											terminal(null, "CASE"),
											nonTerminal("label", "Expression")
									),
									terminal(null, "DEFAULT")
							),
							terminal(null, "COLON"),
							nonTerminal("stmts", "Statements"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "IF"),
							terminal(null, "LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal(null, "RPAREN"),
							nonTerminal("thenStmt", "Statement"),
							zeroOrOne(
									lookAhead(1),
									terminal(null, "ELSE"),
									nonTerminal("elseStmt", "Statement")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "WHILE"),
							terminal(null, "LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal(null, "RPAREN"),
							nonTerminal("body", "Statement"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "DO"),
							nonTerminal("body", "Statement"),
							terminal(null, "WHILE"),
							terminal(null, "LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal(null, "RPAREN"),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "FOR"),
							terminal(null, "LPAREN"),
							choice(
									sequence(
											lookAhead(
													nonTerminal(null, "VariableDeclExpression"),
													terminal(null, "COLON")
											),
											nonTerminal("varExpr", "VariableDeclExpression"),
											terminal(null, "COLON"),
											nonTerminal("expr", "Expression")
									),
									sequence(
											zeroOrOne(
													nonTerminal("init", "ForInit")
											),
											terminal(null, "SEMICOLON"),
											zeroOrOne(
													nonTerminal("expr", "Expression")
											),
											terminal(null, "SEMICOLON"),
											zeroOrOne(
													nonTerminal("update", "ForUpdate")
											)
									)
							),
							terminal(null, "RPAREN"),
							nonTerminal("body", "Statement"),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(
													nonTerminal(null, "Modifiers"),
													nonTerminal(null, "Type"),
													nonTerminal(null, "Name")
											),
											nonTerminal("expr", "VariableDeclExpression"),
											action(
													listOf(
															stmt("ret = emptyList();").build(),
															stmt("ret = append(ret, expr);").build()
													)
											)
									),
									nonTerminal("ret", "ExpressionList")
							),
							action(
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
					sequence(
							nonTerminal("expr", "Expression"),
							action(
									listOf(
											stmt("ret = append(ret, expr);").build()
									)
							),
							zeroOrMore(
									terminal(null, "COMMA"),
									nonTerminal("expr", "Expression"),
									action(
											listOf(
													stmt("ret = append(ret, expr);").build()
											)
									)
							),
							action(
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
					sequence(
							nonTerminal("ret", "ExpressionList"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "BREAK"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "CONTINUE"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "RETURN"),
							zeroOrOne(
									nonTerminal("expr", "Expression")
							),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "THROW"),
							nonTerminal("expr", "Expression"),
							terminal(null, "SEMICOLON"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "SYNCHRONIZED"),
							terminal(null, "LPAREN"),
							nonTerminal("expr", "Expression"),
							terminal(null, "RPAREN"),
							nonTerminal("block", "Block"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "TRY"),
							choice(
									sequence(
											nonTerminal("resources", "ResourceSpecification", null, listOf(
													expr("trailingSemiColon").build()
											)),
											nonTerminal("tryBlock", "Block"),
											zeroOrOne(
													nonTerminal("catchClauses", "CatchClauses")
											),
											zeroOrOne(
													terminal(null, "FINALLY"),
													nonTerminal("finallyBlock", "Block")
											)
									),
									sequence(
											nonTerminal("tryBlock", "Block"),
											choice(
													sequence(
															nonTerminal("catchClauses", "CatchClauses"),
															zeroOrOne(
																	terminal(null, "FINALLY"),
																	nonTerminal("finallyBlock", "Block")
															)
													),
													sequence(
															terminal(null, "FINALLY"),
															nonTerminal("finallyBlock", "Block")
													)
											)
									)
							),
							action(
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
					sequence(
							oneOrMore(
									nonTerminal("catchClause", "CatchClause"),
									action(
											listOf(
													stmt("catchClauses = append(catchClauses, catchClause);").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "CATCH"),
							terminal(null, "LPAREN"),
							nonTerminal("param", "CatchFormalParameter"),
							terminal(null, "RPAREN"),
							nonTerminal("catchBlock", "Block"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("exceptType", "QualifiedType", null, listOf(
									expr("null").build()
							)),
							action(
									listOf(
											stmt("exceptTypes = append(exceptTypes, exceptType);").build()
									)
							),
							zeroOrOne(
									lookAhead(
											terminal(null, "BIT_OR")
									),
									action(
											listOf(
													stmt("lateRun();").build()
											)
									),
									oneOrMore(
											terminal(null, "BIT_OR"),
											nonTerminal("exceptType", "AnnotatedQualifiedType"),
											action(
													listOf(
															stmt("exceptTypes = append(exceptTypes, exceptType);").build()
													)
											)
									),
									action(
											listOf(
													stmt("exceptType = dress(SUnionType.make(exceptTypes));").build()
											)
									)
							),
							nonTerminal("exceptId", "VariableDeclaratorId"),
							action(
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
					sequence(
							terminal(null, "LPAREN"),
							nonTerminal("var", "VariableDeclExpression"),
							action(
									listOf(
											stmt("vars = append(vars, var);").build()
									)
							),
							zeroOrMore(
									lookAhead(2),
									terminal(null, "SEMICOLON"),
									nonTerminal("var", "VariableDeclExpression"),
									action(
											listOf(
													stmt("vars = append(vars, var);").build()
											)
									)
							),
							zeroOrOne(
									lookAhead(2),
									terminal(null, "SEMICOLON"),
									action(
											listOf(
													stmt("trailingSemiColon.value = true;").build()
											)
									)
							),
							terminal(null, "RPAREN"),
							action(
									listOf(
											stmt("return vars;").build()
									)
							)
					)
			),
			new GProduction("RUNSIGNEDSHIFT",
					(MethodDecl) memberDecl("void RUNSIGNEDSHIFT();").build(),
					emptyList(),
					sequence(
							lookAhead(
									expr("getToken(1).kind == TokenType.GT && getToken(1).realKind == TokenType.RUNSIGNEDSHIFT").build()
							),
							terminal(null, "GT"),
							terminal(null, "GT"),
							terminal(null, "GT"),
							action(
									listOf(
											stmt("popNewWhitespaces(2);").build()
									)
							)
					)
			),
			new GProduction("RSIGNEDSHIFT",
					(MethodDecl) memberDecl("void RSIGNEDSHIFT();").build(),
					emptyList(),
					sequence(
							lookAhead(
									expr("getToken(1).kind == TokenType.GT && getToken(1).realKind == TokenType.RSIGNEDSHIFT").build()
							),
							terminal(null, "GT"),
							terminal(null, "GT"),
							action(
									listOf(
											stmt("popNewWhitespaces(1);").build()
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
					sequence(
							zeroOrMore(
									nonTerminal("annotation", "Annotation"),
									action(
											listOf(
													stmt("annotations = append(annotations, annotation);").build()
											)
									)
							),
							action(
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
					sequence(
							choice(
									sequence(
											lookAhead(
													terminal(null, "AT"),
													nonTerminal(null, "QualifiedName"),
													terminal(null, "LPAREN"),
													choice(
															sequence(
																	nonTerminal(null, "Name"),
																	terminal(null, "ASSIGN")
															),
															terminal(null, "RPAREN")
													)
											),
											nonTerminal("ret", "NormalAnnotation")
									),
									sequence(
											lookAhead(
													terminal(null, "AT"),
													nonTerminal(null, "QualifiedName"),
													terminal(null, "LPAREN")
											),
											nonTerminal("ret", "SingleMemberAnnotation")
									),
									sequence(
											lookAhead(
													terminal(null, "AT"),
													nonTerminal(null, "QualifiedName")
											),
											nonTerminal("ret", "MarkerAnnotation")
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "AT"),
							nonTerminal("name", "QualifiedName"),
							terminal(null, "LPAREN"),
							zeroOrOne(
									nonTerminal("pairs", "MemberValuePairs")
							),
							terminal(null, "RPAREN"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "AT"),
							nonTerminal("name", "QualifiedName"),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "AT"),
							nonTerminal("name", "QualifiedName"),
							terminal(null, "LPAREN"),
							nonTerminal("memberVal", "MemberValue"),
							terminal(null, "RPAREN"),
							action(
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
					sequence(
							nonTerminal("pair", "MemberValuePair"),
							action(
									listOf(
											stmt("ret = append(ret, pair);").build()
									)
							),
							zeroOrMore(
									terminal(null, "COMMA"),
									nonTerminal("pair", "MemberValuePair"),
									action(
											listOf(
													stmt("ret = append(ret, pair);").build()
											)
									)
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							nonTerminal("name", "Name"),
							terminal(null, "ASSIGN"),
							nonTerminal("value", "MemberValue"),
							action(
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
					sequence(
							choice(
									nonTerminal("ret", "Annotation"),
									nonTerminal("ret", "MemberValueArrayInitializer"),
									nonTerminal("ret", "ConditionalExpression")
							),
							action(
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
					sequence(
							action(
									listOf(
											stmt("run();").build()
									)
							),
							terminal(null, "LBRACE"),
							zeroOrOne(
									nonTerminal("member", "MemberValue"),
									action(
											listOf(
													stmt("ret = append(ret, member);").build()
											)
									),
									zeroOrMore(
											lookAhead(2),
											terminal(null, "COMMA"),
											nonTerminal("member", "MemberValue"),
											action(
													listOf(
															stmt("ret = append(ret, member);").build()
													)
											)
									)
							),
							zeroOrOne(
									terminal(null, "COMMA"),
									action(
											listOf(
													stmt("trailingComma = true;").build()
											)
									)
							),
							terminal(null, "RBRACE"),
							action(
									listOf(
											stmt("return dress(SArrayInitializerExpr.make(ret, trailingComma));").build()
									)
							)
					)
			)
	);
}
