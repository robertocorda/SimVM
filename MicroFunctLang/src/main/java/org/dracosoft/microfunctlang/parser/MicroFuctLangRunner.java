package org.dracosoft.microfunctlang.parser;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.microfunctlang.runtime.MicroFunctLangInterpreter;
import org.dracosoft.microfunctlang.parser.MicroFunctLangLexer;
import org.dracosoft.microfunctlang.parser.MicroFunctLangParser;
import org.dracosoft.parserutils.CollectingErrorListener;

public class MicroFuctLangRunner {
    public static void main(String[] args) throws Exception {

        CollectingErrorListener errorLexerListener = new CollectingErrorListener("lexer parserErrors");
        CollectingErrorListener errorParserListener = new CollectingErrorListener("parser parserErrors");

        // parser
        String code = ""
                + "int pippo( v -> v*3 + 1)";
        // "funct2 ((bool a, bool b, bool c) -> a and b and c);";
        MicroFunctLangLexer lexer = new MicroFunctLangLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorLexerListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println(tokens.getTokens());
        System.out.println(errorLexerListener.getErrorMessages());

        MicroFunctLangParser parser = new MicroFunctLangParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorParserListener);
        ParseTree tree = parser.expr();
        System.out.println(tree.toStringTree(parser));

        //runtime
        MicroFunctLangInterpreter eval = new MicroFunctLangInterpreter();
        System.out.println("Result: " + eval.visit(tree));
    }
}
