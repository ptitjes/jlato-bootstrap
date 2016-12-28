package org.jlato.cc;

import org.jlato.cc.grammar.GProductions;
import org.jlato.pattern.Quotes;
import org.jlato.tree.NodeList;
import org.jlato.tree.decl.FormalParameter;
import org.jlato.tree.expr.Expr;
import org.jlato.tree.stmt.Stmt;

import java.util.HashMap;
import java.util.Map;

import static org.jlato.tree.Trees.emptyList;
import static org.jlato.tree.Trees.listOf;
import static org.jlato.cc.grammar.GExpansion.*;
import static org.jlato.cc.grammar.GProduction.*;
import static org.jlato.tree.Trees.voidType;

public class JavaGrammar {

	public static Map<String, Integer> terminals = new HashMap<>();

	static {
		terminals.put("EOF", 0);
		terminals.put("WHITESPACE", 1);
		terminals.put("NEWLINE", 2);
		terminals.put("SINGLE_LINE_COMMENT", 3);
		terminals.put("JAVA_DOC_COMMENT", 6);
		terminals.put("MULTI_LINE_COMMENT", 7);
		terminals.put("ABSTRACT", 9);
		terminals.put("ASSERT", 10);
		terminals.put("BOOLEAN", 11);
		terminals.put("BREAK", 12);
		terminals.put("BYTE", 13);
		terminals.put("CASE", 14);
		terminals.put("CATCH", 15);
		terminals.put("CHAR", 16);
		terminals.put("CLASS", 17);
		terminals.put("CONST", 18);
		terminals.put("CONTINUE", 19);
		terminals.put("DEFAULT", 20);
		terminals.put("DO", 21);
		terminals.put("DOUBLE", 22);
		terminals.put("ELSE", 23);
		terminals.put("ENUM", 24);
		terminals.put("EXTENDS", 25);
		terminals.put("FALSE", 26);
		terminals.put("FINAL", 27);
		terminals.put("FINALLY", 28);
		terminals.put("FLOAT", 29);
		terminals.put("FOR", 30);
		terminals.put("GOTO", 31);
		terminals.put("IF", 32);
		terminals.put("IMPLEMENTS", 33);
		terminals.put("IMPORT", 34);
		terminals.put("INSTANCEOF", 35);
		terminals.put("INT", 36);
		terminals.put("INTERFACE", 37);
		terminals.put("LONG", 38);
		terminals.put("NATIVE", 39);
		terminals.put("NEW", 40);
		terminals.put("NULL", 41);
		terminals.put("PACKAGE", 42);
		terminals.put("PRIVATE", 43);
		terminals.put("PROTECTED", 44);
		terminals.put("PUBLIC", 45);
		terminals.put("RETURN", 46);
		terminals.put("SHORT", 47);
		terminals.put("STATIC", 48);
		terminals.put("STRICTFP", 49);
		terminals.put("SUPER", 50);
		terminals.put("SWITCH", 51);
		terminals.put("SYNCHRONIZED", 52);
		terminals.put("THIS", 53);
		terminals.put("THROW", 54);
		terminals.put("THROWS", 55);
		terminals.put("TRANSIENT", 56);
		terminals.put("TRUE", 57);
		terminals.put("TRY", 58);
		terminals.put("VOID", 59);
		terminals.put("VOLATILE", 60);
		terminals.put("WHILE", 61);
		terminals.put("LONG_LITERAL", 62);
		terminals.put("INTEGER_LITERAL", 63);
		terminals.put("FLOAT_LITERAL", 68);
		terminals.put("DOUBLE_LITERAL", 69);
		terminals.put("CHARACTER_LITERAL", 78);
		terminals.put("STRING_LITERAL", 79);
		terminals.put("LPAREN", 80);
		terminals.put("RPAREN", 81);
		terminals.put("LBRACE", 82);
		terminals.put("RBRACE", 83);
		terminals.put("LBRACKET", 84);
		terminals.put("RBRACKET", 85);
		terminals.put("SEMICOLON", 86);
		terminals.put("COMMA", 87);
		terminals.put("DOT", 88);
		terminals.put("AT", 89);
		terminals.put("ASSIGN", 90);
		terminals.put("LT", 91);
		terminals.put("BANG", 92);
		terminals.put("TILDE", 93);
		terminals.put("HOOK", 94);
		terminals.put("COLON", 95);
		terminals.put("EQ", 96);
		terminals.put("LE", 97);
		terminals.put("GE", 98);
		terminals.put("NE", 99);
		terminals.put("SC_OR", 100);
		terminals.put("SC_AND", 101);
		terminals.put("INCR", 102);
		terminals.put("DECR", 103);
		terminals.put("PLUS", 104);
		terminals.put("MINUS", 105);
		terminals.put("STAR", 106);
		terminals.put("SLASH", 107);
		terminals.put("BIT_AND", 108);
		terminals.put("BIT_OR", 109);
		terminals.put("XOR", 110);
		terminals.put("REM", 111);
		terminals.put("LSHIFT", 112);
		terminals.put("PLUSASSIGN", 113);
		terminals.put("MINUSASSIGN", 114);
		terminals.put("STARASSIGN", 115);
		terminals.put("SLASHASSIGN", 116);
		terminals.put("ANDASSIGN", 117);
		terminals.put("ORASSIGN", 118);
		terminals.put("XORASSIGN", 119);
		terminals.put("REMASSIGN", 120);
		terminals.put("LSHIFTASSIGN", 121);
		terminals.put("RSIGNEDSHIFTASSIGN", 122);
		terminals.put("RUNSIGNEDSHIFTASSIGN", 123);
		terminals.put("ELLIPSIS", 124);
		terminals.put("ARROW", 125);
		terminals.put("DOUBLECOLON", 126);
		terminals.put("RUNSIGNEDSHIFT", 127);
		terminals.put("RSIGNEDSHIFT", 128);
		terminals.put("GT", 129);
		terminals.put("NODE_VARIABLE", 130);
		terminals.put("NODE_LIST_VARIABLE", 131);
		terminals.put("IDENTIFIER", 132);
	}

