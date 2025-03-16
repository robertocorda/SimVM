grammar MicroFunctLang;

@header {
    package org.dracosoft.microfunctlang.parser;
}

// PRODUZIONE TOP-LEVEL: uno o più functionDefinition
program
    : functionDefinition EOF
    ;

functionDefinition
    : typeDecl functionName LPAR paramList+  ARROW expr RPAR
    ;

RPAR : ')' ;
ARROW : '->' ;
LPAR : '(' ;

// Tipo: int o bool
typeDecl
    : 'int'
    | 'bool'
    ;

// Nome funzione
functionName
    : ID
    ;

// Parametri: (typeDecl ID) separati da virgola
paramList
    : param (',' param)*
    ;

param
    : typeDecl ID
    ;

// Espressione generica, può essere aritmetica o booleana
expr
    : expr opMulDiv expr                    # MulDivExpr
    | expr opAddSub expr                    # AddSubExpr
    | expr opLogic expr                     # LogicExpr
    | 'not' expr                            # NotExpr
    | '(' expr ')'                          # ParenExpr
    | INT                                   # IntLiteral
    | 'true'                                # TrueLiteral
    | 'false'                               # FalseLiteral
    | ID                                    # VarRef
    ;

// Operatori

opMulDiv : '*' | '/' ;
opAddSub : '+' | '-' ;
opLogic  : 'and' | 'or' ;

// LESSICO
ID      : [a-z][a-zA-Z0-9_]* ;
INT     : [0-9]+ ;
WS      : [ \t\r\n]+ -> skip ;
