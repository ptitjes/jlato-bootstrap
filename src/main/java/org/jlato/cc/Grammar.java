package org.jlato.cc;

import org.jlato.cc.grammar.GProductions;

import static org.jlato.pattern.Quotes.expr;
import static org.jlato.pattern.Quotes.param;
import static org.jlato.pattern.Quotes.stmt;
import static org.jlato.pattern.Quotes.type;
import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.listOf;
import static org.jlato.cc.grammar.GExpansion.*;
import static org.jlato.cc.grammar.GProduction.*;

public class Grammar {

	public static GProductions productions = new GProductions(

			// Entry productions

			production("CompilationUnitEntry", type("BUTree<SCompilationUnit>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SCompilationUnit> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.COMPILATION_UNIT_ENTRY;").build()
							)),
							nonTerminal("ret", "CompilationUnit"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PackageDeclEntry", type("BUTree<SPackageDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SPackageDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.PACKAGE_DECL_ENTRY;").build()
							)),
							nonTerminal("ret", "PackageDecl"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("ImportDeclEntry", type("BUTree<SImportDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SImportDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.IMPORT_DECL_ENTRY;").build()
							)),
							nonTerminal("ret", "ImportDecl"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("TypeDeclEntry", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends STypeDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.TYPE_DECL_ENTRY;").build()
							)),
							nonTerminal("ret", "TypeDecl"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("MemberDeclEntry", type("BUTree<? extends SMemberDecl>").build(),
					listOf(param("TypeKind typeKind").build()),
					emptyList(),
					listOf(stmt("BUTree<? extends SMemberDecl> ret;").build()),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.MEMBER_DECL_ENTRY;").build()
							)),
							nonTerminal("ret", "ClassOrInterfaceBodyDecl", listOf(expr("typeKind").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("AnnotationMemberDeclEntry", type("BUTree<? extends SMemberDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SMemberDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.ANNOTATION_MEMBER_DECL_ENTRY;").build()
							)),
							// TODO Rename AnnotationMemberDecl
							nonTerminal("ret", "AnnotationTypeBodyDecl"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),

			production("ModifiersEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.MODIFIERS_ENTRY;").build()
							)),
							nonTerminal("ret", "Modifiers"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AnnotationsEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.ANNOTATIONS_ENTRY;").build()
							)),
							nonTerminal("ret", "Annotations"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),

			production("MethodDeclEntry", type("BUTree<SMethodDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<SMethodDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.METHOD_DECL_ENTRY;").build()
							)),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "MethodDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("FieldDeclEntry", type("BUTree<SFieldDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<SFieldDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.FIELD_DECL_ENTRY;").build()
							)),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "FieldDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("AnnotationElementDeclEntry", type("BUTree<SAnnotationMemberDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<SAnnotationMemberDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.ANNOTATION_ELEMENT_DECL_ENTRY;").build()
							)),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "Modifiers"),
							// TODO Rename AnnotationElementDecl
							nonTerminal("ret", "AnnotationTypeMemberDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),

			production("EnumConstantDeclEntry", type("BUTree<SEnumConstantDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SEnumConstantDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.ENUM_CONSTANT_DECL_ENTRY;").build()
							)),
							nonTerminal("ret", "EnumConstantDecl"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("FormalParameterEntry", type("BUTree<SFormalParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SFormalParameter> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.FORMAL_PARAMETER_ENTRY;").build()
							)),
							nonTerminal("ret", "FormalParameter"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("TypeParameterEntry", type("BUTree<STypeParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<STypeParameter> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.TYPE_PARAMETER_ENTRY;").build()
							)),
							nonTerminal("ret", "TypeParameter"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("StatementsEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.STATEMENTS_ENTRY;").build()
							)),
							nonTerminal("ret", "Statements", listOf(expr("false").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("BlockStatementEntry", type("BUTree<? extends SStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.BLOCK_STATEMENT_ENTRY;").build()
							)),
							nonTerminal("ret", "BlockStatement"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("ExpressionEntry", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.EXPRESSION_ENTRY;").build()
							)),
							nonTerminal("ret", "Expression"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),


			production("TypeEntry", type("BUTree<? extends SType>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SType> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.TYPE_ENTRY;").build()
							)),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "Type", listOf(expr("annotations").build())),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),

			production("QualifiedNameEntry", type("BUTree<SQualifiedName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.QUALIFIED_NAME_ENTRY;").build()
							)),
							nonTerminal("ret", "QualifiedName"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),
			production("NameEntry", type("BUTree<SName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("entryPoint = JavaGrammar.NAME_ENTRY;").build()
							)),
							nonTerminal("ret", "Name"),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(ret);").build()
							))
					)
			),

			production("Epilog", null,
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							terminal("EOF")
					)
			),

			// Main productions

			production("NodeListVar", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("Token id;").build()
					),
					sequence(
							terminal("id", "NODE_LIST_VARIABLE"),
							action(listOf(
									stmt("return makeVar(id);").build()
							))
					)
			),
			production("NodeVar", type("BUTree<SName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("Token id;").build()
					),
					sequence(
							terminal("id", "NODE_VARIABLE"),
							action(listOf(
									stmt("return makeVar(id);").build()
							))
					)
			),
			production("CompilationUnit", type("BUTree<SCompilationUnit>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SPackageDecl> packageDecl = null;").build(),
							stmt("BUTree<SNodeList> imports;").build(),
							stmt("BUTree<SNodeList> types;").build(),
							stmt("BUTree<SCompilationUnit> compilationUnit;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							zeroOrOne(
									nonTerminal("packageDecl", "PackageDecl")
							),
							nonTerminal("imports", "ImportDecls"),
							nonTerminal("types", "TypeDecls"),
							action(listOf(
									stmt("compilationUnit = dress(SCompilationUnit.make(packageDecl, imports, types));").build()
							)),
							nonTerminal("Epilog"),
							action(listOf(
									stmt("return dressWithPrologAndEpilog(compilationUnit);").build()
							))
					)
			),
			production("PackageDecl", type("BUTree<SPackageDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SQualifiedName> name;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							terminal("PACKAGE"),
							nonTerminal("name", "QualifiedName"),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SPackageDecl.make(annotations, name));").build()
							))
					)
			),
			production("ImportDecls", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> imports = emptyList();").build(),
							stmt("BUTree<SImportDecl> importDecl = null;").build()
					),
					sequence(
							zeroOrMore(
									nonTerminal("importDecl", "ImportDecl"),
									action(listOf(
											stmt("imports = append(imports, importDecl);").build()
									))
							),
							action(listOf(
									stmt("return imports;").build()
							))
					)
			),
			production("ImportDecl", type("BUTree<SImportDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("boolean isStatic = false;").build(),
							stmt("boolean isAsterisk = false;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("IMPORT"),
							zeroOrOne(
									terminal("STATIC"),
									action(listOf(
											stmt("isStatic = true;").build()
									))
							),
							nonTerminal("name", "QualifiedName"),
							zeroOrOne(
									terminal("DOT"),
									terminal("STAR"),
									action(listOf(
											stmt("isAsterisk = true;").build()
									))
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SImportDecl.make(name, isStatic, isAsterisk));").build()
							))
					)
			),
			production("TypeDecls", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> types = emptyList();").build(),
							stmt("BUTree<? extends STypeDecl> typeDecl = null;").build()
					),
					sequence(
							zeroOrMore(
									nonTerminal("typeDecl", "TypeDecl"),
									action(listOf(
											stmt("types = append(types, typeDecl);").build()
									))
							),
							action(listOf(
									stmt("return types;").build()
							))
					)
			),
			production("Modifiers", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> ann;").build()
					),
					sequence(
							zeroOrMore(
									choice(
											sequence(
													terminal("PUBLIC"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
													))
											),
											sequence(
													terminal("PROTECTED"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
													))
											),
											sequence(
													terminal("PRIVATE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
													))
											),
											sequence(
													terminal("ABSTRACT"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
													))
											),
											sequence(
													terminal("DEFAULT"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Default));").build()
													))
											),
											sequence(
													terminal("STATIC"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
													))
											),
											sequence(
													terminal("FINAL"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
													))
											),
											sequence(
													terminal("TRANSIENT"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
													))
											),
											sequence(
													terminal("VOLATILE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
													))
											),
											sequence(
													terminal("SYNCHRONIZED"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
													))
											),
											sequence(
													terminal("NATIVE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
													))
											),
											sequence(
													terminal("STRICTFP"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
													))
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action(listOf(
															stmt("modifiers = append(modifiers, ann);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return modifiers;").build()
							))
					)
			).memoizeMatches(),
			production("ModifiersNoDefault", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> ann;").build()
					),
					sequence(
							zeroOrMore(
									choice(
											sequence(
													terminal("PUBLIC"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));").build()
													))
											),
											sequence(
													terminal("PROTECTED"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));").build()
													))
											),
											sequence(
													terminal("PRIVATE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));").build()
													))
											),
											sequence(
													terminal("ABSTRACT"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));").build()
													))
											),
											sequence(
													terminal("STATIC"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));").build()
													))
											),
											sequence(
													terminal("FINAL"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));").build()
													))
											),
											sequence(
													terminal("TRANSIENT"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));").build()
													))
											),
											sequence(
													terminal("VOLATILE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));").build()
													))
											),
											sequence(
													terminal("SYNCHRONIZED"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));").build()
													))
											),
											sequence(
													terminal("NATIVE"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));").build()
													))
											),
											sequence(
													terminal("STRICTFP"),
													action(listOf(
															stmt("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));").build()
													))
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action(listOf(
															stmt("modifiers = append(modifiers, ann);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return modifiers;").build()
							))
					)
			).memoizeMatches(),
			production("TypeDecl", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends STypeDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action(listOf(
													stmt("ret = dress(SEmptyTypeDecl.make());").build()
											))
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
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ClassOrInterfaceDecl", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
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
											terminal("CLASS"),
											action(listOf(
													stmt("typeKind = TypeKind.Class;").build()
											)),
											nonTerminal("name", "Name"),
											zeroOrOne(
													nonTerminal("typeParams", "TypeParameters")
											),
											zeroOrOne(
													terminal("EXTENDS"),
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
											terminal("INTERFACE"),
											action(listOf(
													stmt("typeKind = TypeKind.Interface;").build()
											)),
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
							action(listOf(
									stmt("if (typeKind == TypeKind.Interface)\n" + "\treturn dress(SInterfaceDecl.make(modifiers, name, ensureNotNull(typeParams), ensureNotNull(extendsClause), members)).withProblem(problem.value);\n" + "else {\n" + "\treturn dress(SClassDecl.make(modifiers, name, ensureNotNull(typeParams), optionOf(superClassType), ensureNotNull(implementsClause), members));\n" + "}").build()
							))
					)
			),
			production("ExtendsList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							terminal("EXTENDS"),
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(listOf(
													stmt("ret = append(ret, cit);").build()
											)),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(listOf(
															stmt("ret = append(ret, cit);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ImplementsList", type("BUTree<SNodeList>").build(),
					emptyList(),
					listOf(
							param("TypeKind typeKind").build(),
							param("ByRef<BUProblem> problem").build()
					),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							terminal("IMPLEMENTS"),
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(listOf(
													stmt("ret = append(ret, cit);").build()
											)),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(listOf(
															stmt("ret = append(ret, cit);").build()
													))
											),
											action(listOf(
													stmt("if (typeKind == TypeKind.Interface) problem.value = new BUProblem(Severity.ERROR, \"An interface cannot implement other interfaces\");\n" + "").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("EnumDecl", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
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
							terminal("ENUM"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("implementsClause", "ImplementsList", null, listOf(
											expr("TypeKind.Enum").build(),
											expr("problem").build()
									))
							),
							terminal("LBRACE"),
							zeroOrOne(
									choice(
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("constants", "NodeListVar")
											),
											sequence(
													nonTerminal("entry", "EnumConstantDecl"),
													action(listOf(
															stmt("constants = append(constants, entry);").build()
													)),
													zeroOrMore(
															terminal("COMMA"),
															nonTerminal("entry", "EnumConstantDecl"),
															action(listOf(
																	stmt("constants = append(constants, entry);").build()
															))
													)
											)
									)
							),
							zeroOrOne(
									terminal("COMMA"),
									action(listOf(
											stmt("trailingComma = true;").build()
									))
							),
							zeroOrOne(
									terminal("SEMICOLON"),
									nonTerminal("members", "ClassOrInterfaceBodyDecls", null, listOf(
											expr("TypeKind.Enum").build()
									))
							),
							terminal("RBRACE"),
							action(listOf(
									stmt("return dress(SEnumDecl.make(modifiers, name, implementsClause, constants, trailingComma, ensureNotNull(members))).withProblem(problem.value);").build()
							))
					)
			),
			production("EnumConstantDecl", type("BUTree<SEnumConstantDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> args = null;").build(),
							stmt("BUTree<SNodeList> classBody = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
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
							action(listOf(
									stmt("return dress(SEnumConstantDecl.make(modifiers, name, optionOf(args), optionOf(classBody)));").build()
							))
					)
			),
			production("AnnotationTypeDecl", type("BUTree<SAnnotationDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> members;").build()
					),
					sequence(
							terminal("AT"),
							terminal("INTERFACE"),
							nonTerminal("name", "Name"),
							nonTerminal("members", "AnnotationTypeBody"),
							action(listOf(
									stmt("return dress(SAnnotationDecl.make(modifiers, name, members));").build()
							))
					)
			),
			production("AnnotationTypeBody", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SMemberDecl> member;").build()
					),
					sequence(
							terminal("LBRACE"),
							zeroOrOne(
									choice(
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											),
											oneOrMore(
													nonTerminal("member", "AnnotationTypeBodyDecl"),
													action(listOf(
															stmt("ret = append(ret, member);").build()
													))
											)
									)
							),
							terminal("RBRACE"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AnnotationTypeBodyDecl", type("BUTree<? extends SMemberDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SMemberDecl> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action(listOf(
													stmt("ret = dress(SEmptyTypeDecl.make());").build()
											))
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											choice(
													nonTerminal("ret", "AnnotationTypeMemberDecl", null, listOf(
															expr("modifiers").build()
													)),
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
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AnnotationTypeMemberDecl", type("BUTree<SAnnotationMemberDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
					listOf(
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> dims;").build(),
							stmt("BUTree<SNodeOption> defaultValue = none();").build(),
							stmt("BUTree<? extends SExpr> value = null;").build()
					),
					sequence(
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							nonTerminal("name", "Name"),
							terminal("LPAREN"),
							terminal("RPAREN"),
							nonTerminal("dims", "ArrayDims"),
							zeroOrOne(
									terminal("DEFAULT"),
									nonTerminal("value", "ElementValue"),
									action(listOf(
											stmt("defaultValue = optionOf(value);").build()
									))
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SAnnotationMemberDecl.make(modifiers, type, name, dims, defaultValue));").build()
							))
					)
			),
			production("TypeParameters", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<STypeParameter> tp;").build()
					),
					sequence(
							terminal("LT"),
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									),
									sequence(
											nonTerminal("tp", "TypeParameter"),
											action(listOf(
													stmt("ret = append(ret, tp);").build()
											)),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("tp", "TypeParameter"),
													action(listOf(
															stmt("ret = append(ret, tp);").build()
													))
											)
									)
							),
							terminal("GT"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("TypeParameter", type("BUTree<STypeParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> typeBounds = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("typeBounds", "TypeBounds")
							),
							action(listOf(
									stmt("return dress(STypeParameter.make(annotations, name, ensureNotNull(typeBounds)));").build()
							))
					)
			),
			production("TypeBounds", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							terminal("EXTENDS"),
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action(listOf(
													stmt("ret = append(ret, cit);").build()
											)),
											zeroOrMore(
													terminal("BIT_AND"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action(listOf(
															stmt("ret = append(ret, cit);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ClassOrInterfaceBody", type("BUTree<SNodeList>").build(),
					emptyList(),
					listOf(
							param("TypeKind typeKind").build()
					),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SMemberDecl> member;").build()
					),
					sequence(
							terminal("LBRACE"),
							nonTerminal("ret", "ClassOrInterfaceBodyDecls", null, listOf(
									expr("typeKind").build()
							)),
							terminal("RBRACE"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ClassOrInterfaceBodyDecls", type("BUTree<SNodeList>").build(),
					emptyList(),
					listOf(
							param("TypeKind typeKind").build()
					),
					listOf(
							stmt("BUTree<? extends SMemberDecl> member;").build(),
							stmt("BUTree<SNodeList> ret = emptyList();").build()
					),
					sequence(
							zeroOrOne(
									choice(
											sequence(
													lookAhead(
															expr("quotesMode").build()
													),
													nonTerminal("ret", "NodeListVar")
											),
											oneOrMore(
													nonTerminal("member", "ClassOrInterfaceBodyDecl", null, listOf(
															expr("typeKind").build()
													)),
													action(listOf(
															stmt("ret = append(ret, member);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ClassOrInterfaceBodyDecl", type("BUTree<? extends SMemberDecl>").build(),
					emptyList(),
					listOf(
							param("TypeKind typeKind").build()
					),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SMemberDecl> ret;").build(),
							stmt("BUProblem problem = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action(listOf(
													stmt("ret = dress(SEmptyMemberDecl.make());").build()
											))
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											action(listOf(
													stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default)) && typeKind != TypeKind.Interface) problem = new BUProblem(Severity.ERROR, \"Only interfaces can have default members\");\n" + "").build()
											)),
											choice(
													sequence(
															nonTerminal("ret", "InitializerDecl", null, listOf(
																	expr("modifiers").build()
															)),
															action(listOf(
																	stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));\n" + "").build()
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
													sequence(
															nonTerminal("ret", "ConstructorDecl", null, listOf(
																	expr("modifiers").build()
															)),
															action(listOf(
																	stmt("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));\n" + "").build()
															))
													),
													sequence(
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
							action(listOf(
									stmt("return ret.withProblem(problem);").build()
							))
					)
			),
			production("FieldDecl", type("BUTree<SFieldDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
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
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SFieldDecl.make(modifiers, type, variables));").build()
							))
					)
			),
			production("VariableDecl", type("BUTree<SLocalVariableDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
					listOf(
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> variables = emptyList();").build()
					),
					sequence(
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							nonTerminal("variables", "VariableDeclarators"),
							action(listOf(
									stmt("return dress(SLocalVariableDecl.make(modifiers, type, variables));").build()
							))
					)
			),
			production("VariableDeclarators", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> variables = emptyList();").build(),
							stmt("BUTree<SVariableDeclarator> val;").build()
					),
					sequence(
							nonTerminal("val", "VariableDeclarator"),
							action(listOf(
									stmt("variables = append(variables, val);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("val", "VariableDeclarator"),
									action(listOf(
											stmt("variables = append(variables, val);").build()
									))
							),
							action(listOf(
									stmt("return variables;").build()
							))
					)
			),
			production("VariableDeclarator", type("BUTree<SVariableDeclarator>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SVariableDeclaratorId> id;").build(),
							stmt("BUTree<SNodeOption> init = none();").build(),
							stmt("BUTree<? extends SExpr> initExpr = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("id", "VariableDeclaratorId"),
							zeroOrOne(
									terminal("ASSIGN"),
									nonTerminal("initExpr", "VariableInitializer"),
									action(listOf(
											stmt("init = optionOf(initExpr);").build()
									))
							),
							action(listOf(
									stmt("return dress(SVariableDeclarator.make(id, init));").build()
							))
					)
			),
			production("VariableDeclaratorId", type("BUTree<SVariableDeclaratorId>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("name", "Name"),
							nonTerminal("arrayDims", "ArrayDims"),
							action(listOf(
									stmt("return dress(SVariableDeclaratorId.make(name, arrayDims));").build()
							))
					)
			),
			production("ArrayDims", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build()
					),
					sequence(
							zeroOrMore(
									action(listOf(
											stmt("run();").build()
									)),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									terminal("RBRACKET"),
									action(listOf(
											stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
									))
							),
							action(listOf(
									stmt("return arrayDims;").build()
							))
					)
			),
			production("VariableInitializer", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "ArrayInitializer"),
									nonTerminal("ret", "Expression")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ArrayInitializer", type("BUTree<SArrayInitializerExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> values = emptyList();").build(),
							stmt("BUTree<? extends SExpr> val;").build(),
							stmt("boolean trailingComma = false;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("LBRACE"),
							zeroOrOne(
									nonTerminal("val", "VariableInitializer"),
									action(listOf(
											stmt("values = append(values, val);").build()
									)),
									zeroOrMore(
											terminal("COMMA"),
											nonTerminal("val", "VariableInitializer"),
											action(listOf(
													stmt("values = append(values, val);").build()
											))
									)
							),
							zeroOrOne(
									terminal("COMMA"),
									action(listOf(
											stmt("trailingComma = true;").build()
									))
							),
							terminal("RBRACE"),
							action(listOf(
									stmt("return dress(SArrayInitializerExpr.make(values, trailingComma));").build()
							))
					)
			),
			production("MethodDecl", type("BUTree<SMethodDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
					listOf(
							stmt("BUTree<SNodeList> typeParameters = null;").build(),
							stmt("BUTree<SNodeList> additionalAnnotations = null;").build(),
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
									nonTerminal("typeParameters", "TypeParameters"),
									nonTerminal("additionalAnnotations", "Annotations")
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
											terminal("SEMICOLON"),
											action(listOf(
													stmt("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default))) problem = new BUProblem(Severity.ERROR, \"Default methods must have a body\");\n" + "").build()
											))
									)
							),
							action(listOf(
									stmt("return dress(SMethodDecl.make(modifiers, ensureNotNull(typeParameters), ensureNotNull(additionalAnnotations), type, name, parameters, arrayDims, ensureNotNull(throwsClause), optionOf(block))).withProblem(problem);").build()
							))
					)
			),
			production("FormalParameters", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<SFormalParameter> par;").build()
					),
					sequence(
							terminal("LPAREN"),
							zeroOrOne(
									nonTerminal("ret", "FormalParameterList")
							),
							terminal("RPAREN"),
							action(listOf(
									stmt("return ensureNotNull(ret);").build()
							))
					)
			),
			production("FormalParameterList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<SFormalParameter> par;").build()
					),
					sequence(
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("ret", "NodeListVar")
									),
									// TODO Handle a receiver parameter as first parameter
									sequence(
											nonTerminal("par", "FormalParameter"),
											action(listOf(
													stmt("ret = append(ret, par);").build()
											)),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("par", "FormalParameter"),
													action(listOf(
															stmt("ret = append(ret, par);").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("FormalParameter", type("BUTree<SFormalParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> ellipsisAnnotations = null;").build(),
							stmt("boolean isVarArg = false;").build(),
							stmt("BUTree<SVariableDeclaratorId> id = null;").build(),
							stmt("boolean isReceiver = false;").build(),
							stmt("BUTree<SName> receiverTypeName = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							zeroOrOne(
									nonTerminal("ellipsisAnnotations", "Annotations"),
									terminal("ELLIPSIS"),
									action(listOf(
											stmt("isVarArg = true;").build()
									))
							),
							choice(
									sequence(
											zeroOrOne(
													nonTerminal("receiverTypeName", "Name"),
													terminal("DOT")
											),
											terminal("THIS"),
											action(listOf(
													stmt("isReceiver = true;").build()
											))
									),
									nonTerminal("id", "VariableDeclaratorId")
							),
							action(listOf(
									stmt("return dress(SFormalParameter.make(modifiers, type, isVarArg, ensureNotNull(ellipsisAnnotations), optionOf(id), isReceiver, optionOf(receiverTypeName)));").build()
							))
					)
			),
			production("ThrowsClause", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SQualifiedType> cit;").build()
					),
					sequence(
							terminal("THROWS"),
							nonTerminal("cit", "AnnotatedQualifiedType"),
							action(listOf(
									stmt("ret = append(ret, cit);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("cit", "AnnotatedQualifiedType"),
									action(listOf(
											stmt("ret = append(ret, cit);").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ConstructorDecl", type("BUTree<SConstructorDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
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
							action(listOf(
									stmt("run();").build()
							)),
							terminal("LBRACE"),
							nonTerminal("stmts", "Statements", listOf(
									expr("true").build()
							)),
							terminal("RBRACE"),
							action(listOf(
									stmt("block = dress(SBlockStmt.make(stmts));").build()
							)),
							action(listOf(
									stmt("return dress(SConstructorDecl.make(modifiers, ensureNotNull(typeParameters), name, parameters, ensureNotNull(throwsClause), block));").build()
							))
					)
			),
			// TODO Enable parsing of this anywhere in a block and add later checks to report when not used in a constructor
			production("ExplicitConstructorInvocation", type("BUTree<SExplicitConstructorInvocationStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("boolean isThis = false;").build(),
							stmt("BUTree<SNodeList> args;").build(),
							stmt("BUTree<? extends SExpr> expr = null;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											zeroOrOne(
													nonTerminal("typeArgs", "TypeArguments")
											),
											terminal("THIS"),
											action(listOf(
													stmt("isThis = true;").build()
											)),
											nonTerminal("args", "Arguments"),
											terminal("SEMICOLON")
									),
									sequence(
											zeroOrOne(
													nonTerminal("expr", "PrimaryExpressionWithoutSuperSuffix"),
													terminal("DOT")
											),
											zeroOrOne(
													nonTerminal("typeArgs", "TypeArguments")
											),
											terminal("SUPER"),
											nonTerminal("args", "Arguments"),
											terminal("SEMICOLON")
									)
							),
							action(listOf(
									stmt("return dress(SExplicitConstructorInvocationStmt.make(ensureNotNull(typeArgs), isThis, optionOf(expr), args));").build()
							))
					)
			).memoizeMatches(),
			production("Statements", type("BUTree<SNodeList>").build(),
					listOf(
							param("boolean inConstructor").build()
					),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					sequence(
							zeroOrOne(
									choice(
											sequence(
													nonTerminal("ret", "NodeListVar")
											),
											sequence(
													zeroOrOne(
															lookAhead(
																	expr("inConstructor").build()
															),
															nonTerminal("stmt", "ExplicitConstructorInvocation"),
															action(listOf(
																	stmt("ret = append(ret, stmt);").build()
															))
													),
													zeroOrMore(
															nonTerminal("stmt", "BlockStatement"),
															action(listOf(
																	stmt("ret = append(ret, stmt);").build()
															))
													)
											)
									)
							),
							action(listOf(
									stmt("return ensureNotNull(ret);").build()
							))
					)
			),
			production("InitializerDecl", type("BUTree<SInitializerDecl>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> modifiers").build()
					),
					listOf(
							stmt("BUTree<SBlockStmt> block;").build()
					),
					sequence(
							nonTerminal("block", "Block"),
							action(listOf(
									stmt("return dress(SInitializerDecl.make(modifiers, block));").build()
							))
					)
			),
			production("Type", type("BUTree<? extends SType>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> annotations").build()
					),
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
													action(listOf(
															stmt("lateRun();").build()
													)),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(listOf(
															stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
													))
											)
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													action(listOf(
															stmt("lateRun();").build()
													)),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(listOf(
															stmt("type = dress(SArrayType.make(type, arrayDims));").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return type == null ? primitiveType : type;").build()
							))
					)
			).memoizeMatches(),
			production("ReferenceType", type("BUTree<? extends SReferenceType>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> annotations").build()
					),
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
											action(listOf(
													stmt("lateRun();").build()
											)),
											nonTerminal("arrayDims", "ArrayDimsMandatory"),
											action(listOf(
													stmt("type = dress(SArrayType.make(primitiveType, arrayDims));").build()
											))
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													action(listOf(
															stmt("lateRun();").build()
													)),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action(listOf(
															stmt("type = dress(SArrayType.make(type, arrayDims));").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return type;").build()
							))
					)
			),
			production("QualifiedType", type("BUTree<SQualifiedType>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> annotations").build()
					),
					listOf(
							stmt("BUTree<SNodeOption> scope = none();").build(),
							stmt("BUTree<SQualifiedType> ret;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build()
					),
					sequence(
							action(listOf(
									stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
							)),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
							),
							action(listOf(
									stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
							)),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("DOT"),
									action(listOf(
											stmt("scope = optionOf(ret);").build()
									)),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("name", "Name"),
									zeroOrOne(
											nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
									),
									action(listOf(
											stmt("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("TypeArguments", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					sequence(
							terminal("LT"),
							zeroOrOne(
									nonTerminal("ret", "TypeArgumentList")
							),
							terminal("GT"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("TypeArgumentsOrDiamond", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					sequence(
							terminal("LT"),
							zeroOrOne(
									nonTerminal("ret", "TypeArgumentList")
							),
							terminal("GT"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("TypeArgumentList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					choice(
							sequence(
									lookAhead(
											expr("quotesMode").build()
									),
									nonTerminal("ret", "NodeListVar"),
									action(listOf(
											stmt("return ret;").build()
									))
							),
							sequence(
									nonTerminal("type", "TypeArgument"),
									action(listOf(
											stmt("ret = append(ret, type);").build()
									)),
									zeroOrMore(
											terminal("COMMA"),
											nonTerminal("type", "TypeArgument"),
											action(listOf(
													stmt("ret = append(ret, type);").build()
											))
									),
									action(listOf(
											stmt("return ret;").build()
									))
							)
					)
			),
			production("TypeArgument", type("BUTree<? extends SType>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SType> ret;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							choice(
									nonTerminal("ret", "ReferenceType", null, listOf(
											expr("annotations").build()
									)),
									nonTerminal("ret", "Wildcard", null, listOf(
											expr("annotations").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("Wildcard", type("BUTree<SWildcardType>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> annotations").build()
					),
					listOf(
							stmt("BUTree<? extends SReferenceType> ext = null;").build(),
							stmt("BUTree<? extends SReferenceType> sup = null;").build(),
							stmt("BUTree<SNodeList> boundAnnotations = null;").build()
					),
					sequence(
							action(listOf(
									stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
							)),
							terminal("HOOK"),
							zeroOrOne(
									choice(
											sequence(
													terminal("EXTENDS"),
													action(listOf(
															stmt("run();").build()
													)),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("ext", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											),
											sequence(
													terminal("SUPER"),
													action(listOf(
															stmt("run();").build()
													)),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("sup", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											)
									)
							),
							action(listOf(
									stmt("return dress(SWildcardType.make(annotations, optionOf(ext), optionOf(sup)));").build()
							))
					)
			),
			production("PrimitiveType", type("BUTree<SPrimitiveType>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> annotations").build()
					),
					listOf(
							stmt("Primitive primitive;").build()
					),
					sequence(
							action(listOf(
									stmt("if (annotations == null) {\n" + "\trun();\n" + "\tannotations = emptyList();\n" + "}").build()
							)),
							choice(
									sequence(
											terminal("BOOLEAN"),
											action(listOf(
													stmt("primitive = Primitive.Boolean;").build()
											))
									),
									sequence(
											terminal("CHAR"),
											action(listOf(
													stmt("primitive = Primitive.Char;").build()
											))
									),
									sequence(
											terminal("BYTE"),
											action(listOf(
													stmt("primitive = Primitive.Byte;").build()
											))
									),
									sequence(
											terminal("SHORT"),
											action(listOf(
													stmt("primitive = Primitive.Short;").build()
											))
									),
									sequence(
											terminal("INT"),
											action(listOf(
													stmt("primitive = Primitive.Int;").build()
											))
									),
									sequence(
											terminal("LONG"),
											action(listOf(
													stmt("primitive = Primitive.Long;").build()
											))
									),
									sequence(
											terminal("FLOAT"),
											action(listOf(
													stmt("primitive = Primitive.Float;").build()
											))
									),
									sequence(
											terminal("DOUBLE"),
											action(listOf(
													stmt("primitive = Primitive.Double;").build()
											))
									)
							),
							action(listOf(
									stmt("return dress(SPrimitiveType.make(annotations, primitive));").build()
							))
					)
			),
			production("ResultType", type("BUTree<? extends SType>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SType> ret;").build()
					),
					sequence(
							choice(
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											terminal("VOID"),
											action(listOf(
													stmt("ret = dress(SVoidType.make());").build()
											))
									),
									nonTerminal("ret", "Type", null, listOf(
											expr("null").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AnnotatedQualifiedType", type("BUTree<SQualifiedType>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<SQualifiedType> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "QualifiedType", null, listOf(
									expr("annotations").build()
							)),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("QualifiedName", type("BUTree<SQualifiedName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeOption> qualifier = none();").build(),
							stmt("BUTree<SQualifiedName> ret = null;").build(),
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("name", "Name"),
							action(listOf(
									stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
							)),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("DOT"),
									action(listOf(
											stmt("qualifier = optionOf(ret);").build()
									)),
									nonTerminal("name", "Name"),
									action(listOf(
											stmt("ret = dress(SQualifiedName.make(qualifier, name));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("Name", type("BUTree<SName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("Token id;").build(),
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							choice(
									sequence(
											lookAhead(
													expr("quotesMode").build()
											),
											nonTerminal("name", "NodeVar")
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											terminal("id", "IDENTIFIER"),
											action(listOf(
													stmt("name = dress(SName.make(id.image));").build()
											))
									)
							),
							action(listOf(
									stmt("return name;").build()
							))
					)
			),
			production("Expression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "AssignmentExpression"),
									nonTerminal("ret", "LambdaExpression")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AssignmentExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("AssignOp op;").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							// TODO Add checks to report invalid left hand side in assignment
							nonTerminal("ret", "ConditionalExpression"),
							zeroOrOne(
									action(listOf(
											stmt("lateRun();").build()
									)),
									nonTerminal("op", "AssignmentOperator"),
									nonTerminal("expr", "Expression"),
									action(listOf(
											stmt("ret = dress(SAssignExpr.make(ret, op, expr));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("LambdaExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											terminal("LPAREN"),
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											nonTerminal("type", "ReferenceCastTypeRest", null, listOf(
													expr("type").build()
											)),
											terminal("RPAREN"),
											nonTerminal("ret", "LambdaExpression"),
											action(listOf(
													stmt("ret = dress(SCastExpr.make(type, ret));").build()
											))
									),
									nonTerminal("ret", "LambdaExpressionWithoutCast")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("LambdaExpressionWithoutCast", type("BUTree<SLambdaExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SLambdaExpr> ret;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<SNodeList> params;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											nonTerminal("name", "Name"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("singletonList(makeFormalParameter(name))").build(),
													expr("false").build()
											))
									),
									sequence(
											terminal("LPAREN"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("emptyList()").build(),
													expr("true").build()
											))
									),
									sequence(
											terminal("LPAREN"),
											nonTerminal("params", "InferredFormalParameterList"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("params").build(),
													expr("true").build()
											))
									),
									sequence(
											terminal("LPAREN"),
											nonTerminal("params", "FormalParameterList"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, listOf(
													expr("params").build(),
													expr("true").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("LambdaBody", type("BUTree<SLambdaExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<SNodeList> parameters").build(),
							param("boolean parenthesis").build()
					),
					listOf(
							stmt("BUTree<SBlockStmt> block;").build(),
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SLambdaExpr> ret;").build()
					),
					sequence(
							choice(
									sequence(
											nonTerminal("expr", "Expression"),
											action(listOf(
													stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, left(expr)));").build()
											))
									),
									sequence(
											nonTerminal("block", "Block"),
											action(listOf(
													stmt("ret = dress(SLambdaExpr.make(parameters, parenthesis, right(block)));").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("InferredFormalParameterList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SFormalParameter> param;").build()
					),
					sequence(
							nonTerminal("param", "InferredFormalParameter"),
							action(listOf(
									stmt("ret = append(ret, param);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("param", "InferredFormalParameter"),
									action(listOf(
											stmt("ret = append(ret, param);").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("InferredFormalParameter", type("BUTree<SFormalParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							nonTerminal("name", "Name"),
							action(listOf(
									stmt("return makeFormalParameter(name);").build()
							))
					)
			),
			production("AssignmentOperator", type("AssignOp").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("AssignOp ret;").build()
					),
					sequence(
							choice(
									sequence(
											terminal("ASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Normal;").build()
											))
									),
									sequence(
											terminal("STARASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Times;").build()
											))
									),
									sequence(
											terminal("SLASHASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Divide;").build()
											))
									),
									sequence(
											terminal("REMASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Remainder;").build()
											))
									),
									sequence(
											terminal("PLUSASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Plus;").build()
											))
									),
									sequence(
											terminal("MINUSASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Minus;").build()
											))
									),
									sequence(
											terminal("LSHIFTASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.LeftShift;").build()
											))
									),
									sequence(
											terminal("RSIGNEDSHIFTASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.RightSignedShift;").build()
											))
									),
									sequence(
											terminal("RUNSIGNEDSHIFTASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.RightUnsignedShift;").build()
											))
									),
									sequence(
											terminal("ANDASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.And;").build()
											))
									),
									sequence(
											terminal("XORASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.XOr;").build()
											))
									),
									sequence(
											terminal("ORASSIGN"),
											action(listOf(
													stmt("ret = AssignOp.Or;").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ConditionalExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> left;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "ConditionalOrExpression"),
							zeroOrOne(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("HOOK"),
									nonTerminal("left", "Expression"),
									terminal("COLON"),
									choice(
											nonTerminal("right", "ConditionalExpression"),
											nonTerminal("right", "LambdaExpression")
									),
									action(listOf(
											stmt("ret = dress(SConditionalExpr.make(ret, left, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ConditionalOrExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "ConditionalAndExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("SC_OR"),
									nonTerminal("right", "ConditionalAndExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.Or, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ConditionalAndExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "InclusiveOrExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("SC_AND"),
									nonTerminal("right", "InclusiveOrExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.And, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("InclusiveOrExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "ExclusiveOrExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("BIT_OR"),
									nonTerminal("right", "ExclusiveOrExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinOr, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ExclusiveOrExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "AndExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("XOR"),
									nonTerminal("right", "AndExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.XOr, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AndExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build()
					),
					sequence(
							nonTerminal("ret", "EqualityExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("BIT_AND"),
									nonTerminal("right", "EqualityExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinAnd, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("EqualityExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "InstanceOfExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("EQ"),
													action(listOf(
															stmt("op = BinaryOp.Equal;").build()
													))
											),
											sequence(
													terminal("NE"),
													action(listOf(
															stmt("op = BinaryOp.NotEqual;").build()
													))
											)
									),
									nonTerminal("right", "InstanceOfExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("InstanceOfExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SType> type;").build()
					),
					sequence(
							nonTerminal("ret", "RelationalExpression"),
							zeroOrOne(
									action(listOf(
											stmt("lateRun();").build()
									)),
									terminal("INSTANCEOF"),
									action(listOf(
											stmt("run();").build()
									)),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("type", "Type", null, listOf(
											expr("annotations").build()
									)),
									action(listOf(
											stmt("ret = dress(SInstanceOfExpr.make(ret, type));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("RelationalExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "ShiftExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("LT"),
													action(listOf(
															stmt("op = BinaryOp.Less;").build()
													))
											),
											sequence(
													terminal("GT"),
													action(listOf(
															stmt("op = BinaryOp.Greater;").build()
													))
											),
											sequence(
													terminal("LE"),
													action(listOf(
															stmt("op = BinaryOp.LessOrEqual;").build()
													))
											),
											sequence(
													terminal("GE"),
													action(listOf(
															stmt("op = BinaryOp.GreaterOrEqual;").build()
													))
											)
									),
									nonTerminal("right", "ShiftExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ShiftExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "AdditiveExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("LSHIFT"),
													action(listOf(
															stmt("op = BinaryOp.LeftShift;").build()
													))
											),
											sequence(
													lookAhead(3),
													nonTerminal("RUNSIGNEDSHIFT"),
													action(listOf(
															stmt("op = BinaryOp.RightUnsignedShift;").build()
													))
											),
											sequence(
													nonTerminal("RSIGNEDSHIFT"),
													action(listOf(
															stmt("op = BinaryOp.RightSignedShift;").build()
													))
											)
									),
									nonTerminal("right", "AdditiveExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AdditiveExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "MultiplicativeExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("PLUS"),
													action(listOf(
															stmt("op = BinaryOp.Plus;").build()
													))
											),
											sequence(
													terminal("MINUS"),
													action(listOf(
															stmt("op = BinaryOp.Minus;").build()
													))
											)
									),
									nonTerminal("right", "MultiplicativeExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("MultiplicativeExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SExpr> right;").build(),
							stmt("BinaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "UnaryExpression"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("STAR"),
													action(listOf(
															stmt("op = BinaryOp.Times;").build()
													))
											),
											sequence(
													terminal("SLASH"),
													action(listOf(
															stmt("op = BinaryOp.Divide;").build()
													))
											),
											sequence(
													terminal("REM"),
													action(listOf(
															stmt("op = BinaryOp.Remainder;").build()
													))
											)
									),
									nonTerminal("right", "UnaryExpression"),
									action(listOf(
											stmt("ret = dress(SBinaryExpr.make(ret, op, right));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("UnaryExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "PrefixExpression"),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											choice(
													sequence(
															terminal("PLUS"),
															action(listOf(
																	stmt("op = UnaryOp.Positive;").build()
															))
													),
													sequence(
															terminal("MINUS"),
															action(listOf(
																	stmt("op = UnaryOp.Negative;").build()
															))
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action(listOf(
													stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
											))
									),
									nonTerminal("ret", "UnaryExpressionNotPlusMinus")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PrefixExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("UnaryOp op;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("INCR"),
											action(listOf(
													stmt("op = UnaryOp.PreIncrement;").build()
											))
									),
									sequence(
											terminal("DECR"),
											action(listOf(
													stmt("op = UnaryOp.PreDecrement;").build()
											))
									)
							),
							nonTerminal("ret", "UnaryExpression"),
							action(listOf(
									stmt("return dress(SUnaryExpr.make(op, ret));").build()
							))
					)
			),
			production("UnaryExpressionNotPlusMinus", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					sequence(
							choice(
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											choice(
													sequence(
															terminal("TILDE"),
															action(listOf(
																	stmt("op = UnaryOp.Inverse;").build()
															))
													),
													sequence(
															terminal("BANG"),
															action(listOf(
																	stmt("op = UnaryOp.Not;").build()
															))
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action(listOf(
													stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
											))
									),
									nonTerminal("ret", "CastExpression"),
									nonTerminal("ret", "PostfixExpression")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PostfixExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("UnaryOp op;").build()
					),
					sequence(
							nonTerminal("ret", "PrimaryExpression"),
							zeroOrOne(
									action(listOf(
											stmt("lateRun();").build()
									)),
									choice(
											sequence(
													terminal("INCR"),
													action(listOf(
															stmt("op = UnaryOp.PostIncrement;").build()
													))
											),
											sequence(
													terminal("DECR"),
													action(listOf(
															stmt("op = UnaryOp.PostDecrement;").build()
													))
											)
									),
									action(listOf(
											stmt("ret = dress(SUnaryExpr.make(op, ret));").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("CastExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<? extends SType> primitiveType;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> arrayDims;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("LPAREN"),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											terminal("RPAREN"),
											nonTerminal("ret", "UnaryExpression"),
											action(listOf(
													stmt("ret = dress(SCastExpr.make(primitiveType, ret));").build()
											))
									),
									sequence(
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											nonTerminal("type", "ReferenceCastTypeRest", null, listOf(
													expr("type").build()
											)),
											terminal("RPAREN"),
											nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
											action(listOf(
													stmt("ret = dress(SCastExpr.make(type, ret));").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("ReferenceCastTypeRest", type("BUTree<? extends SType>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SType> type").build()
					),
					listOf(
							stmt("BUTree<SNodeList> types = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							zeroOrOne(
									action(listOf(
											stmt("types = append(types, type);").build()
									)),
									action(listOf(
											stmt("lateRun();").build()
									)),
									oneOrMore(
											terminal("BIT_AND"),
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											action(listOf(
													stmt("types = append(types, type);").build()
											))
									),
									action(listOf(
											stmt("type = dress(SIntersectionType.make(types));").build()
									))
							),
							action(listOf(
									stmt("return type;").build()
							))
					)
			),
			production("Literal", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("Token literal;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("literal", "INTEGER_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Integer.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "LONG_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Long.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "FLOAT_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Float.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "DOUBLE_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Double.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "CHARACTER_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Character.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "STRING_LITERAL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(String.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "TRUE"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "FALSE"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Boolean.class, literal.image);").build()
											))
									),
									sequence(
											terminal("literal", "NULL"),
											action(listOf(
													stmt("ret = SLiteralExpr.make(Void.class, literal.image);").build()
											))
									)
							),
							action(listOf(
									stmt("return dress(ret);").build()
							))
					)
			),
			production("PrimaryExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "PrimaryNoNewArray"),
									nonTerminal("ret", "ArrayCreationExpr", null, listOf(
											expr("null").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PrimaryNoNewArray", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									nonTerminal("ret", "PrimarySuffix", null, listOf(
											expr("ret").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PrimaryExpressionWithoutSuperSuffix", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									action(listOf(
											stmt("lateRun();").build()
									)),
									nonTerminal("ret", "PrimarySuffixWithoutSuper", null, listOf(
											expr("ret").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("PrimaryPrefix", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
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
											action(listOf(
													stmt("run();").build()
											)),
											terminal("THIS"),
											action(listOf(
													stmt("ret = dress(SThisExpr.make(none()));").build()
											))
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											terminal("SUPER"),
											action(listOf(
													stmt("ret = dress(SSuperExpr.make(none()));").build()
											)),
											choice(
													sequence(
															action(listOf(
																	stmt("lateRun();").build()
															)),
															terminal("DOT"),
															choice(
																	nonTerminal("ret", "MethodInvocation", null, listOf(
																			expr("ret").build()
																	)),
																	nonTerminal("ret", "FieldAccess", null, listOf(
																			expr("ret").build()
																	))
															)
													),
													sequence(
															action(listOf(
																	stmt("lateRun();").build()
															)),
															nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
																	expr("ret").build()
															))
													)
											)
									),
									nonTerminal("ret", "ClassCreationExpr", null, listOf(
											expr("null").build()
									)),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("type", "ResultType"),
											terminal("DOT"),
											terminal("CLASS"),
											action(listOf(
													stmt("ret = dress(SClassExpr.make(type));").build()
											))
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("type", "ResultType"),
											action(listOf(
													stmt("ret = STypeExpr.make(type);").build()
											)),
											nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
													expr("ret").build()
											))
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("ret", "MethodInvocation", null, listOf(
													expr("null").build()
											))
									),
									sequence(
											nonTerminal("ret", "Name")
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											terminal("LPAREN"),
											nonTerminal("ret", "Expression"),
											terminal("RPAREN"),
											action(listOf(
													stmt("ret = dress(SParenthesizedExpr.make(ret));").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PrimarySuffix", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									sequence(
											nonTerminal("ret", "PrimarySuffixWithoutSuper", null, listOf(
													expr("scope").build()
											))
									),
									sequence(
											terminal("DOT"),
											terminal("SUPER"),
											action(listOf(
													stmt("ret = dress(SSuperExpr.make(optionOf(scope)));").build()
											))
									),
									nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
											expr("scope").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("PrimarySuffixWithoutSuper", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							choice(
									sequence(
											terminal("DOT"),
											choice(
													sequence(
															terminal("THIS"),
															action(listOf(
																	stmt("ret = dress(SThisExpr.make(optionOf(scope)));").build()
															))
													),
													nonTerminal("ret", "ClassCreationExpr", null, listOf(
															expr("scope").build()
													)),
													nonTerminal("ret", "MethodInvocation", null, listOf(
															expr("scope").build()
													)),
													nonTerminal("ret", "FieldAccess", null, listOf(
															expr("scope").build()
													))
											)
									),
									sequence(
											terminal("LBRACKET"),
											nonTerminal("ret", "Expression"),
											terminal("RBRACKET"),
											action(listOf(
													stmt("ret = dress(SArrayAccessExpr.make(scope, ret));").build()
											))
									)
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("FieldAccess", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							nonTerminal("name", "Name"),
							action(listOf(
									stmt("return dress(SFieldAccessExpr.make(optionOf(scope), name));").build()
							))
					)
			),
			production("MethodInvocation", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
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
							action(listOf(
									stmt("return dress(SMethodInvocationExpr.make(optionOf(scope), ensureNotNull(typeArgs), name, args));").build()
							))
					)
			),
			production("Arguments", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							terminal("LPAREN"),
							zeroOrOne(
									choice(
											sequence(
													lookAhead(expr("quotesMode").build()),
													nonTerminal("ret", "NodeListVar")
											),
											sequence(
													nonTerminal("expr", "Expression"),
													action(listOf(
															stmt("ret = append(ret, expr);").build()
													)),
													zeroOrMore(
															terminal("COMMA"),
															nonTerminal("expr", "Expression"),
															action(listOf(
																	stmt("ret = append(ret, expr);").build()
															))
													)
											)
									)
							),
							terminal("RPAREN"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("MethodReferenceSuffix", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							terminal("DOUBLECOLON"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							choice(
									nonTerminal("name", "Name"),
									sequence(
											terminal("NEW"),
											action(listOf(
													stmt("name = SName.make(\"new\");").build()
											))
									)
							),
							action(listOf(
									stmt("ret = dress(SMethodReferenceExpr.make(scope, ensureNotNull(typeArgs), name));").build()
							)),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			).memoizeMatches(),
			production("ClassCreationExpr", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SNodeList> anonymousBody = null;").build(),
							stmt("BUTree<SNodeList> args;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							action(listOf(
									stmt("if (scope == null) run();\n" + "").build()
							)),
							terminal("NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("type", "QualifiedType", null, listOf(
									expr("annotations").build()
							)),
							nonTerminal("args", "Arguments"),
							zeroOrOne(
									nonTerminal("anonymousBody", "ClassOrInterfaceBody", null, listOf(
											expr("TypeKind.Class").build()
									))
							),
							action(listOf(
									stmt("return dress(SObjectCreationExpr.make(optionOf(scope), ensureNotNull(typeArgs), (BUTree<SQualifiedType>) type, args, optionOf(anonymousBody)));").build()
							))
					)
			),
			production("ArrayCreationExpr", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SExpr> scope").build()
					),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build(),
							stmt("BUTree<? extends SType> type;").build(),
							stmt("BUTree<SNodeList> typeArgs = null;").build(),
							stmt("BUTree<SNodeList> anonymousBody = null;").build(),
							stmt("BUTree<SNodeList> args;").build(),
							stmt("BUTree<SNodeList> annotations = null;").build()
					),
					sequence(
							action(listOf(
									stmt("if (scope == null) run();\n" + "").build()
							)),
							terminal("NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("annotations", "Annotations"),
							choice(
									nonTerminal("type", "PrimitiveType", null, listOf(
											expr("annotations").build()
									)),
									nonTerminal("type", "QualifiedType", null, listOf(
											expr("annotations").build()
									))
							),
							nonTerminal("ret", "ArrayCreationExprRest", null, listOf(
									expr("type").build()
							)),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ArrayCreationExprRest", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					listOf(
							param("BUTree<? extends SType> componentType").build()
					),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SNodeList> arrayDimExprs = emptyList();").build(),
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations = null;").build(),
							stmt("BUTree<SArrayInitializerExpr> initializer;").build()
					),
					choice(
							sequence(
									nonTerminal("arrayDimExprs", "ArrayDimExprsMandatory"),
									nonTerminal("arrayDims", "ArrayDims"),
									action(listOf(
											stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, none()));").build()
									))
							),
							sequence(
									nonTerminal("arrayDims", "ArrayDimsMandatory"),
									nonTerminal("initializer", "ArrayInitializer"),
									action(listOf(
											stmt("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, optionOf(initializer)));").build()
									))
							)
					)
			),
			production("ArrayDimExprsMandatory", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> arrayDimExprs = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							oneOrMore(
									action(listOf(
											stmt("run();").build()
									)),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									nonTerminal("expr", "Expression"),
									terminal("RBRACKET"),
									action(listOf(
											stmt("arrayDimExprs = append(arrayDimExprs, dress(SArrayDimExpr.make(annotations, expr)));").build()
									))
							),
							action(listOf(
									stmt("return arrayDimExprs;").build()
							))
					)
			),
			production("ArrayDimsMandatory", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> arrayDims = emptyList();").build(),
							stmt("BUTree<SNodeList> annotations;").build()
					),
					sequence(
							oneOrMore(
									action(listOf(
											stmt("run();").build()
									)),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									terminal("RBRACKET"),
									action(listOf(
											stmt("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));").build()
									))
							),
							action(listOf(
									stmt("return arrayDims;").build()
							))
					)
			).memoizeMatches(),
			production("Statement", type("BUTree<? extends SStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "LabeledStatement"),
									nonTerminal("ret", "AssertStatement"),
									nonTerminal("ret", "Block"),
									nonTerminal("ret", "EmptyStatement"),
									nonTerminal("ret", "ExpressionStatement"),
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
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("AssertStatement", type("BUTree<SAssertStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> check;").build(),
							stmt("BUTree<? extends SExpr> msg = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("ASSERT"),
							nonTerminal("check", "Expression"),
							zeroOrOne(
									terminal("COLON"),
									nonTerminal("msg", "Expression")
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SAssertStmt.make(check, optionOf(msg)));").build()
							))
					)
			),
			production("LabeledStatement", type("BUTree<SLabeledStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> label;").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("label", "Name"),
							terminal("COLON"),
							nonTerminal("stmt", "Statement"),
							action(listOf(
									stmt("return dress(SLabeledStmt.make(label, stmt));").build()
							))
					)
			),
			production("Block", type("BUTree<SBlockStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> stmts;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("LBRACE"),
							nonTerminal("stmts", "Statements", listOf(
									expr("false").build()
							)),
							terminal("RBRACE"),
							action(listOf(
									stmt("return dress(SBlockStmt.make(ensureNotNull(stmts)));").build()
							))
					)
			),
			production("BlockStatement", type("BUTree<? extends SStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build(),
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<? extends STypeDecl> typeDecl;").build(),
							stmt("BUTree<SNodeList> modifiers;").build()
					),
					sequence(
							choice(
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											action(listOf(
													stmt("run();").build()
											)),
											nonTerminal("modifiers", "ModifiersNoDefault"),
											nonTerminal("typeDecl", "ClassOrInterfaceDecl", null, listOf(
													expr("modifiers").build()
											)),
											action(listOf(
													stmt("ret = dress(STypeDeclarationStmt.make(typeDecl));").build()
											))
									),
									sequence(
											action(listOf(
													stmt("run();").build()
											)),
											// TODO Rename LocalVariableDeclStmt and remove use of ExpressionStmt ?
											nonTerminal("expr", "VariableDeclExpression"),
											terminal("SEMICOLON"),
											action(listOf(
													stmt("ret = dress(SExpressionStmt.make(expr));").build()
											))
									),
									nonTerminal("ret", "Statement")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("VariableDeclExpression", type("BUTree<SVariableDeclarationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<SLocalVariableDecl> variableDecl;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "ModifiersNoDefault"),
							nonTerminal("variableDecl", "VariableDecl", null, listOf(
									expr("modifiers").build()
							)),
							action(listOf(
									stmt("return dress(SVariableDeclarationExpr.make(variableDecl));").build()
							))
					)
			),
			production("EmptyStatement", type("BUTree<SEmptyStmt>").build(),
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SEmptyStmt.make());").build()
							))
					)
			),
			production("ExpressionStatement", type("BUTree<SExpressionStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("AssignOp op;").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("expr", "StatementExpression"),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SExpressionStmt.make(expr));").build()
							))
					)
			),
			production("StatementExpression", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							// TODO Add further checks to report invalid expression in a statement
							nonTerminal("ret", "Expression"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("SwitchStatement", type("BUTree<SSwitchStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> selector;").build(),
							stmt("BUTree<SSwitchCase> entry;").build(),
							stmt("BUTree<SNodeList> entries = emptyList();").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("SWITCH"),
							terminal("LPAREN"),
							nonTerminal("selector", "Expression"),
							terminal("RPAREN"),
							terminal("LBRACE"),
							zeroOrMore(
									nonTerminal("entry", "SwitchEntry"),
									action(listOf(
											stmt("entries = append(entries, entry);").build()
									))
							),
							terminal("RBRACE"),
							action(listOf(
									stmt("return dress(SSwitchStmt.make(selector, entries));").build()
							))
					)
			),
			production("SwitchEntry", type("BUTree<SSwitchCase>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> label = null;").build(),
							stmt("BUTree<SNodeList> stmts;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							choice(
									sequence(
											terminal("CASE"),
											nonTerminal("label", "Expression")
									),
									terminal("DEFAULT")
							),
							terminal("COLON"),
							nonTerminal("stmts", "Statements", listOf(
									expr("false").build()
							)),
							action(listOf(
									stmt("return dress(SSwitchCase.make(optionOf(label), ensureNotNull(stmts)));").build()
							))
					)
			),
			production("IfStatement", type("BUTree<SIfStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> thenStmt;").build(),
							stmt("BUTree<? extends SStmt> elseStmt = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("IF"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							nonTerminal("thenStmt", "Statement"),
							zeroOrOne(
									terminal("ELSE"),
									nonTerminal("elseStmt", "Statement")
							),
							action(listOf(
									stmt("return dress(SIfStmt.make(condition, thenStmt, optionOf(elseStmt)));").build()
							))
					)
			),
			production("WhileStatement", type("BUTree<SWhileStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("WHILE"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							nonTerminal("body", "Statement"),
							action(listOf(
									stmt("return dress(SWhileStmt.make(condition, body));").build()
							))
					)
			),
			production("DoStatement", type("BUTree<SDoStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> condition;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("DO"),
							nonTerminal("body", "Statement"),
							terminal("WHILE"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SDoStmt.make(body, condition));").build()
							))
					)
			),
			production("ForStatement", type("BUTree<? extends SStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SVariableDeclarationExpr> varExpr = null;").build(),
							stmt("BUTree<? extends SExpr> expr = null;").build(),
							stmt("BUTree<SNodeList> init = null;").build(),
							stmt("BUTree<SNodeList> update = null;").build(),
							stmt("BUTree<? extends SStmt> body;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("FOR"),
							terminal("LPAREN"),
							choice(
									sequence(
											nonTerminal("varExpr", "VariableDeclExpression"),
											terminal("COLON"),
											nonTerminal("expr", "Expression")
									),
									sequence(
											zeroOrOne(
													nonTerminal("init", "ForInit")
											),
											terminal("SEMICOLON"),
											zeroOrOne(
													nonTerminal("expr", "Expression")
											),
											terminal("SEMICOLON"),
											zeroOrOne(
													nonTerminal("update", "ForUpdate")
											)
									)
							),
							terminal("RPAREN"),
							nonTerminal("body", "Statement"),
							action(listOf(
									stmt("if (varExpr != null)\n" + "\treturn dress(SForeachStmt.make(varExpr, expr, body));\n" + "else\n" + "\treturn dress(SForStmt.make(init, expr, update, body));\n" + "").build()
							))
					)
			),
			production("ForInit", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							choice(
									sequence(
											nonTerminal("expr", "VariableDeclExpression"),
											action(listOf(
													stmt("ret = emptyList();").build(),
													stmt("ret = append(ret, expr);").build()
											))
									),
									nonTerminal("ret", "StatementExpressionList")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("StatementExpressionList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							nonTerminal("expr", "StatementExpression"),
							action(listOf(
									stmt("ret = append(ret, expr);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("expr", "StatementExpression"),
									action(listOf(
											stmt("ret = append(ret, expr);").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ForUpdate", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							nonTerminal("ret", "StatementExpressionList"),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("BreakStatement", type("BUTree<SBreakStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("BREAK"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SBreakStmt.make(optionOf(id)));").build()
							))
					)
			),
			production("ContinueStatement", type("BUTree<SContinueStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("CONTINUE"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SContinueStmt.make(optionOf(id)));").build()
							))
					)
			),
			production("ReturnStatement", type("BUTree<SReturnStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("RETURN"),
							zeroOrOne(
									nonTerminal("expr", "Expression")
							),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SReturnStmt.make(optionOf(expr)));").build()
							))
					)
			),
			production("ThrowStatement", type("BUTree<SThrowStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("THROW"),
							nonTerminal("expr", "Expression"),
							terminal("SEMICOLON"),
							action(listOf(
									stmt("return dress(SThrowStmt.make(expr));").build()
							))
					)
			),
			production("SynchronizedStatement", type("BUTree<SSynchronizedStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build(),
							stmt("BUTree<SBlockStmt> block;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("SYNCHRONIZED"),
							terminal("LPAREN"),
							nonTerminal("expr", "Expression"),
							terminal("RPAREN"),
							nonTerminal("block", "Block"),
							action(listOf(
									stmt("return dress(SSynchronizedStmt.make(expr, block));").build()
							))
					)
			),
			production("TryStatement", type("BUTree<STryStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> resources = null;").build(),
							stmt("ByRef<Boolean> trailingSemiColon = new ByRef<Boolean>(false);").build(),
							stmt("BUTree<SBlockStmt> tryBlock;").build(),
							stmt("BUTree<SBlockStmt> finallyBlock = null;").build(),
							stmt("BUTree<SNodeList> catchClauses = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("TRY"),
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
													terminal("FINALLY"),
													nonTerminal("finallyBlock", "Block")
											)
									),
									sequence(
											nonTerminal("tryBlock", "Block"),
											choice(
													sequence(
															nonTerminal("catchClauses", "CatchClauses"),
															zeroOrOne(
																	terminal("FINALLY"),
																	nonTerminal("finallyBlock", "Block")
															)
													),
													sequence(
															terminal("FINALLY"),
															nonTerminal("finallyBlock", "Block")
													)
											)
									)
							),
							action(listOf(
									stmt("return dress(STryStmt.make(ensureNotNull(resources), trailingSemiColon.value, tryBlock, ensureNotNull(catchClauses), optionOf(finallyBlock)));").build()
							))
					)
			),
			production("CatchClauses", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> catchClauses = emptyList();").build(),
							stmt("BUTree<SCatchClause> catchClause;").build()
					),
					sequence(
							oneOrMore(
									nonTerminal("catchClause", "CatchClause"),
									action(listOf(
											stmt("catchClauses = append(catchClauses, catchClause);").build()
									))
							),
							action(listOf(
									stmt("return catchClauses;").build()
							))
					)
			),
			production("CatchClause", type("BUTree<SCatchClause>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SFormalParameter> param;").build(),
							stmt("BUTree<SBlockStmt> catchBlock;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("CATCH"),
							terminal("LPAREN"),
							nonTerminal("param", "CatchFormalParameter"),
							terminal("RPAREN"),
							nonTerminal("catchBlock", "Block"),
							action(listOf(
									stmt("return dress(SCatchClause.make(param, catchBlock));").build()
							))
					)
			),
			production("CatchFormalParameter", type("BUTree<SFormalParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends SType> exceptType;").build(),
							stmt("BUTree<SNodeList> exceptTypes = emptyList();").build(),
							stmt("BUTree<SVariableDeclaratorId> exceptId;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("exceptType", "QualifiedType", null, listOf(
									expr("null").build()
							)),
							action(listOf(
									stmt("exceptTypes = append(exceptTypes, exceptType);").build()
							)),
							zeroOrOne(
									action(listOf(
											stmt("lateRun();").build()
									)),
									oneOrMore(
											terminal("BIT_OR"),
											nonTerminal("exceptType", "AnnotatedQualifiedType"),
											action(listOf(
													stmt("exceptTypes = append(exceptTypes, exceptType);").build()
											))
									),
									action(listOf(
											stmt("exceptType = dress(SUnionType.make(exceptTypes));").build()
									))
							),
							nonTerminal("exceptId", "VariableDeclaratorId"),
							action(listOf(
									stmt("return dress(SFormalParameter.make(modifiers, exceptType, false, emptyList(), optionOf(exceptId), false, none()));").build()
							))
					)
			),
			production("ResourceSpecification", type("BUTree<SNodeList>").build(),
					emptyList(),
					listOf(
							param("ByRef<Boolean> trailingSemiColon").build()
					),
					listOf(
							stmt("BUTree<SNodeList> vars = emptyList();").build(),
							stmt("BUTree<SVariableDeclarationExpr> var;").build()
					),
					sequence(
							terminal("LPAREN"),
							nonTerminal("var", "VariableDeclExpression"),
							action(listOf(
									stmt("vars = append(vars, var);").build()
							)),
							zeroOrMore(
									terminal("SEMICOLON"),
									nonTerminal("var", "VariableDeclExpression"),
									action(listOf(
											stmt("vars = append(vars, var);").build()
									))
							),
							zeroOrOne(
									terminal("SEMICOLON"),
									action(listOf(
											stmt("trailingSemiColon.value = true;").build()
									))
							),
							terminal("RPAREN"),
							action(listOf(
									stmt("return vars;").build()
							))
					)
			),
			production("RUNSIGNEDSHIFT", null,
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							terminal("GT"),
							terminal("GT"),
							terminal("GT"),
							action(listOf(
									stmt("popNewWhitespaces(2);").build()
							))
					)
			),
			production("RSIGNEDSHIFT", null,
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							terminal("GT"),
							terminal("GT"),
							action(listOf(
									stmt("popNewWhitespaces(1);").build()
							))
					)
			),

			// ----- Annotations -----

			production("Annotations", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> annotations = emptyList();").build(),
							stmt("BUTree<? extends SAnnotationExpr> annotation;").build()
					),
					sequence(
							zeroOrMore(
									nonTerminal("annotation", "Annotation"),
									action(listOf(
											stmt("annotations = append(annotations, annotation);").build()
									))
							),
							action(listOf(
									stmt("return annotations;").build()
							))
					)
			).memoizeMatches(),
			production("Annotation", type("BUTree<? extends SAnnotationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SAnnotationExpr> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "NormalAnnotation"),
									nonTerminal("ret", "MarkerAnnotation"),
									nonTerminal("ret", "SingleElementAnnotation")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("NormalAnnotation", type("BUTree<SNormalAnnotationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("BUTree<SNodeList> pairs = null;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							terminal("LPAREN"),
							zeroOrOne(
									nonTerminal("pairs", "ElementValuePairList")
							),
							terminal("RPAREN"),
							action(listOf(
									stmt("return dress(SNormalAnnotationExpr.make(name, ensureNotNull(pairs)));").build()
							))
					)
			),
			production("MarkerAnnotation", type("BUTree<SMarkerAnnotationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							action(listOf(
									stmt("return dress(SMarkerAnnotationExpr.make(name));").build()
							))
					)
			),
			production("SingleElementAnnotation", type("BUTree<SSingleMemberAnnotationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							terminal("LPAREN"),
							nonTerminal("value", "ElementValue"),
							terminal("RPAREN"),
							action(listOf(
									stmt("return dress(SSingleMemberAnnotationExpr.make(name, value));").build()
							))
					)
			),
			production("ElementValuePairList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<SMemberValuePair> pair;").build()
					),
					sequence(
							nonTerminal("pair", "ElementValuePair"),
							action(listOf(
									stmt("ret = append(ret, pair);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("pair", "ElementValuePair"),
									action(listOf(
											stmt("ret = append(ret, pair);").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ElementValuePair", type("BUTree<SMemberValuePair>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> name;").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							nonTerminal("name", "Name"),
							terminal("ASSIGN"),
							nonTerminal("value", "ElementValue"),
							action(listOf(
									stmt("return dress(SMemberValuePair.make(name, value));").build()
							))
					)
			),
			production("ElementValue", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							choice(
									nonTerminal("ret", "ConditionalExpression"),
									nonTerminal("ret", "ElementValueArrayInitializer"),
									nonTerminal("ret", "Annotation")
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			),
			production("ElementValueArrayInitializer", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> values = null;").build(),
							stmt("boolean trailingComma = false;").build()
					),
					sequence(
							action(listOf(
									stmt("run();").build()
							)),
							terminal("LBRACE"),
							zeroOrOne(
									nonTerminal("values", "ElementValueList")
							),
							zeroOrOne(
									terminal("COMMA"),
									action(listOf(
											stmt("trailingComma = true;").build()
									))
							),
							terminal("RBRACE"),
							action(listOf(
									stmt("return dress(SArrayInitializerExpr.make(ensureNotNull(values), trailingComma));").build()
							))
					)
			),
			production("ElementValueList", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = emptyList();").build(),
							stmt("BUTree<? extends SExpr> value;").build()
					),
					sequence(
							nonTerminal("value", "ElementValue"),
							action(listOf(
									stmt("ret = append(ret, value);").build()
							)),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("value", "ElementValue"),
									action(listOf(
											stmt("ret = append(ret, value);").build()
									))
							),
							action(listOf(
									stmt("return ret;").build()
							))
					)
			)
	);
}