	public static GProductions productions = new GProductions(

			// Entry productions

			production("CompilationUnitEntry", "BUTree<SCompilationUnit>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SCompilationUnit> ret;"),
					sequence(
							action("entryPoint = COMPILATION_UNIT_ENTRY;"),
							nonTerminal("ret", "CompilationUnit"),
							action("return ret;")
					)
			),
			production("PackageDeclEntry", "BUTree<SPackageDecl>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SPackageDecl> ret;"),
					sequence(
							action("entryPoint = PACKAGE_DECL_ENTRY;"),
							nonTerminal("ret", "PackageDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("ImportDeclEntry", "BUTree<SImportDecl>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SImportDecl> ret;"),
					sequence(
							action("entryPoint = IMPORT_DECL_ENTRY;"),
							nonTerminal("ret", "ImportDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("TypeDeclEntry", "BUTree<? extends STypeDecl>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends STypeDecl> ret;"),
					sequence(
							action("entryPoint = TYPE_DECL_ENTRY;"),
							nonTerminal("ret", "TypeDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("MemberDeclEntry", "BUTree<? extends SMemberDecl>",
					params("TypeKind typeKind"),
					emptyList(),
					stmts("BUTree<? extends SMemberDecl> ret;"),
					sequence(
							action("entryPoint = MEMBER_DECL_ENTRY;"),
							nonTerminal("ret", "ClassOrInterfaceBodyDecl", exprs("typeKind")),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("AnnotationMemberDeclEntry", "BUTree<? extends SMemberDecl>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SMemberDecl> ret;"),
					sequence(
							action("entryPoint = ANNOTATION_MEMBER_DECL_ENTRY;"),
							// TODO Rename AnnotationMemberDecl
							nonTerminal("ret", "AnnotationTypeBodyDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("ModifiersEntry", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SNodeList> ret;"),
					sequence(
							action("entryPoint = MODIFIERS_ENTRY;"),
							nonTerminal("ret", "Modifiers"),
							nonTerminal("Epilog"),
							action("return ret;")
					)
			),
			production("AnnotationsEntry", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SNodeList> ret;"),
					sequence(
							action("entryPoint = ANNOTATIONS_ENTRY;"),
							nonTerminal("ret", "Annotations"),
							nonTerminal("Epilog"),
							action("return ret;")
					)
			),

			production("MethodDeclEntry", "BUTree<SMethodDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<SMethodDecl> ret;"
					),
					sequence(
							action("entryPoint = METHOD_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "MethodDecl", exprs("modifiers")),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("FieldDeclEntry", "BUTree<SFieldDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<SFieldDecl> ret;"
					),
					sequence(
							action("entryPoint = FIELD_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("ret", "FieldDecl", exprs("modifiers")),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("AnnotationElementDeclEntry", "BUTree<SAnnotationMemberDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<SAnnotationMemberDecl> ret;"
					),
					sequence(
							action("entryPoint = ANNOTATION_ELEMENT_DECL_ENTRY;"),
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							// TODO Rename AnnotationElementDecl
							nonTerminal("ret", "AnnotationTypeMemberDecl", exprs("modifiers")),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("EnumConstantDeclEntry", "BUTree<SEnumConstantDecl>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SEnumConstantDecl> ret;"),
					sequence(
							action("entryPoint = ENUM_CONSTANT_DECL_ENTRY;"),
							nonTerminal("ret", "EnumConstantDecl"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("FormalParameterEntry", "BUTree<SFormalParameter>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SFormalParameter> ret;"),
					sequence(
							action("entryPoint = FORMAL_PARAMETER_ENTRY;"),
							nonTerminal("ret", "FormalParameter"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("TypeParameterEntry", "BUTree<STypeParameter>",
					emptyList(),
					emptyList(),
					stmts("BUTree<STypeParameter> ret;"),
					sequence(
							action("entryPoint = TYPE_PARAMETER_ENTRY;"),
							nonTerminal("ret", "TypeParameter"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("StatementsEntry", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SNodeList> ret;"),
					sequence(
							action("entryPoint = STATEMENTS_ENTRY;"),
							nonTerminal("ret", "Statements"),
							nonTerminal("Epilog"),
							action("return ret;")
					)
			),
			production("BlockStatementEntry", "BUTree<? extends SStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SStmt> ret;"),
					sequence(
							action("entryPoint = BLOCK_STATEMENT_ENTRY;"),
							nonTerminal("ret", "BlockStatement"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("ExpressionEntry", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							action("entryPoint = EXPRESSION_ENTRY;"),
							nonTerminal("ret", "Expression"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),


			production("TypeEntry", "BUTree<? extends SType>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations;",
							"BUTree<? extends SType> ret;"
					),
					sequence(
							action("entryPoint = TYPE_ENTRY;"),
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "Type", exprs("annotations")),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("QualifiedNameEntry", "BUTree<SQualifiedName>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SQualifiedName> ret;"),
					sequence(
							action("entryPoint = QUALIFIED_NAME_ENTRY;"),
							nonTerminal("ret", "QualifiedName"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),
			production("NameEntry", "BUTree<SName>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SName> ret;"),
					sequence(
							action("entryPoint = NAME_ENTRY;"),
							nonTerminal("ret", "Name"),
							nonTerminal("Epilog"),
							action("return dressWithPrologAndEpilog(ret);")
					)
			),

			production("Epilog", voidType(),
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							terminal("EOF")
					)
			),

			// Main productions

			production("NodeListVar", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts("Token id;"),
					sequence(
							terminal("id", "NODE_LIST_VARIABLE"),
							action("return makeVar(id);")
					)
			),
			production("NodeVar", "BUTree<SName>",
					emptyList(),
					emptyList(),
					stmts("Token id;"),
					sequence(
							terminal("id", "NODE_VARIABLE"),
							action("return makeVar(id);")
					)
			),
			production("CompilationUnit", "BUTree<SCompilationUnit>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SPackageDecl> packageDecl = null;",
							"BUTree<SNodeList> imports;",
							"BUTree<SNodeList> types;",
							"BUTree<SCompilationUnit> compilationUnit;"
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
			production("PackageDecl", "BUTree<SPackageDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations = null;",
							"BUTree<SQualifiedName> name;"
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
			production("ImportDecls", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> imports = emptyList();",
							"BUTree<SImportDecl> importDecl = null;"
					),
					sequence(
							zeroOrMore(
									nonTerminal("importDecl", "ImportDecl"),
									action("imports = append(imports, importDecl);")
							),
							action("return imports;")
					)
			),
			production("ImportDecl", "BUTree<SImportDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SQualifiedName> name;",
							"boolean isStatic = false;",
							"boolean isAsterisk = false;"
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
			production("TypeDecls", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> types = emptyList();",
							"BUTree<? extends STypeDecl> typeDecl = null;"
					),
					sequence(
							zeroOrMore(
									nonTerminal("typeDecl", "TypeDecl"),
									action("types = append(types, typeDecl);")
							),
							action("return types;")
					)
			),
			production("Modifiers", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers = emptyList();",
							"BUTree<? extends SAnnotationExpr> ann;"
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
			production("ModifiersNoDefault", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers = emptyList();",
							"BUTree<? extends SAnnotationExpr> ann;"
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
			production("TypeDecl", "BUTree<? extends STypeDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<? extends STypeDecl> ret;"
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
													nonTerminal("ret", "ClassOrInterfaceDecl", null, exprs("modifiers")),
													nonTerminal("ret", "EnumDecl", null, exprs("modifiers")),
													nonTerminal("ret", "AnnotationTypeDecl", null, exprs("modifiers"))
											)
									)
							),
							action("return ret;")
					)
			),
			production("ClassOrInterfaceDecl", "BUTree<? extends STypeDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"TypeKind typeKind;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> typeParams = null;",
							"BUTree<SQualifiedType> superClassType = null;",
							"BUTree<SNodeList> extendsClause = null;",
							"BUTree<SNodeList> implementsClause = null;",
							"BUTree<SNodeList> members;",
							"ByRef<BUProblem> problem = new ByRef<BUProblem>(null);"
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
													nonTerminal("implementsClause", "ImplementsList", null, exprs(
															"typeKind",
															"problem"
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
							nonTerminal("members", "ClassOrInterfaceBody", null, exprs("typeKind")),
							action("if (typeKind == TypeKind.Interface)\n\treturn dress(SInterfaceDecl.make(modifiers, name, ensureNotNull(typeParams), ensureNotNull(extendsClause), members)).withProblem(problem.value);\nelse {\n\treturn dress(SClassDecl.make(modifiers, name, ensureNotNull(typeParams), optionOf(superClassType), ensureNotNull(implementsClause), members));\n}")
					)
			),
			production("ExtendsList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SQualifiedType> cit;",
							"BUTree<SNodeList> annotations = null;"
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
			production("ImplementsList", "BUTree<SNodeList>",
					emptyList(),
					params(
							"TypeKind typeKind",
							"ByRef<BUProblem> problem"
					),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SQualifiedType> cit;",
							"BUTree<SNodeList> annotations = null;"
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
			production("EnumDecl", "BUTree<? extends STypeDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<SName> name;",
							"BUTree<SNodeList> implementsClause = emptyList();",
							"BUTree<SEnumConstantDecl> entry;",
							"BUTree<SNodeList> constants = emptyList();",
							"boolean trailingComma = false;",
							"BUTree<SNodeList> members = null;",
							"ByRef<BUProblem> problem = new ByRef<BUProblem>(null);"
					),
					sequence(
							terminal("ENUM"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("implementsClause", "ImplementsList", null, exprs(
											"TypeKind.Enum",
											"problem"
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
									nonTerminal("members", "ClassOrInterfaceBodyDecls", null, exprs("TypeKind.Enum"))
							),
							terminal("RBRACE"),
							action("return dress(SEnumDecl.make(modifiers, name, implementsClause, constants, trailingComma, ensureNotNull(members))).withProblem(problem.value);")
					)
			),
			production("EnumConstantDecl", "BUTree<SEnumConstantDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers = null;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> args = null;",
							"BUTree<SNodeList> classBody = null;"
					),
					sequence(
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("name", "Name"),
							zeroOrOne(
									nonTerminal("args", "Arguments")
							),
							zeroOrOne(
									nonTerminal("classBody", "ClassOrInterfaceBody", null, exprs("TypeKind.Class"))
							),
							action("return dress(SEnumConstantDecl.make(modifiers, name, optionOf(args), optionOf(classBody)));")
					)
			),
			production("AnnotationTypeDecl", "BUTree<SAnnotationDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<SName> name;",
							"BUTree<SNodeList> members;"
					),
					sequence(
							terminal("AT"),
							terminal("INTERFACE"),
							nonTerminal("name", "Name"),
							nonTerminal("members", "AnnotationTypeBody"),
							action("return dress(SAnnotationDecl.make(modifiers, name, members));")
					)
			),
			production("AnnotationTypeBody", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SMemberDecl> member;"
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
			production("AnnotationTypeBodyDecl", "BUTree<? extends SMemberDecl>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<? extends SMemberDecl> ret;"
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
													nonTerminal("ret", "AnnotationTypeMemberDecl", null, exprs("modifiers")),
													nonTerminal("ret", "ClassOrInterfaceDecl", null, exprs("modifiers")),
													nonTerminal("ret", "EnumDecl", null, exprs("modifiers")),
													nonTerminal("ret", "AnnotationTypeDecl", null, exprs("modifiers")),
													nonTerminal("ret", "FieldDecl", null, exprs("modifiers"))
											)
									)
							),
							action("return ret;")
					)
			),
			production("AnnotationTypeMemberDecl", "BUTree<SAnnotationMemberDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<? extends SType> type;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> dims;",
							"BUTree<SNodeOption> defaultValue = none();",
							"BUTree<? extends SExpr> value = null;"
					),
					sequence(
							nonTerminal("type", "Type", null, exprs("null")),
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
			production("TypeParameters", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<STypeParameter> tp;"
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
			production("TypeParameter", "BUTree<STypeParameter>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations = null;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> typeBounds = null;"
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
			production("TypeBounds", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SQualifiedType> cit;",
							"BUTree<SNodeList> annotations = null;"
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
			production("ClassOrInterfaceBody", "BUTree<SNodeList>",
					emptyList(),
					params("TypeKind typeKind"),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SMemberDecl> member;"
					),
					sequence(
							terminal("LBRACE"),
							nonTerminal("ret", "ClassOrInterfaceBodyDecls", null, exprs("typeKind")),
							terminal("RBRACE"),
							action("return ret;")
					)
			),
			production("ClassOrInterfaceBodyDecls", "BUTree<SNodeList>",
					emptyList(),
					params("TypeKind typeKind"),
					stmts(
							"BUTree<? extends SMemberDecl> member;",
							"BUTree<SNodeList> ret = emptyList();"
					),
					sequence(
							zeroOrOne(
									choice(
											nonTerminal("ret", "NodeListVar"),
											oneOrMore(
													nonTerminal("member", "ClassOrInterfaceBodyDecl", null, exprs("typeKind")),
													action("ret = append(ret, member);")
											)
									)
							),
							action("return ret;")
					)
			),
			production("ClassOrInterfaceBodyDecl", "BUTree<? extends SMemberDecl>",
					emptyList(),
					params("TypeKind typeKind"),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<? extends SMemberDecl> ret;",
							"BUProblem problem = null;"
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
															nonTerminal("ret", "InitializerDecl", null, exprs("modifiers")),
															action("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have initializers\"));")
													),
													nonTerminal("ret", "ClassOrInterfaceDecl", null, exprs("modifiers")),
													nonTerminal("ret", "EnumDecl", null, exprs("modifiers")),
													nonTerminal("ret", "AnnotationTypeDecl", null, exprs("modifiers")),
													sequence(
															nonTerminal("ret", "ConstructorDecl", null, exprs("modifiers")),
															action("if (typeKind == TypeKind.Interface) ret = ret.withProblem(new BUProblem(Severity.ERROR, \"An interface cannot have constructors\"));")
													),
													sequence(
															nonTerminal("ret", "FieldDecl", null, exprs("modifiers"))
													),
													nonTerminal("ret", "MethodDecl", null, exprs("modifiers"))
											)
									)
							),
							action("return ret.withProblem(problem);")
					)
			),
			production("FieldDecl", "BUTree<SFieldDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> variables = emptyList();",
							"BUTree<SVariableDeclarator> val;"
					),
					sequence(
							nonTerminal("type", "Type", null, exprs("null")),
							nonTerminal("variables", "VariableDeclarators"),
							terminal("SEMICOLON"),
							action("return dress(SFieldDecl.make(modifiers, type, variables));")
					)
			),
			production("VariableDecl", "BUTree<SLocalVariableDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> variables = emptyList();"
					),
					sequence(
							nonTerminal("type", "Type", null, exprs("null")),
							nonTerminal("variables", "VariableDeclarators"),
							action("return dress(SLocalVariableDecl.make(modifiers, type, variables));")
					)
			),
			production("VariableDeclarators", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> variables = emptyList();",
							"BUTree<SVariableDeclarator> val;"
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
			production("VariableDeclarator", "BUTree<SVariableDeclarator>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SVariableDeclaratorId> id;",
							"BUTree<SNodeOption> init = none();",
							"BUTree<? extends SExpr> initExpr = null;"
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
			production("VariableDeclaratorId", "BUTree<SVariableDeclaratorId>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SName> name;",
							"BUTree<SNodeList> arrayDims;"
					),
					sequence(
							action("run();"),
							nonTerminal("name", "Name"),
							nonTerminal("arrayDims", "ArrayDims"),
							action("return dress(SVariableDeclaratorId.make(name, arrayDims));")
					)
			),
			production("ArrayDims", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> arrayDims = emptyList();",
							"BUTree<SNodeList> annotations;"
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
			production("VariableInitializer", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							choice(
									nonTerminal("ret", "ArrayInitializer"),
									nonTerminal("ret", "Expression")
							),
							action("return ret;")
					)
			),
			production("ArrayInitializer", "BUTree<SArrayInitializerExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> values = emptyList();",
							"BUTree<? extends SExpr> val;",
							"boolean trailingComma = false;"
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
			production("MethodDecl", "BUTree<SMethodDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<SNodeList> typeParameters = null;",
							"BUTree<SNodeList> additionalAnnotations = null;",
							"BUTree<? extends SType> type;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> parameters;",
							"BUTree<SNodeList> arrayDims;",
							"BUTree<SNodeList> throwsClause = null;",
							"BUTree<SBlockStmt> block = null;",
							"BUProblem problem = null;"
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
			production("FormalParameters", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = null;",
							"BUTree<SFormalParameter> par;"
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
			production("FormalParameterList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = null;",
							"BUTree<SFormalParameter> par;"
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
			production("FormalParameter", "BUTree<SFormalParameter>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> ellipsisAnnotations = null;",
							"boolean isVarArg = false;",
							"BUTree<SVariableDeclaratorId> id = null;",
							"boolean isReceiver = false;",
							"BUTree<SName> receiverTypeName = null;"
					),
					sequence(
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("type", "Type", null, exprs("null")),
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
			production("ThrowsClause", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SQualifiedType> cit;"
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
			production("ConstructorDecl", "BUTree<SConstructorDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts(
							"BUTree<SNodeList> typeParameters = null;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> parameters;",
							"BUTree<SNodeList> throwsClause = null;",
							"BUTree<SExplicitConstructorInvocationStmt> exConsInv = null;",
							"BUTree<SBlockStmt> block;",
							"BUTree<SNodeList> stmts = emptyList();",
							"BUTree<? extends SStmt> stmt;"
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
			production("ExplicitConstructorInvocation", "BUTree<SExplicitConstructorInvocationStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"boolean isThis = false;",
							"BUTree<SNodeList> args;",
							"BUTree<? extends SExpr> expr = null;",
							"BUTree<SNodeList> typeArgs = null;"
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
			production("Statements", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = null;",
							"BUTree<? extends SStmt> stmt;"
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
			production("InitializerDecl", "BUTree<SInitializerDecl>",
					emptyList(),
					params("BUTree<SNodeList> modifiers"),
					stmts("BUTree<SBlockStmt> block;"),
					sequence(
							nonTerminal("block", "Block"),
							action("return dress(SInitializerDecl.make(modifiers, block));")
					)
			),
			production("Type", "BUTree<? extends SType>",
					emptyList(),
					params("BUTree<SNodeList> annotations"),
					stmts(
							"BUTree<? extends SType> type = null;",
							"BUTree<SNodeList> arrayDims;"
					),
					sequence(
							choice(
									nonTerminal("type", "PrimitiveType", null, exprs("annotations")),
									nonTerminal("type", "QualifiedType", null, exprs("annotations"))
							),
							zeroOrOne(
									action("lateRun();"),
									nonTerminal("arrayDims", "ArrayDimsMandatory"),
									action("type = dress(SArrayType.make(type, arrayDims));")
							),
							action("return type;")
					)
			),
			production("ReferenceType", "BUTree<? extends SReferenceType>",
					emptyList(),
					params("BUTree<SNodeList> annotations"),
					stmts(
							"BUTree<? extends SType> primitiveType;",
							"BUTree<? extends SReferenceType> type;",
							"BUTree<SNodeList> arrayDims;"
					),
					sequence(
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, exprs("annotations")),
											action("lateRun();"),
											nonTerminal("arrayDims", "ArrayDimsMandatory"),
											action("type = dress(SArrayType.make(primitiveType, arrayDims));")
									),
									sequence(
											nonTerminal("type", "QualifiedType", null, exprs("annotations")),
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
			production("QualifiedType", "BUTree<SQualifiedType>",
					emptyList(),
					params("BUTree<SNodeList> annotations"),
					stmts(
							"BUTree<SNodeOption> scope = none();",
							"BUTree<SQualifiedType> ret;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> typeArgs = null;"
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
			production("TypeArguments", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = null;",
							"BUTree<? extends SType> type;"
					),
					sequence(
							terminal("LT"),
							nonTerminal("ret", "TypeArgumentList"),
							terminal("GT"),
							action("return ret;")
					)
			),
			production("TypeArgumentsOrDiamond", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SType> type;"
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
			production("TypeArgumentList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SType> type;"
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
			production("TypeArgument", "BUTree<? extends SType>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SType> ret;",
							"BUTree<SNodeList> annotations = null;"
					),
					sequence(
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							choice(
									nonTerminal("ret", "ReferenceType", null, exprs("annotations")),
									nonTerminal("ret", "Wildcard", null, exprs("annotations"))
							),
							action("return ret;")
					)
			),
			production("Wildcard", "BUTree<SWildcardType>",
					emptyList(),
					params("BUTree<SNodeList> annotations"),
					stmts(
							"BUTree<? extends SReferenceType> ext = null;",
							"BUTree<? extends SReferenceType> sup = null;",
							"BUTree<SNodeList> boundAnnotations = null;"
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
													nonTerminal("ext", "ReferenceType", null, exprs("boundAnnotations"))
											),
											sequence(
													terminal("SUPER"),
													action("run();"),
													nonTerminal("boundAnnotations", "Annotations"),
													nonTerminal("sup", "ReferenceType", null, exprs("boundAnnotations"))
											)
									)
							),
							action("return dress(SWildcardType.make(annotations, optionOf(ext), optionOf(sup)));")
					)
			),
			production("PrimitiveType", "BUTree<SPrimitiveType>",
					emptyList(),
					params("BUTree<SNodeList> annotations"),
					stmts("Primitive primitive;"),
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
			production("ResultType", "BUTree<? extends SType>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SType> ret;"),
					sequence(
							choice(
									sequence(
											action("run();"),
											terminal("VOID"),
											action("ret = dress(SVoidType.make());")
									),
									nonTerminal("ret", "Type", null, exprs("null"))
							),
							action("return ret;")
					)
			),
			production("AnnotatedQualifiedType", "BUTree<SQualifiedType>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations;",
							"BUTree<SQualifiedType> ret;"
					),
					sequence(
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("ret", "QualifiedType", null, exprs("annotations")),
							action("return ret;")
					)
			),
			production("QualifiedName", "BUTree<SQualifiedName>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeOption> qualifier = none();",
							"BUTree<SQualifiedName> ret = null;",
							"BUTree<SName> name;"
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
			production("Name", "BUTree<SName>",
					emptyList(),
					emptyList(),
					stmts(
							"Token id;",
							"BUTree<SName> name;"
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
			production("Expression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							choice(
									nonTerminal("ret", "AssignmentExpression"),
									nonTerminal("ret", "LambdaExpression")
							),
							action("return ret;")
					)
			),
			production("AssignmentExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"AssignOp op;",
							"BUTree<? extends SExpr> expr;"
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
			production("LambdaExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations;",
							"BUTree<? extends SType> type;",
							"BUTree<? extends SExpr> ret;"
					),
					sequence(
							choice(
									sequence(
											action("run();"),
											terminal("LPAREN"),
											action("run();"),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, exprs("annotations")),
											nonTerminal("type", "ReferenceCastTypeRest", null, exprs("type")),
											terminal("RPAREN"),
											nonTerminal("ret", "LambdaExpression"),
											action("ret = dress(SCastExpr.make(type, ret));")
									),
									nonTerminal("ret", "LambdaExpressionWithoutCast")
							),
							action("return ret;")
					)
			),
			production("LambdaExpressionWithoutCast", "BUTree<SLambdaExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SLambdaExpr> ret;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> params;"
					),
					sequence(
							action("run();"),
							choice(
									sequence(
											nonTerminal("name", "Name"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, exprs(
													"singletonList(makeFormalParameter(name))",
													"false"
											))
									),
									sequence(
											terminal("LPAREN"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, exprs(
													"emptyList()",
													"true"
											))
									),
									sequence(
											terminal("LPAREN"),
											nonTerminal("params", "InferredFormalParameterList"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, exprs(
													"params",
													"true"
											))
									),
									sequence(
											terminal("LPAREN"),
											nonTerminal("params", "FormalParameterList"),
											terminal("RPAREN"),
											terminal("ARROW"),
											nonTerminal("ret", "LambdaBody", null, exprs(
													"params",
													"true"
											))
									)
							),
							action("return ret;")
					)
			),
			production("LambdaBody", "BUTree<SLambdaExpr>",
					emptyList(),
					params(
							"BUTree<SNodeList> parameters",
							"boolean parenthesis"
					),
					stmts(
							"BUTree<SBlockStmt> block;",
							"BUTree<? extends SExpr> expr;",
							"BUTree<SLambdaExpr> ret;"
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
			production("InferredFormalParameterList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SFormalParameter> param;"
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
			production("InferredFormalParameter", "BUTree<SFormalParameter>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SName> name;"),
					sequence(
							nonTerminal("name", "Name"),
							action("return makeFormalParameter(name);")
					)
			),
			production("AssignmentOperator", "AssignOp",
					emptyList(),
					emptyList(),
					stmts("AssignOp ret;"),
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
			production("ConditionalExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> left;",
							"BUTree<? extends SExpr> right;"
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
			production("ConditionalOrExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;"
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
			production("ConditionalAndExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;"
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
			production("InclusiveOrExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;"
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
			production("ExclusiveOrExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;"
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
			production("AndExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;"
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
			production("EqualityExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;",
							"BinaryOp op;"
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
			production("InstanceOfExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<SNodeList> annotations;",
							"BUTree<? extends SType> type;"
					),
					sequence(
							nonTerminal("ret", "RelationalExpression"),
							zeroOrOne(
									action("lateRun();"),
									terminal("INSTANCEOF"),
									action("run();"),
									nonTerminal("annotations", "Annotations"),
									nonTerminal("type", "Type", null, exprs("annotations")),
									action("ret = dress(SInstanceOfExpr.make(ret, type));")
							),
							action("return ret;")
					)
			),
			production("RelationalExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;",
							"BinaryOp op;"
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
			production("ShiftExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;",
							"BinaryOp op;"
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
													terminal("GT"),
													terminal("GT"),
													terminal("GT"),
													action("popNewWhitespaces(2);"),
													action("op = BinaryOp.RightUnsignedShift;")
											),
											sequence(
													terminal("GT"),
													terminal("GT"),
													action("popNewWhitespaces(1);"),
													action("op = BinaryOp.RightSignedShift;")
											)
									),
									nonTerminal("right", "AdditiveExpression"),
									action("ret = dress(SBinaryExpr.make(ret, op, right));")
							),
							action("return ret;")
					)
			),
			production("AdditiveExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;",
							"BinaryOp op;"
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
			production("MultiplicativeExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SExpr> right;",
							"BinaryOp op;"
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
			production("UnaryExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"UnaryOp op;"
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
			production("PrefixExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"UnaryOp op;",
							"BUTree<? extends SExpr> ret;"
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
			production("UnaryExpressionNotPlusMinus", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"UnaryOp op;"
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
			production("PostfixExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"UnaryOp op;"
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
			production("CastExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations = null;",
							"BUTree<? extends SType> primitiveType;",
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> arrayDims;",
							"BUTree<? extends SExpr> ret;"
					),
					sequence(
							action("run();"),
							terminal("LPAREN"),
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							choice(
									sequence(
											nonTerminal("primitiveType", "PrimitiveType", null, exprs("annotations")),
											terminal("RPAREN"),
											nonTerminal("ret", "UnaryExpression"),
											action("ret = dress(SCastExpr.make(primitiveType, ret));")
									),
									sequence(
											nonTerminal("type", "ReferenceType", null, exprs("annotations")),
											nonTerminal("type", "ReferenceCastTypeRest", null, exprs("type")),
											terminal("RPAREN"),
											nonTerminal("ret", "UnaryExpressionNotPlusMinus"),
											action("ret = dress(SCastExpr.make(type, ret));")
									)
							),
							action("return ret;")
					)
			),
			production("ReferenceCastTypeRest", "BUTree<? extends SType>",
					emptyList(),
					params("BUTree<? extends SType> type"),
					stmts(
							"BUTree<SNodeList> types = emptyList();",
							"BUTree<SNodeList> annotations = null;"
					),
					sequence(
							zeroOrOne(
									action("types = append(types, type);"),
									action("lateRun();"),
									oneOrMore(
											terminal("BIT_AND"),
											action("run();"),
											nonTerminal("annotations", "Annotations"),
											nonTerminal("type", "ReferenceType", null, exprs("annotations")),
											action("types = append(types, type);")
									),
									action("type = dress(SIntersectionType.make(types));")
							),
							action("return type;")
					)
			),
			production("Literal", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"Token literal;",
							"BUTree<? extends SExpr> ret;"
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
			production("PrimaryExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							choice(
									nonTerminal("ret", "PrimaryNoNewArray"),
									nonTerminal("ret", "ArrayCreationExpr", null, exprs("null"))
							),
							action("return ret;")
					)
			),
			production("PrimaryNoNewArray", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									action("lateRun();"),
									nonTerminal("ret", "PrimarySuffix", null, exprs("ret"))
							),
							action("return ret;")
					)
			),
			production("PrimaryExpressionWithoutSuperSuffix", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							nonTerminal("ret", "PrimaryPrefix"),
							zeroOrMore(
									action("lateRun();"),
									nonTerminal("ret", "PrimarySuffixWithoutSuper", null, exprs("ret"))
							),
							action("return ret;")
					)
			),
			production("PrimaryPrefix", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> ret = null;",
							"BUTree<SNodeList> typeArgs = null;",
							"BUTree<SNodeList> params;",
							"BUTree<? extends SType> type;"
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
																	nonTerminal("ret", "MethodInvocation", null, exprs("ret")),
																	nonTerminal("ret", "FieldAccess", null, exprs("ret"))
															)
													),
													sequence(
															action("lateRun();"),
															nonTerminal("ret", "MethodReferenceSuffix", null, exprs("ret"))
													)
											)
									),
									nonTerminal("ret", "ClassCreationExpr", null, exprs("null")),
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
											nonTerminal("ret", "MethodReferenceSuffix", null, exprs("ret"))
									),
									sequence(
											action("run();"),
											nonTerminal("ret", "MethodInvocation", null, exprs("null"))
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
			production("PrimarySuffix", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							choice(
									sequence(
											nonTerminal("ret", "PrimarySuffixWithoutSuper", null, exprs("scope"))
									),
									sequence(
											terminal("DOT"),
											terminal("SUPER"),
											action("ret = dress(SSuperExpr.make(optionOf(scope)));")
									),
									nonTerminal("ret", "MethodReferenceSuffix", null, exprs("scope"))
							),
							action("return ret;")
					)
			),
			production("PrimarySuffixWithoutSuper", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<SName> name;"
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
													nonTerminal("ret", "ClassCreationExpr", null, exprs("scope")),
													nonTerminal("ret", "MethodInvocation", null, exprs("scope")),
													nonTerminal("ret", "FieldAccess", null, exprs("scope"))
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
			production("FieldAccess", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts("BUTree<SName> name;"),
					sequence(
							nonTerminal("name", "Name"),
							action("return dress(SFieldAccessExpr.make(optionOf(scope), name));")
					)
			),
			production("MethodInvocation", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts(
							"BUTree<SNodeList> typeArgs = null;",
							"BUTree<SName> name;",
							"BUTree<SNodeList> args = null;",
							"BUTree<? extends SExpr> ret;"
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
			production("Arguments", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SExpr> expr;"
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
			production("MethodReferenceSuffix", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts(
							"BUTree<SNodeList> typeArgs = null;",
							"BUTree<SName> name;",
							"BUTree<? extends SExpr> ret;"
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
			production("ClassCreationExpr", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> typeArgs = null;",
							"BUTree<SNodeList> anonymousBody = null;",
							"BUTree<SNodeList> args;",
							"BUTree<SNodeList> annotations = null;"
					),
					sequence(
							action("if (scope == null) run();"),
							terminal("NEW"),
							zeroOrOne(
									nonTerminal("typeArgs", "TypeArguments")
							),
							action("run();"),
							nonTerminal("annotations", "Annotations"),
							nonTerminal("type", "QualifiedType", null, exprs("annotations")),
							nonTerminal("args", "Arguments"),
							zeroOrOne(
									nonTerminal("anonymousBody", "ClassOrInterfaceBody", null, exprs("TypeKind.Class"))
							),
							action("return dress(SObjectCreationExpr.make(optionOf(scope), ensureNotNull(typeArgs), (BUTree<SQualifiedType>) type, args, optionOf(anonymousBody)));")
					)
			),
			production("ArrayCreationExpr", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SExpr> scope"),
					stmts(
							"BUTree<? extends SExpr> ret;",
							"BUTree<? extends SType> type;",
							"BUTree<SNodeList> typeArgs = null;",
							"BUTree<SNodeList> anonymousBody = null;",
							"BUTree<SNodeList> args;",
							"BUTree<SNodeList> annotations = null;"
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
									nonTerminal("type", "PrimitiveType", null, exprs("annotations")),
									nonTerminal("type", "QualifiedType", null, exprs("annotations"))
							),
							nonTerminal("ret", "ArrayCreationExprRest", null, exprs("type")),
							action("return ret;")
					)
			),
			production("ArrayCreationExprRest", "BUTree<? extends SExpr>",
					emptyList(),
					params("BUTree<? extends SType> componentType"),
					stmts(
							"BUTree<? extends SExpr> expr;",
							"BUTree<SNodeList> arrayDimExprs = emptyList();",
							"BUTree<SNodeList> arrayDims = emptyList();",
							"BUTree<SNodeList> annotations = null;",
							"BUTree<SArrayInitializerExpr> initializer;"
					),
					sequence(
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
					)
			),
			production("ArrayDimExprsMandatory", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> arrayDimExprs = emptyList();",
							"BUTree<SNodeList> annotations;",
							"BUTree<? extends SExpr> expr;"
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
			production("ArrayDimsMandatory", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> arrayDims = emptyList();",
							"BUTree<SNodeList> annotations;"
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
			production("Statement", "BUTree<? extends SStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SStmt> ret;"),
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
			production("AssertStatement", "BUTree<SAssertStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> check;",
							"BUTree<? extends SExpr> msg = null;"
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
			production("LabeledStatement", "BUTree<SLabeledStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SName> label;",
							"BUTree<? extends SStmt> stmt;"
					),
					sequence(
							action("run();"),
							nonTerminal("label", "Name"),
							terminal("COLON"),
							nonTerminal("stmt", "Statement"),
							action("return dress(SLabeledStmt.make(label, stmt));")
					)
			),
			production("Block", "BUTree<SBlockStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SNodeList> stmts;"),
					sequence(
							action("run();"),
							terminal("LBRACE"),
							nonTerminal("stmts", "Statements"),
							terminal("RBRACE"),
							action("return dress(SBlockStmt.make(ensureNotNull(stmts)));")
					)
			),
			production("BlockStatement", "BUTree<? extends SStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SStmt> ret;",
							"BUTree<? extends SExpr> expr;",
							"BUTree<? extends STypeDecl> typeDecl;",
							"BUTree<SNodeList> modifiers;"
					),
					sequence(
							choice(
									sequence(
											action("run();"),
											action("run();"),
											nonTerminal("modifiers", "ModifiersNoDefault"),
											nonTerminal("typeDecl", "ClassOrInterfaceDecl", null, exprs("modifiers")),
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
			production("VariableDeclExpression", "BUTree<SVariableDeclarationExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<SLocalVariableDecl> variableDecl;"
					),
					sequence(
							action("run();"),
							action("run();"),
							nonTerminal("modifiers", "ModifiersNoDefault"),
							nonTerminal("variableDecl", "VariableDecl", null, exprs("modifiers")),
							action("return dress(SVariableDeclarationExpr.make(variableDecl));")
					)
			),
			production("EmptyStatement", "BUTree<SEmptyStmt>",
					emptyList(),
					emptyList(),
					emptyList(),
					sequence(
							action("run();"),
							terminal("SEMICOLON"),
							action("return dress(SEmptyStmt.make());")
					)
			),
			production("ExpressionStatement", "BUTree<SExpressionStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> expr;",
							"AssignOp op;",
							"BUTree<? extends SExpr> value;"
					),
					sequence(
							action("run();"),
							nonTerminal("expr", "StatementExpression"),
							terminal("SEMICOLON"),
							action("return dress(SExpressionStmt.make(expr));")
					)
			),
			production("StatementExpression", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							// TODO Add further checks to report invalid expression in a statement
							nonTerminal("ret", "Expression"),
							action("return ret;")
					)
			),
			production("SwitchStatement", "BUTree<SSwitchStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> selector;",
							"BUTree<SSwitchCase> entry;",
							"BUTree<SNodeList> entries = emptyList();"
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
			production("SwitchEntry", "BUTree<SSwitchCase>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> label = null;",
							"BUTree<SNodeList> stmts;"
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
			production("IfStatement", "BUTree<SIfStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> condition;",
							"BUTree<? extends SStmt> thenStmt;",
							"BUTree<? extends SStmt> elseStmt = null;"
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
			production("WhileStatement", "BUTree<SWhileStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> condition;",
							"BUTree<? extends SStmt> body;"
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
			production("DoStatement", "BUTree<SDoStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> condition;",
							"BUTree<? extends SStmt> body;"
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
			production("ForStatement", "BUTree<? extends SStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SVariableDeclarationExpr> varExpr = null;",
							"BUTree<? extends SExpr> expr = null;",
							"BUTree<SNodeList> init = null;",
							"BUTree<SNodeList> update = null;",
							"BUTree<? extends SStmt> body;"
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
			production("ForInit", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret;",
							"BUTree<? extends SExpr> expr;"
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
			production("StatementExpressionList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SExpr> expr;"
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
			production("ForUpdate", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SNodeList> ret;"),
					sequence(
							nonTerminal("ret", "StatementExpressionList"),
							action("return ret;")
					)
			),
			production("BreakStatement", "BUTree<SBreakStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SName> id = null;"),
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
			production("ContinueStatement", "BUTree<SContinueStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SName> id = null;"),
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
			production("ReturnStatement", "BUTree<SReturnStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> expr = null;"),
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
			production("ThrowStatement", "BUTree<SThrowStmt>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> expr;"),
					sequence(
							action("run();"),
							terminal("THROW"),
							nonTerminal("expr", "Expression"),
							terminal("SEMICOLON"),
							action("return dress(SThrowStmt.make(expr));")
					)
			),
			production("SynchronizedStatement", "BUTree<SSynchronizedStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<? extends SExpr> expr;",
							"BUTree<SBlockStmt> block;"
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
			production("TryStatement", "BUTree<STryStmt>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> resources = null;",
							"ByRef<Boolean> trailingSemiColon = new ByRef<Boolean>(false);",
							"BUTree<SBlockStmt> tryBlock;",
							"BUTree<SBlockStmt> finallyBlock = null;",
							"BUTree<SNodeList> catchClauses = null;"
					),
					sequence(
							action("run();"),
							terminal("TRY"),
							choice(
									sequence(
											nonTerminal("resources", "ResourceSpecification", null, exprs("trailingSemiColon")),
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
			production("CatchClauses", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> catchClauses = emptyList();",
							"BUTree<SCatchClause> catchClause;"
					),
					sequence(
							oneOrMore(
									nonTerminal("catchClause", "CatchClause"),
									action("catchClauses = append(catchClauses, catchClause);")
							),
							action("return catchClauses;")
					)
			),
			production("CatchClause", "BUTree<SCatchClause>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SFormalParameter> param;",
							"BUTree<SBlockStmt> catchBlock;"
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
			production("CatchFormalParameter", "BUTree<SFormalParameter>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> modifiers;",
							"BUTree<? extends SType> exceptType;",
							"BUTree<SNodeList> exceptTypes = emptyList();",
							"BUTree<SVariableDeclaratorId> exceptId;"
					),
					sequence(
							action("run();"),
							nonTerminal("modifiers", "Modifiers"),
							nonTerminal("exceptType", "QualifiedType", null, exprs("null")),
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
			production("ResourceSpecification", "BUTree<SNodeList>",
					emptyList(),
					params("ByRef<Boolean> trailingSemiColon"),
					stmts(
							"BUTree<SNodeList> vars = emptyList();",
							"BUTree<SVariableDeclarationExpr> var;"
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

			// ----- Annotations -----

			production("Annotations", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> annotations = emptyList();",
							"BUTree<? extends SAnnotationExpr> annotation;"
					),
					sequence(
							zeroOrMore(
									nonTerminal("annotation", "Annotation"),
									action("annotations = append(annotations, annotation);")
							),
							action("return annotations;")
					)
			),
			production("Annotation", "BUTree<? extends SAnnotationExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SAnnotationExpr> ret;"),
					sequence(
							choice(
									nonTerminal("ret", "NormalAnnotation"),
									nonTerminal("ret", "MarkerAnnotation"),
									nonTerminal("ret", "SingleElementAnnotation")
							),
							action("return ret;")
					)
			),
			production("NormalAnnotation", "BUTree<SNormalAnnotationExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SQualifiedName> name;",
							"BUTree<SNodeList> pairs = null;"
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
			production("MarkerAnnotation", "BUTree<SMarkerAnnotationExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<SQualifiedName> name;"),
					sequence(
							action("run();"),
							terminal("AT"),
							nonTerminal("name", "QualifiedName"),
							action("return dress(SMarkerAnnotationExpr.make(name));")
					)
			),
			production("SingleElementAnnotation", "BUTree<SSingleMemberAnnotationExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SQualifiedName> name;",
							"BUTree<? extends SExpr> value;"
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
			production("ElementValuePairList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<SMemberValuePair> pair;"
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
			production("ElementValuePair", "BUTree<SMemberValuePair>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SName> name;",
							"BUTree<? extends SExpr> value;"
					),
					sequence(
							action("run();"),
							nonTerminal("name", "Name"),
							terminal("ASSIGN"),
							nonTerminal("value", "ElementValue"),
							action("return dress(SMemberValuePair.make(name, value));")
					)
			),
			production("ElementValue", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts("BUTree<? extends SExpr> ret;"),
					sequence(
							choice(
									nonTerminal("ret", "ConditionalExpression"),
									nonTerminal("ret", "ElementValueArrayInitializer"),
									nonTerminal("ret", "Annotation")
							),
							action("return ret;")
					)
			),
			production("ElementValueArrayInitializer", "BUTree<? extends SExpr>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> values = null;",
							"boolean trailingComma = false;"
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
			production("ElementValueList", "BUTree<SNodeList>",
					emptyList(),
					emptyList(),
					stmts(
							"BUTree<SNodeList> ret = emptyList();",
							"BUTree<? extends SExpr> value;"
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

	private static NodeList<Stmt> stmts(String... strings) {
		NodeList<Stmt> result = emptyList();
		for (String string : strings) {
			result = result.append(Quotes.stmt(string).build());
		}
		return result;
	}

	private static NodeList<FormalParameter> params(String... strings) {
		NodeList<FormalParameter> result = emptyList();
		for (String string : strings) {
			result = result.append(Quotes.param(string).build());
		}
		return result;
	}

	private static NodeList<Expr> exprs(String... strings) {
		NodeList<Expr> result = emptyList();
		for (String string : strings) {
			result = result.append(Quotes.expr(string).build());
		}
		return result;
	}
}
