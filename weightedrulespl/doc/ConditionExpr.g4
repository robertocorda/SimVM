grammar WeightedRulesPl;

// Produzione principale per l'intera condizione (qui omettiamo la regola 'program',
// concentrandoci solo sul blocco "condizioneExpr").
conditionExpr
    : inputType? condition (AND condition)* EOF
    ;

// Una 'condition' singola può avere un optional NOT davanti,
// e uno "inputType" facoltativo in alcuni DSL (se serve).
// Oppure puoi prevederlo nella regola superior (dipende dal DSL).
condition
    : (NOT)? singleCondition
    ;

// La "singleCondition" gestisce la forma "expr conditionOp expr",
// con la possibilità di avere un "not" dopo l'operatore (es: "is not"? Se vuoi gestire "is not" come un token unico, puoi farlo).
singleCondition
    : expr conditionOp (NOT)? expr
    ;

// L'espressione su cui si calcolano i confronti (es. "distance + 2", "${pippo} * speed")
expr
    : expr opMulDiv expr                 // es: expr * expr
    | expr opAddSub expr                 // es: expr + expr
    | literal                            // un singolo literal (field, costante, stored, etc.)
    | LPAR expr RPAR                     // parentesi
    ;

// operatori aritmetici
opMulDiv : '*' | '/' ;
opAddSub : '+' | '-' ;

// L'inputType: "sense" o "mem"
inputType
    : SENSE
    | MEM
    ;

// conditionOp gestisce <, >, <=, >=, ==, is (equivalente a ==)
// (se vuoi "is not" come token unico, puoi gestirlo qui;
//   altrimenti usi la combinazione "is" e "not" nella regola 'singleCondition')
conditionOp
    : LT
    | GT
    | LE
    | GE
    | EQ
    | IS
    ;

// literal può essere un field, un costante, un valore stored (es. ${pippo})
// oppure un "lowerLiteral" (una variabile minuscola).
literal
    : FIELD
    | CONSTANT
    | STORED
    | LOWER_LITERAL
    ;

// Field -> distance, speed, color (se li vuoi come campi "built-in" col DSL)
FIELD
    : DISTANCE
    | SPEED
    | COLOR
    ;

// Riconoscimento di un costante come "ABC123" (solo maiuscole e cifre)
CONSTANT : [A-Z0-9]+
    ;

// Riconoscimento di un valore esterno minuscolo (es. "pippo", "speedy123" se inizia con minuscola).
LOWER_LITERAL
    : [a-z] [a-z0-9]*
    ;

// Valore stored con la sintassi ${qualcosa}
STORED
    : '${' ID '}'
    ;

// Tokens
NOT             : 'not' ;
AND             : 'and' ;
SENSE           : 'sense' ;
MEM             : 'mem' ;
DISTANCE        : 'distance' ;
SPEED           : 'speed' ;
COLOR           : 'color' ;

// operatori di confronto
LT              : '<' ;
GT              : '>' ;
LE              : '<=' ;
GE              : '>=' ;
EQ              : '==' ;
IS              : 'is' ;

// ID generico (usato in 'stored')
ID              : [a-zA-Z0-9_]+ ;

// Simboli
LPAR            : '(' ;
RPAR            : ')' ;

// Spazi e newline ignorati
WS              : [ \t\r\n]+ -> skip ;
