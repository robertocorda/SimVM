package org.dracosoft.condition.parser;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.condition.parser.ConditionGrammarLexer;
import org.dracosoft.condition.parser.ConditionGrammarParser;
import org.dracosoft.condition.runtime.Evaluator;

public class ConditionParser {
    public static void main(String[] args) throws Exception {

        // parser
        String expression = "3 + 5";
        org.dracosoft.condition.parser.ConditionGrammarLexer lexer = new ConditionGrammarLexer(CharStreams.fromString(expression));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConditionGrammarParser parser = new ConditionGrammarParser(tokens);
        ParseTree tree = parser.expr();
        System.out.println(tree.toStringTree(parser));

        //runtime
        Evaluator eval = new Evaluator();
        System.out.println("Result: " + eval.visit(tree));
    }
}
