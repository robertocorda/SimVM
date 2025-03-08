package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;
import org.junit.jupiter.api.Test;

import org.dracosoft.weightedrulespl.parser.WeightedRulesPlLexer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class WeightedRulesPlParserTest {

    @Test
    public void testParseSingleRule() {
        // Test per un programma DSL con una singola regola
        String input2 = """
                if applies { 
                    if see distance < 5 and color is RED and speed > 2 
                } do { 
                    ROTATE 
                } with importance { 
                    100 
                } 
                end
                """;
        String input =
                "if applies { if see distance < 5 and color is RED and speed > 2 }"+
                " do { ROTATE } with importance { 100 } end";
        // Crea lo stream di input
        CharStream charStream = CharStreams.fromString(input);

        CollectingErrorListener errorListener = new CollectingErrorListener();
        // Crea il lexer
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(charStream);
        // Aggiungi il mio listener degli errori
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Crea il parser
        WeightedRulesPlParser parser = new WeightedRulesPlParser(tokens);
        // Aggiungi il mio listener degli errori
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        // Avvia il parsing a partire dalla produzione 'program'
        ParseTree tree = parser.program();

        // Verifica che l'albero non sia null
        assertNotNull(tree, "L'albero di parsing non dovrebbe essere null.");
        System.out.println(tree.toStringTree());

        // Verifica che il programma contenga esattamente una decision rule
        WeightedRulesPlParser.ProgramContext programContext = parser.program();


        // Dopo il parsing, recupera gli errori
        List<String> errors = errorListener.getErrorMessages();
        if (!errors.isEmpty()) {
            System.out.println("Errori di parsing:");
            for (String error : errors) {
                System.out.println(error);
            }
        } else {
            System.out.println("Parsing completato senza errori.");
        }

        tokens.fill();
        for (Token t : tokens.getTokens()) {
            System.out.println("Token: " + t.getText() + " - " + WeightedRulesPlLexer.VOCABULARY.getSymbolicName(t.getType()));
        }

        //assertEquals(1, programContext.decisionRule(),
       //         "Ci si aspetta esattamente una decisionRule nel programma.");




    }

    //@Test
    public void testParseMultipleRules() {
        // Test per un programma DSL con due regole separate da una riga vuota
        String input = """
                if applies { if see object with distance < 5 and color is RED and speed > 2 } \
                do { ROTATE } with importance { 100 }\

                if applies { if see object with distance > 10 and color is BLUE and speed < 3 } \
                do { PUSH } with importance { 50 }""";
        CharStream charStream = CharStreams.fromString(input);
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WeightedRulesPlParser parser = new WeightedRulesPlParser(tokens);
        WeightedRulesPlParser.ProgramContext programContext = parser.program();

        // Verifica che il programma contenga esattamente due decision rule
        //assertEquals(2, programContext.decisionRule().size(),
        //        "Ci si aspetta esattamente due decisionRules nel programma.");
    }
}

