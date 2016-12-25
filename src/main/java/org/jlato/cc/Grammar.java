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
							action("entryPoint = JavaGrammar.COMPILATION_UNIT_ENTRY;"),
							nonTerminal("ret", "CompilationUnit"),
							action("return ret;")
					)
			),
			production("PackageDeclEntry", type("BUTree<SPackageDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SPackageDecl> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.PACKAGE_DECL_ENTRY;"),
							nonTerminal("ret", "PackageDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("ImportDeclEntry", type("BUTree<SImportDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SImportDecl> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.IMPORT_DECL_ENTRY;"),
							nonTerminal("ret", "ImportDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("TypeDeclEntry", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends STypeDecl> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.TYPE_DECL_ENTRY;"),
							nonTerminal("ret", "TypeDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("MemberDeclEntry", type("BUTree<? extends SMemberDecl>").build(),
					listOf(param("TypeKind typeKind").build()),
					emptyList(),
					listOf(stmt("BUTree<? extends SMemberDecl> ret;").build()),
					sequence(
							action("entryPoint = JavaGrammar.MEMBER_DECL_ENTRY;"),
							nonTerminal("ret", "ClassOrInterfaceBodyDecl", listOf(expr("typeKind").build())),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("AnnotationMemberDeclEntry", type("BUTree<? extends SMemberDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SMemberDecl> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.ANNOTATION_MEMBER_DECL_ENTRY;"),
							// TODO Rename AnnotationMemberDecl
							nonTerminal("ret", "AnnotationTypeBodyDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("ModifiersEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.MODIFIERS_ENTRY;"),
							nonTerminal("ret", "Modifiers"),
							nonTerminal("Epilog"),
							action("return ret;")
					)
			),
			production("AnnotationsEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.ANNOTATIONS_ENTRY;"),
							nonTerminal("ret", "Annotations"),
							nonTerminal("Epilog"),
							action("return ret;")
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
							action("entryPoint = JavaGrammar.METHOD_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "MethodDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
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
							action("entryPoint = JavaGrammar.FIELD_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "FieldDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
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
							action("entryPoint = JavaGrammar.ANNOTATION_ELEMENT_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							// TODO Rename AnnotationElementDecl
							nonTerminal("ret", "AnnotationTypeMemberDecl", listOf(expr("modifiers").build())),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("EnumConstantDeclEntry", type("BUTree<SEnumConstantDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SEnumConstantDecl> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.ENUM_CONSTANT_DECL_ENTRY;"),
							nonTerminal("ret", "EnumConstantDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("FormalParameterEntry", type("BUTree<SFormalParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SFormalParameter> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.FORMAL_PARAMETER_ENTRY;"),
							nonTerminal("ret", "FormalParameter"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("TypeParameterEntry", type("BUTree<STypeParameter>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<STypeParameter> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.TYPE_PARAMETER_ENTRY;"),
							nonTerminal("ret", "TypeParameter"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("StatementsEntry", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.STATEMENTS_ENTRY;"),
							nonTerminal("ret", "Statements"),
							nonTerminal("Epilog"),
							action("return ret;")
					)
			),
			production("BlockStatementEntry", type("BUTree<? extends SStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SStmt> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.BLOCK_STATEMENT_ENTRY;"),
							nonTerminal("ret", "BlockStatement"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("ExpressionEntry", type("BUTree<? extends SExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.EXPRESSION_ENTRY;"),
							nonTerminal("ret", "Expression"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
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
							action("entryPoint = JavaGrammar.TYPE_ENTRY;"),
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "Type", listOf(expr("annotations").build())),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("QualifiedNameEntry", type("BUTree<SQualifiedName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.QUALIFIED_NAME_ENTRY;"),
							nonTerminal("ret", "QualifiedName"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("NameEntry", type("BUTree<SName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> ret;").build()
					),
					sequence(
							action("entryPoint = JavaGrammar.NAME_ENTRY;"),
							nonTerminal("ret", "Name"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
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
							action("return makeVar(id);")
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
							action("return makeVar(id);")
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
							action("run();"),
							zeroOrOne(
									nonTerminal("packageDecl", "PackageDecl")
							),
							nonTerminal("imports", "ImportDecls"),
							nonTerminal("types", "TypeDecls"),
							action("compilationUnit = dress(SCompilationUnit.make(packageDecl, imports, types));"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(compilationUnit);")
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
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							terminal("PACKAGE"),
							nonTerminal("name", "QualifiedName"),
							terminal("SEMICOLON"),
							action("return dress(SPackageDecl.make(annotations, name));")
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
									action("imports = append(imports, importDecl);")
							),
							action("return imports;")
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
							action("run();"),
							terminal("IMPORT"),
							zeroOrOne(
									terminal("STATIC"),
									action("isStatic = true;")
							),
							nonTerminal("name", "QualifiedName"),
							zeroOrOne(
									terminal("DOT"),
									terminal("STAR"),
									action("isAsterisk = true;")
							),
							terminal("SEMICOLON"),
							action("return dress(SImportDecl.make(name, isStatic, isAsterisk));")
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
									action("types = append(types, typeDecl);")
							),
							action("return types;")
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
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));")
											),
											sequence(
													terminal("PROTECTED"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));")
											),
											sequence(
													terminal("PRIVATE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));")
											),
											sequence(
													terminal("ABSTRACT"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));")
											),
											sequence(
													terminal("DEFAULT"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Default));")
											),
											sequence(
													terminal("STATIC"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));")
											),
											sequence(
													terminal("FINAL"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));")
											),
											sequence(
													terminal("TRANSIENT"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));")
											),
											sequence(
													terminal("VOLATILE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));")
											),
											sequence(
													terminal("SYNCHRONIZED"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));")
											),
											sequence(
													terminal("NATIVE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));")
											),
											sequence(
													terminal("STRICTFP"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));")
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action("modifiers = append(modifiers, ann);")
											)
									)
							),
							action("return modifiers;")
					)
			),
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
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Public));")
											),
											sequence(
													terminal("PROTECTED"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Protected));")
											),
											sequence(
													terminal("PRIVATE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Private));")
											),
											sequence(
													terminal("ABSTRACT"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Abstract));")
											),
											sequence(
													terminal("STATIC"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Static));")
											),
											sequence(
													terminal("FINAL"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Final));")
											),
											sequence(
													terminal("TRANSIENT"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Transient));")
											),
											sequence(
													terminal("VOLATILE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Volatile));")
											),
											sequence(
													terminal("SYNCHRONIZED"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Synchronized));")
											),
											sequence(
													terminal("NATIVE"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.Native));")
											),
											sequence(
													terminal("STRICTFP"),
													action("modifiers = append(modifiers, SModifier.make(ModifierKeyword.StrictFP));")
											),
											sequence(
													nonTerminal("ann", "Annotation"),
													action("modifiers = append(modifiers, ann);")
											)
									)
							),
							action("return modifiers;")
					)
			),
			production("TypeDecl", type("BUTree<? extends STypeDecl>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> modifiers;").build(),
							stmt("BUTree<? extends STypeDecl> ret;").build()
					),
					sequence(
							action("run();"),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action("ret = dress(SEmptyTypeDecl.make());")
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
							action("return ret;")
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
											action("typeKind = TypeKind.Class;"),
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
											action("typeKind = TypeKind.Interface;"),
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
							action("if (typeKind == TypeKind.Interface)\n\treturn dress(SInterfaceDecl.make(modifiers, name, ensureNotNull(typeParams), ensureNotNull(extendsClause), members)).withProblem(problem.value);\nelse {\n\treturn dress(SClassDecl.make(modifiers, name, ensureNotNull(typeParams), optionOf(superClassType), ensureNotNull(implementsClause), members));\n}")
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
									nonTerminal("ret", "NodeListVar"),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action("ret = append(ret, cit);"),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action("ret = append(ret, cit);")
											)
									)
							),
							action("return ret;")
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
									nonTerminal("ret", "NodeListVar"),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action("ret = append(ret, cit);"),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action("ret = append(ret, cit);")
											),
											action("if (typeKind == TypeKind.Interface) problem.value = new BUProblem(Severity.ERROR, \"An interface cannot implement other interfaces\");")
									)
							),
							action("return ret;")
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
											nonTerminal("constants", "NodeListVar"),
											sequence(
													nonTerminal("entry", "EnumConstantDecl"),
													action("constants = append(constants, entry);"),
													zeroOrMore(
															terminal("COMMA"),
															nonTerminal("entry", "EnumConstantDecl"),
															action("constants = append(constants, entry);")
													)
											)
									)
							),
							zeroOrOne(
									terminal("COMMA"),
									action("trailingComma = true;")
							),
							zeroOrOne(
									terminal("SEMICOLON"),
									nonTerminal("members", "ClassOrInterfaceBodyDecls", null, listOf(
											expr("TypeKind.Enum").build()
									))
							),
							terminal("RBRACE"),
							action("return dress(SEnumDecl.make(modifiers, name, implementsClause, constants, trailingComma, ensureNotNull(members))).withProblem(problem.value);")
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
							action("run();"),
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
							action("return dress(SEnumConstantDecl.make(modifiers, name, optionOf(args), optionOf(classBody)));")
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
							action("return dress(SAnnotationDecl.make(modifiers, name, members));")
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
											nonTerminal("ret", "NodeListVar"),
											oneOrMore(
													nonTerminal("member", "AnnotationTypeBodyDecl"),
													action("ret = append(ret, member);")
											)
									)
							),
							terminal("RBRACE"),
							action("return ret;")
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
							action("run();"),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action("ret = dress(SEmptyTypeDecl.make());")
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
							action("return ret;")
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
									action("defaultValue = optionOf(value);")
							),
							terminal("SEMICOLON"),
							action("return dress(SAnnotationMemberDecl.make(modifiers, type, name, dims, defaultValue));")
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
									nonTerminal("ret", "NodeListVar"),
									sequence(
											nonTerminal("tp", "TypeParameter"),
											action("ret = append(ret, tp);"),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("tp", "TypeParameter"),
													action("ret = append(ret, tp);")
											)
									)
							),
							terminal("GT"),
							action("return ret;")
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
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("typeBounds", "TypeBounds")
							),
							action("return dress(STypeParameter.make(annotations, name, ensureNotNull(typeBounds)));")
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
									nonTerminal("ret", "NodeListVar"),
									sequence(
											nonTerminal("cit", "AnnotatedQualifiedType"),
											action("ret = append(ret, cit);"),
											zeroOrMore(
													terminal("BIT_AND"),
													nonTerminal("cit", "AnnotatedQualifiedType"),
													action("ret = append(ret, cit);")
											)
									)
							),
							action("return ret;")
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
							action("return ret;")
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
											nonTerminal("ret", "NodeListVar"),
											oneOrMore(
													nonTerminal("member", "ClassOrInterfaceBodyDecl", null, listOf(
															expr("typeKind").build()
													)),
													action("ret = append(ret, member);")
											)
									)
							),
							action("return ret;")
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
							action("run();"),
							choice(
									sequence(
											terminal("SEMICOLON"),
											action("ret = dress(SEmptyMemberDecl.make());")
									),
									sequence(
											nonTerminal("modifiers", "Modifiers"),
											action("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default)) && typeKind != TypeKind.Interface) problem = new BUProblem(Severity.ERROR, \"Only interfaces can have default members\");"),
											choice(
													sequence(
															nonTerminal("ret", "InitializerDecl", null, listOf(
																	expr("modifiers").build()
															)),
															action("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));")
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
															action("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));")
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
							action("return ret.withProblem(problem);")
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
							action("return dress(SFieldDecl.make(modifiers, type, variables));")
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
							action("return dress(SLocalVariableDecl.make(modifiers, type, variables));")
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
							action("variables = append(variables, val);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("val", "VariableDeclarator"),
									action("variables = append(variables, val);")
							),
							action("return variables;")
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
							action("run();"),
							nonTerminal("id", "VariableDeclaratorId"),
							zeroOrOne(
									terminal("ASSIGN"),
									nonTerminal("initExpr", "VariableInitializer"),
									action("init = optionOf(initExpr);")
							),
							action("return dress(SVariableDeclarator.make(id, init));")
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
							action("run();"),
							nonTerminal("name", "Name"),
							nonTerminal("arrayDims", "ArrayDims"),
							action("return dress(SVariableDeclaratorId.make(name, arrayDims));")
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
									action("run();"),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									terminal("RBRACKET"),
									action("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));")
							),
							action("return arrayDims;")
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
							action("return ret;")
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
							action("run();"),
							terminal("LBRACE"),
							zeroOrOne(
									nonTerminal("val", "VariableInitializer"),
									action("values = append(values, val);"),
									zeroOrMore(
											terminal("COMMA"),
											nonTerminal("val", "VariableInitializer"),
											action("values = append(values, val);")
									)
							),
							zeroOrOne(
									terminal("COMMA"),
									action("trailingComma = true;")
							),
							terminal("RBRACE"),
							action("return dress(SArrayInitializerExpr.make(values, trailingComma));")
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
											action("if (modifiers != null && contains(modifiers, SModifier.make(ModifierKeyword.Default))) problem = new BUProblem(Severity.ERROR, \"Default methods must have a body\");")
									)
							),
							action("return dress(SMethodDecl.make(modifiers, ensureNotNull(typeParameters), ensureNotNull(additionalAnnotations), type, name, parameters, arrayDims, ensureNotNull(throwsClause), optionOf(block))).withProblem(problem);")
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
							action("return ensureNotNull(ret);")
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
									nonTerminal("ret", "NodeListVar"),
									// TODO Handle a receiver parameter as first parameter
									sequence(
											nonTerminal("par", "FormalParameter"),
											action("ret = append(ret, par);"),
											zeroOrMore(
													terminal("COMMA"),
													nonTerminal("par", "FormalParameter"),
													action("ret = append(ret, par);")
											)
									)
							),
							action("return ret;")
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
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("type", "Type", null, listOf(
									expr("null").build()
							)),
							zeroOrOne(
									nonTerminal("ellipsisAnnotations", "Annotations"),
									terminal("ELLIPSIS"),
									action("isVarArg = true;")
							),
							choice(
									sequence(
											zeroOrOne(
													nonTerminal("receiverTypeName", "Name"),
													terminal("DOT")
											),
											terminal("THIS"),
											action("isReceiver = true;")
									),
									nonTerminal("id", "VariableDeclaratorId")
							),
							action("return dress(SFormalParameter.make(modifiers, type, isVarArg, ensureNotNull(ellipsisAnnotations), optionOf(id), isReceiver, optionOf(receiverTypeName)));")
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
							action("ret = append(ret, cit);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("cit", "AnnotatedQualifiedType"),
									action("ret = append(ret, cit);")
							),
							action("return ret;")
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
							action("run();"),
							terminal("LBRACE"),
							nonTerminal("stmts", "Statements"),
							terminal("RBRACE"),
							action("block = dress(SBlockStmt.make(stmts));"),
							action("return dress(SConstructorDecl.make(modifiers, ensureNotNull(typeParameters), name, parameters, ensureNotNull(throwsClause), block));")
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
							action("run();"),
							choice(
									sequence(
											zeroOrOne(
													nonTerminal("typeArgs", "TypeArguments")
											),
											terminal("THIS"),
											action("isThis = true;"),
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
							action("return dress(SExplicitConstructorInvocationStmt.make(ensureNotNull(typeArgs), isThis, optionOf(expr), args));")
					)
			),
			production("Statements", type("BUTree<SNodeList>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> ret = null;").build(),
							stmt("BUTree<? extends SStmt> stmt;").build()
					),
					sequence(
							zeroOrOne(
									choice(
											nonTerminal("ret", "NodeListVar"),
											sequence(
													zeroOrOne(
															nonTerminal("stmt", "ExplicitConstructorInvocation"),
															action("ret = append(ret, stmt);")
													),
													zeroOrMore(
															nonTerminal("stmt", "BlockStatement"),
															action("ret = append(ret, stmt);")
													)
											)
									)
							),
							action("return ensureNotNull(ret);")
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
							action("return dress(SInitializerDecl.make(modifiers, block));")
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
													action("lateRun();"),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action("type = dress(SArrayType.make(primitiveType, arrayDims));")
											)
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													action("lateRun();"),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action("type = dress(SArrayType.make(type, arrayDims));")
											)
									)
							),
							action("return type == null ? primitiveType : type;")
					)
			),
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
											action("lateRun();"),
											nonTerminal("arrayDims", "ArrayDimsMandatory"),
											action("type = dress(SArrayType.make(primitiveType, arrayDims));")
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, listOf(
													expr("annotations").build()
											)),
											zeroOrOne(
													action("lateRun();"),
													nonTerminal("arrayDims", "ArrayDimsMandatory"),
													action("type = dress(SArrayType.make(type, arrayDims));")
											)
									)
							),
							action("return type;")
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
							action("if (annotations == null) {\n\trun();\n\tannotations = emptyList();\n}"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
							),
							action("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));"),
							zeroOrMore(
									action("lateRun();"),
									terminal("DOT"),
									action("scope = optionOf(ret);"),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("name", "Name"),
									zeroOrOne(
											nonTerminal("typeArgs", "TypeArgumentsOrDiamond")
									),
									action("ret = dress(SQualifiedType.make(annotations, scope, name, optionOf(typeArgs)));")
							),
							action("return ret;")
					)
			),
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
							action("return ret;")
					)
			),
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
							action("return ret;")
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
									nonTerminal("ret", "NodeListVar"),
									action("return ret;")
							),
							sequence(
									nonTerminal("type", "TypeArgument"),
									action("ret = append(ret, type);"),
									zeroOrMore(
											terminal("COMMA"),
											nonTerminal("type", "TypeArgument"),
											action("ret = append(ret, type);")
									),
									action("return ret;")
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
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							choice(
									nonTerminal("ret", "ReferenceType", null, listOf(
											expr("annotations").build()
									)),
									nonTerminal("ret", "Wildcard", null, listOf(
											expr("annotations").build()
									))
							),
							action("return ret;")
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
							action("if (annotations == null) {\n\trun();\n\tannotations = emptyList();\n}"),
							terminal("HOOK"),
							zeroOrOne(
									choice(
											sequence(
													terminal("EXTENDS"),
													action("run();"),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("ext", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											),
											sequence(
													terminal("SUPER"),
													action("run();"),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("sup", "ReferenceType", null, listOf(
															expr("boundAnnotations").build()
													))
											)
									)
							),
							action("return dress(SWildcardType.make(annotations, optionOf(ext), optionOf(sup)));")
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
							action("if (annotations == null) {\n\trun();\n\tannotations = emptyList();\n}"),
							choice(
									sequence(
											terminal("BOOLEAN"),
											action("primitive = Primitive.Boolean;")
									),
									sequence(
											terminal("CHAR"),
											action("primitive = Primitive.Char;")
									),
									sequence(
											terminal("BYTE"),
											action("primitive = Primitive.Byte;")
									),
									sequence(
											terminal("SHORT"),
											action("primitive = Primitive.Short;")
									),
									sequence(
											terminal("INT"),
											action("primitive = Primitive.Int;")
									),
									sequence(
											terminal("LONG"),
											action("primitive = Primitive.Long;")
									),
									sequence(
											terminal("FLOAT"),
											action("primitive = Primitive.Float;")
									),
									sequence(
											terminal("DOUBLE"),
											action("primitive = Primitive.Double;")
									)
							),
							action("return dress(SPrimitiveType.make(annotations, primitive));")
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
											action("run();"),
											terminal("VOID"),
											action("ret = dress(SVoidType.make());")
									),
									nonTerminal("ret", "Type", null, listOf(
											expr("null").build()
									))
							),
							action("return ret;")
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
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "QualifiedType", null, listOf(
									expr("annotations").build()
							)),
							action("return ret;")
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
							action("run();"),
							nonTerminal("name", "Name"),
							action("ret = dress(SQualifiedName.make(qualifier, name));"),
							zeroOrMore(
									action("lateRun();"),
									terminal("DOT"),
									action("qualifier = optionOf(ret);"),
									nonTerminal("name", "Name"),
									action("ret = dress(SQualifiedName.make(qualifier, name));")
							),
							action("return ret;")
					)
			),
			production("Name", type("BUTree<SName>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("Token id;").build(),
							stmt("BUTree<SName> name;").build()
					),
					sequence(
							choice(
									nonTerminal("name", "NodeVar"),
									sequence(
											action("run();"),
											terminal("id", "IDENTIFIER"),
											action("name = dress(SName.make(id.image));")
									)
							),
							action("return name;")
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
							action("return ret;")
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
									action("lateRun();"),
									nonTerminal("op", "AssignmentOperator"),
									nonTerminal("expr", "Expression"),
									action("ret = dress(SAssignExpr.make(ret, op, expr));")
							),
							action("return ret;")
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
											action("run();"),
											terminal("LPAREN"),
											action("run();"),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											nonTerminal("type", "ReferenceCastTypeRest", null, listOf(
													expr("type").build()
											)),
											terminal("RPAREN"),
											nonTerminal("ret", "LambdaExpression"),
											action("ret = dress(SCastExpr.make(type, ret));")
									),
									nonTerminal("ret", "LambdaExpressionWithoutCast")
							),
							action("return ret;")
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
							action("run();"),
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
							action("return ret;")
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
											action("ret = dress(SLambdaExpr.make(parameters, parenthesis, left(expr)));")
									),
									sequence(
											nonTerminal("block", "Block"),
											action("ret = dress(SLambdaExpr.make(parameters, parenthesis, right(block)));")
									)
							),
							action("return ret;")
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
							action("ret = append(ret, param);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("param", "InferredFormalParameter"),
									action("ret = append(ret, param);")
							),
							action("return ret;")
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
							action("return makeFormalParameter(name);")
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
											action("ret = AssignOp.Normal;")
									),
									sequence(
											terminal("STARASSIGN"),
											action("ret = AssignOp.Times;")
									),
									sequence(
											terminal("SLASHASSIGN"),
											action("ret = AssignOp.Divide;")
									),
									sequence(
											terminal("REMASSIGN"),
											action("ret = AssignOp.Remainder;")
									),
									sequence(
											terminal("PLUSASSIGN"),
											action("ret = AssignOp.Plus;")
									),
									sequence(
											terminal("MINUSASSIGN"),
											action("ret = AssignOp.Minus;")
									),
									sequence(
											terminal("LSHIFTASSIGN"),
											action("ret = AssignOp.LeftShift;")
									),
									sequence(
											terminal("RSIGNEDSHIFTASSIGN"),
											action("ret = AssignOp.RightSignedShift;")
									),
									sequence(
											terminal("RUNSIGNEDSHIFTASSIGN"),
											action("ret = AssignOp.RightUnsignedShift;")
									),
									sequence(
											terminal("ANDASSIGN"),
											action("ret = AssignOp.And;")
									),
									sequence(
											terminal("XORASSIGN"),
											action("ret = AssignOp.XOr;")
									),
									sequence(
											terminal("ORASSIGN"),
											action("ret = AssignOp.Or;")
									)
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("HOOK"),
									nonTerminal("left", "Expression"),
									terminal("COLON"),
									choice(
											nonTerminal("right", "ConditionalExpression"),
											nonTerminal("right", "LambdaExpression")
									),
									action("ret = dress(SConditionalExpr.make(ret, left, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("SC_OR"),
									nonTerminal("right", "ConditionalAndExpression"),
									action("ret = dress(SBinaryExpr.make(ret, BinaryOp.Or, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("SC_AND"),
									nonTerminal("right", "InclusiveOrExpression"),
									action("ret = dress(SBinaryExpr.make(ret, BinaryOp.And, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("BIT_OR"),
									nonTerminal("right", "ExclusiveOrExpression"),
									action("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinOr, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("XOR"),
									nonTerminal("right", "AndExpression"),
									action("ret = dress(SBinaryExpr.make(ret, BinaryOp.XOr, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("BIT_AND"),
									nonTerminal("right", "EqualityExpression"),
									action("ret = dress(SBinaryExpr.make(ret, BinaryOp.BinAnd, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("EQ"),
													action("op = BinaryOp.Equal;")
											),
											sequence(
													terminal("NE"),
													action("op = BinaryOp.NotEqual;")
											)
									),
									nonTerminal("right", "InstanceOfExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									terminal("INSTANCEOF"),
									action("run();"),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("type", "Type", null, listOf(
											expr("annotations").build()
									)),
									action("ret = dress(SInstanceOfExpr.make(ret, type));")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("LT"),
													action("op = BinaryOp.Less;")
											),
											sequence(
													terminal("GT"),
													action("op = BinaryOp.Greater;")
											),
											sequence(
													terminal("LE"),
													action("op = BinaryOp.LessOrEqual;")
											),
											sequence(
													terminal("GE"),
													action("op = BinaryOp.GreaterOrEqual;")
											)
									),
									nonTerminal("right", "ShiftExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("LSHIFT"),
													action("op = BinaryOp.LeftShift;")
											),
											sequence(
													nonTerminal("RUNSIGNEDSHIFT"),
													action("op = BinaryOp.RightUnsignedShift;")
											),
											sequence(
													nonTerminal("RSIGNEDSHIFT"),
													action("op = BinaryOp.RightSignedShift;")
											)
									),
									nonTerminal("right", "AdditiveExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("PLUS"),
													action("op = BinaryOp.Plus;")
											),
											sequence(
													terminal("MINUS"),
													action("op = BinaryOp.Minus;")
											)
									),
									nonTerminal("right", "MultiplicativeExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("STAR"),
													action("op = BinaryOp.Times;")
											),
											sequence(
													terminal("SLASH"),
													action("op = BinaryOp.Divide;")
											),
											sequence(
													terminal("REM"),
													action("op = BinaryOp.Remainder;")
											)
									),
									nonTerminal("right", "UnaryExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
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
											action("run();"),
											choice(
													sequence(
															terminal("PLUS"),
															action("op = UnaryOp.Positive;")
													),
													sequence(
															terminal("MINUS"),
															action("op = UnaryOp.Negative;")
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action("ret = dress(SUnaryExpr.make(op, ret));")
									),
									nonTerminal("ret", "UnaryExpressionNotPlusMinus")
							),
							action("return ret;")
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
							action("run();"),
							choice(
									sequence(
											terminal("INCR"),
											action("op = UnaryOp.PreIncrement;")
									),
									sequence(
											terminal("DECR"),
											action("op = UnaryOp.PreDecrement;")
									)
							),
							nonTerminal("ret", "UnaryExpression"),
							action("return dress(SUnaryExpr.make(op, ret));")
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
											action("run();"),
											choice(
													sequence(
															terminal("TILDE"),
															action("op = UnaryOp.Inverse;")
													),
													sequence(
															terminal("BANG"),
															action("op = UnaryOp.Not;")
													)
											),
											nonTerminal("ret", "UnaryExpression"),
											action("ret = dress(SUnaryExpr.make(op, ret));")
									),
									nonTerminal("ret", "CastExpression"),
									nonTerminal("ret", "PostfixExpression")
							),
							action("return ret;")
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
									action("lateRun();"),
									choice(
											sequence(
													terminal("INCR"),
													action("op = UnaryOp.PostIncrement;")
											),
											sequence(
													terminal("DECR"),
													action("op = UnaryOp.PostDecrement;")
											)
									),
									action("ret = dress(SUnaryExpr.make(op, ret));")
							),
							action("return ret;")
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
							action("run();"),
							terminal("LPAREN"),
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, listOf(
													expr("annotations").build()
											)),
											terminal("RPAREN"),
											nonTerminal("ret", "UnaryExpression"),
											action("ret = dress(SCastExpr.make(primitiveType, ret));")
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
											action("ret = dress(SCastExpr.make(type, ret));")
									)
							),
							action("return ret;")
					)
			),
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
									action("types = append(types, type);"),
									action("lateRun();"),
									oneOrMore(
											terminal("BIT_AND"),
											action("run();"),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, listOf(
													expr("annotations").build()
											)),
											action("types = append(types, type);")
									),
									action("type = dress(SIntersectionType.make(types));")
							),
							action("return type;")
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
							action("run();"),
							choice(
									sequence(
											terminal("literal", "INTEGER_LITERAL"),
											action("ret = SLiteralExpr.make(Integer.class, literal.image);")
									),
									sequence(
											terminal("literal", "LONG_LITERAL"),
											action("ret = SLiteralExpr.make(Long.class, literal.image);")
									),
									sequence(
											terminal("literal", "FLOAT_LITERAL"),
											action("ret = SLiteralExpr.make(Float.class, literal.image);")
									),
									sequence(
											terminal("literal", "DOUBLE_LITERAL"),
											action("ret = SLiteralExpr.make(Double.class, literal.image);")
									),
									sequence(
											terminal("literal", "CHARACTER_LITERAL"),
											action("ret = SLiteralExpr.make(Character.class, literal.image);")
									),
									sequence(
											terminal("literal", "STRING_LITERAL"),
											action("ret = SLiteralExpr.make(String.class, literal.image);")
									),
									sequence(
											terminal("literal", "TRUE"),
											action("ret = SLiteralExpr.make(Boolean.class, literal.image);")
									),
									sequence(
											terminal("literal", "FALSE"),
											action("ret = SLiteralExpr.make(Boolean.class, literal.image);")
									),
									sequence(
											terminal("literal", "NULL"),
											action("ret = SLiteralExpr.make(Void.class, literal.image);")
									)
							),
							action("return dress(ret);")
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
							action("return ret;")
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
									action("lateRun();"),
									nonTerminal("ret", "PrimarySuffix", null, listOf(
											expr("ret").build()
									))
							),
							action("return ret;")
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
									action("lateRun();"),
									nonTerminal("ret", "PrimarySuffixWithoutSuper", null, listOf(
											expr("ret").build()
									))
							),
							action("return ret;")
					)
			),
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
											action("run();"),
											terminal("THIS"),
											action("ret = dress(SThisExpr.make(none()));")
									),
									sequence(
											action("run();"),
											terminal("SUPER"),
											action("ret = dress(SSuperExpr.make(none()));"),
											choice(
													sequence(
															action("lateRun();"),
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
															action("lateRun();"),
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
											action("run();"),
											nonTerminal("type", "ResultType"),
											terminal("DOT"),
											terminal("CLASS"),
											action("ret = dress(SClassExpr.make(type));")
									),
									sequence(
											action("run();"),
											nonTerminal("type", "ResultType"),
											action("ret = STypeExpr.make(type);"),
											nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
													expr("ret").build()
											))
									),
									sequence(
											action("run();"),
											nonTerminal("ret", "MethodInvocation", null, listOf(
													expr("null").build()
											))
									),
									nonTerminal("ret", "Name"),
									sequence(
											action("run();"),
											terminal("LPAREN"),
											nonTerminal("ret", "Expression"),
											terminal("RPAREN"),
											action("ret = dress(SParenthesizedExpr.make(ret));")
									)
							),
							action("return ret;")
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
											action("ret = dress(SSuperExpr.make(optionOf(scope)));")
									),
									nonTerminal("ret", "MethodReferenceSuffix", null, listOf(
											expr("scope").build()
									))
							),
							action("return ret;")
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
															action("ret = dress(SThisExpr.make(optionOf(scope)));")
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
											action("ret = dress(SArrayAccessExpr.make(scope, ret));")
									)
							),
							action("return ret;")
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
							action("return dress(SFieldAccessExpr.make(optionOf(scope), name));")
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
							action("return dress(SMethodInvocationExpr.make(optionOf(scope), ensureNotNull(typeArgs), name, args));")
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
											nonTerminal("ret", "NodeListVar"),
											sequence(
													nonTerminal("expr", "Expression"),
													action("ret = append(ret, expr);"),
													zeroOrMore(
															terminal("COMMA"),
															nonTerminal("expr", "Expression"),
															action("ret = append(ret, expr);")
													)
											)
									)
							),
							terminal("RPAREN"),
							action("return ret;")
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
											action("name = SName.make(\"new\");")
									)
							),
							action("ret = dress(SMethodReferenceExpr.make(scope, ensureNotNull(typeArgs), name));"),
							action("return ret;")
					)
			),
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
							action("if (scope == null) run();"),
							terminal("NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action("run();"),
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
							action("return dress(SObjectCreationExpr.make(optionOf(scope), ensureNotNull(typeArgs), (BUTree<SQualifiedType>) type, args, optionOf(anonymousBody)));")
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
							action("if (scope == null) run();"),
							terminal("NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action("run();"),
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
							action("return ret;")
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
									action("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, none()));")
							),
							sequence(
									nonTerminal("arrayDims", "ArrayDimsMandatory"),
									nonTerminal("initializer", "ArrayInitializer"),
									action("return dress(SArrayCreationExpr.make(componentType, arrayDimExprs, arrayDims, optionOf(initializer)));")
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
									action("run();"),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									nonTerminal("expr", "Expression"),
									terminal("RBRACKET"),
									action("arrayDimExprs = append(arrayDimExprs, dress(SArrayDimExpr.make(annotations, expr)));")
							),
							action("return arrayDimExprs;")
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
									action("run();"),
									nonTerminal("annotations", "Annotations"),
									terminal("LBRACKET"),
									terminal("RBRACKET"),
									action("arrayDims = append(arrayDims, dress(SArrayDim.make(annotations)));")
							),
							action("return arrayDims;")
					)
			),
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
							action("return ret;")
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
							action("run();"),
							terminal("ASSERT"),
							nonTerminal("check", "Expression"),
							zeroOrOne(
									terminal("COLON"),
									nonTerminal("msg", "Expression")
							),
							terminal("SEMICOLON"),
							action("return dress(SAssertStmt.make(check, optionOf(msg)));")
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
							action("run();"),
							nonTerminal("label", "Name"),
							terminal("COLON"),
							nonTerminal("stmt", "Statement"),
							action("return dress(SLabeledStmt.make(label, stmt));")
					)
			),
			production("Block", type("BUTree<SBlockStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SNodeList> stmts;").build()
					),
					sequence(
							action("run();"),
							terminal("LBRACE"),
							nonTerminal("stmts", "Statements"),
							terminal("RBRACE"),
							action("return dress(SBlockStmt.make(ensureNotNull(stmts)));")
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
											action("run();"),
											action("run();"),
											nonTerminal("modifiers", "ModifiersNoDefault"),
											nonTerminal("typeDecl", "ClassOrInterfaceDecl", null, listOf(
													expr("modifiers").build()
											)),
											action("ret = dress(STypeDeclarationStmt.make(typeDecl));")
									),
									sequence(
											action("run();"),
											// TODO Rename LocalVariableDeclStmt and remove use of ExpressionStmt ?
											nonTerminal("expr", "VariableDeclExpression"),
											terminal("SEMICOLON"),
											action("ret = dress(SExpressionStmt.make(expr));")
									),
									nonTerminal("ret", "Statement")
							),
							action("return ret;")
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
							action("run();"),
							action("run();"),
							nonTerminal("modifiers", "ModifiersNoDefault"),
							nonTerminal("variableDecl", "VariableDecl", null, listOf(
									expr("modifiers").build()
							)),
							action("return dress(SVariableDeclarationExpr.make(variableDecl));")
					)
			),
			production("EmptyStatement", type("BUTree<SEmptyStmt>").build(),
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							action("run();"),
							terminal("SEMICOLON"),
							action("return dress(SEmptyStmt.make());")
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
							action("run();"),
							nonTerminal("expr", "StatementExpression"),
							terminal("SEMICOLON"),
							action("return dress(SExpressionStmt.make(expr));")
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
							action("return ret;")
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
							action("run();"),
							terminal("SWITCH"),
							terminal("LPAREN"),
							nonTerminal("selector", "Expression"),
							terminal("RPAREN"),
							terminal("LBRACE"),
							zeroOrMore(
									nonTerminal("entry", "SwitchEntry"),
									action("entries = append(entries, entry);")
							),
							terminal("RBRACE"),
							action("return dress(SSwitchStmt.make(selector, entries));")
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
							action("run();"),
							choice(
									sequence(
											terminal("CASE"),
											nonTerminal("label", "Expression")
									),
									terminal("DEFAULT")
							),
							terminal("COLON"),
							nonTerminal("stmts", "Statements"),
							action("return dress(SSwitchCase.make(optionOf(label), ensureNotNull(stmts)));")
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
							action("run();"),
							terminal("IF"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							nonTerminal("thenStmt", "Statement"),
							zeroOrOne(
									terminal("ELSE"),
									nonTerminal("elseStmt", "Statement")
							),
							action("return dress(SIfStmt.make(condition, thenStmt, optionOf(elseStmt)));")
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
							action("run();"),
							terminal("WHILE"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							nonTerminal("body", "Statement"),
							action("return dress(SWhileStmt.make(condition, body));")
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
							action("run();"),
							terminal("DO"),
							nonTerminal("body", "Statement"),
							terminal("WHILE"),
							terminal("LPAREN"),
							nonTerminal("condition", "Expression"),
							terminal("RPAREN"),
							terminal("SEMICOLON"),
							action("return dress(SDoStmt.make(body, condition));")
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
							action("run();"),
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
							action("if (varExpr != null)\n\treturn dress(SForeachStmt.make(varExpr, expr, body));\nelse\n\treturn dress(SForStmt.make(init, expr, update, body));")
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
											action("ret = append(emptyList(), expr);")
									),
									nonTerminal("ret", "StatementExpressionList")
							),
							action("return ret;")
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
							action("ret = append(ret, expr);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("expr", "StatementExpression"),
									action("ret = append(ret, expr);")
							),
							action("return ret;")
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
							action("return ret;")
					)
			),
			production("BreakStatement", type("BUTree<SBreakStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					sequence(
							action("run();"),
							terminal("BREAK"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal("SEMICOLON"),
							action("return dress(SBreakStmt.make(optionOf(id)));")
					)
			),
			production("ContinueStatement", type("BUTree<SContinueStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SName> id = null;").build()
					),
					sequence(
							action("run();"),
							terminal("CONTINUE"),
							zeroOrOne(
									nonTerminal("id", "Name")
							),
							terminal("SEMICOLON"),
							action("return dress(SContinueStmt.make(optionOf(id)));")
					)
			),
			production("ReturnStatement", type("BUTree<SReturnStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr = null;").build()
					),
					sequence(
							action("run();"),
							terminal("RETURN"),
							zeroOrOne(
									nonTerminal("expr", "Expression")
							),
							terminal("SEMICOLON"),
							action("return dress(SReturnStmt.make(optionOf(expr)));")
					)
			),
			production("ThrowStatement", type("BUTree<SThrowStmt>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<? extends SExpr> expr;").build()
					),
					sequence(
							action("run();"),
							terminal("THROW"),
							nonTerminal("expr", "Expression"),
							terminal("SEMICOLON"),
							action("return dress(SThrowStmt.make(expr));")
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
							action("run();"),
							terminal("SYNCHRONIZED"),
							terminal("LPAREN"),
							nonTerminal("expr", "Expression"),
							terminal("RPAREN"),
							nonTerminal("block", "Block"),
							action("return dress(SSynchronizedStmt.make(expr, block));")
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
							action("run();"),
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
							action("return dress(STryStmt.make(ensureNotNull(resources), trailingSemiColon.value, tryBlock, ensureNotNull(catchClauses), optionOf(finallyBlock)));")
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
									action("catchClauses = append(catchClauses, catchClause);")
							),
							action("return catchClauses;")
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
							action("run();"),
							terminal("CATCH"),
							terminal("LPAREN"),
							nonTerminal("param", "CatchFormalParameter"),
							terminal("RPAREN"),
							nonTerminal("catchBlock", "Block"),
							action("return dress(SCatchClause.make(param, catchBlock));")
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
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("exceptType", "QualifiedType", null, listOf(
									expr("null").build()
							)),
							action("exceptTypes = append(exceptTypes, exceptType);"),
							zeroOrOne(
									action("lateRun();"),
									oneOrMore(
											terminal("BIT_OR"),
											nonTerminal("exceptType", "AnnotatedQualifiedType"),
											action("exceptTypes = append(exceptTypes, exceptType);")
									),
									action("exceptType = dress(SUnionType.make(exceptTypes));")
							),
							nonTerminal("exceptId", "VariableDeclaratorId"),
							action("return dress(SFormalParameter.make(modifiers, exceptType, false, emptyList(), optionOf(exceptId), false, none()));")
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
							action("vars = append(vars, var);"),
							zeroOrMore(
									terminal("SEMICOLON"),
									nonTerminal("var", "VariableDeclExpression"),
									action("vars = append(vars, var);")
							),
							zeroOrOne(
									terminal("SEMICOLON"),
									action("trailingSemiColon.value = true;")
							),
							terminal("RPAREN"),
							action("return vars;")
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
							action("popNewWhitespaces(2);")
					)
			),
			production("RSIGNEDSHIFT", null,
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							terminal("GT"),
							terminal("GT"),
							action("popNewWhitespaces(1);")
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
									action("annotations = append(annotations, annotation);")
							),
							action("return annotations;")
					)
			),
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
							action("return ret;")
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
							action("run();"),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							terminal("LPAREN"),
							zeroOrOne(
									nonTerminal("pairs", "ElementValuePairList")
							),
							terminal("RPAREN"),
							action("return dress(SNormalAnnotationExpr.make(name, ensureNotNull(pairs)));")
					)
			),
			production("MarkerAnnotation", type("BUTree<SMarkerAnnotationExpr>").build(),
					emptyList(),
					emptyList(),
					listOf(
							stmt("BUTree<SQualifiedName> name;").build()
					),
					sequence(
							action("run();"),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							action("return dress(SMarkerAnnotationExpr.make(name));")
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
							action("run();"),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							terminal("LPAREN"),
							nonTerminal("value", "ElementValue"),
							terminal("RPAREN"),
							action("return dress(SSingleMemberAnnotationExpr.make(name, value));")
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
							action("ret = append(ret, pair);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("pair", "ElementValuePair"),
									action("ret = append(ret, pair);")
							),
							action("return ret;")
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
							action("run();"),
							nonTerminal("name", "Name"),
							terminal("ASSIGN"),
							nonTerminal("value", "ElementValue"),
							action("return dress(SMemberValuePair.make(name, value));")
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
							action("return ret;")
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
							action("run();"),
							terminal("LBRACE"),
							zeroOrOne(
									nonTerminal("values", "ElementValueList")
							),
							zeroOrOne(
									terminal("COMMA"),
									action("trailingComma = true;")
							),
							terminal("RBRACE"),
							action("return dress(SArrayInitializerExpr.make(ensureNotNull(values), trailingComma));")
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
							action("ret = append(ret, value);"),
							zeroOrMore(
									terminal("COMMA"),
									nonTerminal("value", "ElementValue"),
									action("ret = append(ret, value);")
							),
							action("return ret;")
					)
			)
	);
}
