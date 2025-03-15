// version 0.1
// 2025-03

grammar ConditionGrammar;
@header {
    package org.dracosoft.condition.parser;
}
// TODO add this
// expr (MUL|DIV) expr   |
expr:   expr (addSub) expr
    |   INT
    ;

addSub : op=ADD|SUB ;

INT:    [0-9]+;
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';

WS:     [ \t\r\n]+ -> skip;
