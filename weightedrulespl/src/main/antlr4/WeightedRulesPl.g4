grammar WeightedRulesPl;
@header {
    package org.dracosoft.weightedrulespl.parser;
}

program         : (decisionRule SEMI)* EOF ;

decisionRule    : ifClause doClause withImportanceClause ;

ifClause        : IF_APPLIES LBRACE conditionExpr RBRACE ;
doClause        : DO LBRACE commandExpr RBRACE ;
withImportanceClause : WITH_IMPORTANCE LBRACE importanceExpr RBRACE ;

conditionExpr   : inputType condition (AND condition)* ;
inputType       : SEE | MEM ;
condition       : field conditionOp value ;
field           : DISTANCE 
                | COLOR 
                | SPEED ;
conditionOp     : LT | GT | LE | GE | EQ | IS ;
value           : NUMBER
                | COLOR_LITERAL ;

commandExpr     : COMMAND ;

importanceExpr  : expr ;

expr            : expr op=('*'|'/') expr      
                | expr op=('+'|'-') expr      
                | NUMBER
                | field                       
                | LPAR expr RPAR              
                ;

IF_APPLIES      : 'if applies' ;
DO              : 'do' ;
WITH_IMPORTANCE : 'with importance' ;
AND             : 'and' ;
SEMI            : ';' ;
LBRACE          : '{' ;
RBRACE          : '}' ;
LPAR            : '(' ;
RPAR            : ')' ;

IF              : 'if' ;

DISTANCE        : 'distance' ;
COLOR           : 'color' ;
SPEED           : 'speed' ;

SEE             : 'see' ;
MEM             : 'memory' ;

LT              : '<' ;
GT              : '>' ;
LE              : '<=' ;
GE              : '>=' ;
EQ              : '==' ;
IS              : 'is' ;

COMMAND         : 'ROTATE' | 'PUSH' | 'STILL' ;

NUMBER          : [0-9]+ ;
COLOR_LITERAL   : [A-Z]+ ;

WS              : [ \t\r\n]+ -> skip ;
