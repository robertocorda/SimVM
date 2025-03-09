package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlLexer;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class WeightedRulesPlParserTest {

    @Test
    public void testParseSingleRule() {
        // Test per un programma DSL con una singola regola
        String input = """
                if applies {
                    see distance < 5 and color is RED and speed > 2
                } do {
                    ROTATE
                } with importance {
                    100
                }
                ;
                
                
                if applies {
                    see distance < 5 and color is RED and speed > 2
                } do {
                    ROTATE
                } with importance {
                    100
                }
                ;
                """;

        // Crea lo stream di input
        CharStream charStream = CharStreams.fromString(input);

        CollectingErrorListener errorTokensListener = new CollectingErrorListener("tokens");
        CollectingErrorListener errorParserListener = new CollectingErrorListener("parser");
        // Crea il lexer
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(charStream);
        // Aggiungi il mio listener degli errori
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorTokensListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Crea il parser
        WeightedRulesPlParser parser = new WeightedRulesPlParser(tokens);
        // Aggiungi il mio listener degli errori
        parser.removeErrorListeners();
        parser.addErrorListener(errorParserListener);
        // Avvia il parsing a partire dalla produzione 'program'
        ParseTree tree = parser.program();

        // Verifica che l'albero non sia null
        assertNotNull(tree, "L'albero di parsing non dovrebbe essere null.");
        //System.out.println(tree.toStringTree());

        // Verifica che il programma contenga esattamente una decision rule
        WeightedRulesPlParser.ProgramContext programContext = parser.program();


        // Dopo il parsing, recupera gli errori
        List<String> errors = errorParserListener.getErrorMessages();
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

        List<WeightedRulesPlParser.DecisionRuleContext> decisionRules = new ArrayList<>();
        for (int i = 0; i < programContext.getChildCount(); i++) {
            ParseTree child = programContext.getChild(i);
            if (child instanceof WeightedRulesPlParser.DecisionRuleContext) {
                decisionRules.add((WeightedRulesPlParser.DecisionRuleContext) child);
            }
        }
        System.out.println("Trovate decision rule: " + decisionRules.size());

        int count = countDecisionRules(tree);
        System.out.println("Trovate decision rule nel tree: " + count);


        List<WeightedRulesPlParser.DecisionRuleContext> rules =
                DecisionRuleCollector.findDecisionRules(tree);
        System.out.println("Numero di decision rules trovate: " + rules.size());
        for (WeightedRulesPlParser.DecisionRuleContext r : rules) {
            // Puoi stampare informazioni, ad esempio:
            System.out.println(r.getText());
        }

        String result = tree.toStringTree(parser);
        System.out.println("tree: " + result);
        System.out.println(programContext.decisionRule().toString());
        //WeightedRulesPlParser.DecisionRuleContext pippo = programContext.getRule;
        //System.out.println("pippo:" + pippo.toString());
        assertEquals(1, programContext.decisionRule().size(),
                "Ci si aspetta esattamente una decisionRule nel programma.");


    }

    /**
     * Conta quanti nodi di tipo DecisionRuleContext sono presenti
     * in un albero di parsing generico (ParseTree).
     */
    public static int countDecisionRules(ParseTree tree) {
        // Se questo nodo è una DecisionRuleContext, incrementa di 1
        if (tree instanceof WeightedRulesPlParser.DecisionRuleContext) {
            return 1;
        }

        // Altrimenti, scorre i figli ricorsivamente
        int count = 0;
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            count += countDecisionRules(child);
        }
        return count;
    }

    @Test
    public void testParseMultipleRules() {
        // Test per un programma DSL con due regole separate da una riga vuota
        String input = """
                if applies { see distance < 5 and color is RED and speed > 2 }
                do { ROTATE } with importance { 100 };

                if applies { see distance > 10 and color is BLUE and speed < 3 }
                do { PUSH } with importance { 50 };""";
        CharStream charStream = CharStreams.fromString(input);
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WeightedRulesPlParser parser = new WeightedRulesPlParser(tokens);
        WeightedRulesPlParser.ProgramContext programContext = parser.program();

        // Verifica che il programma contenga esattamente due decision rule
        assertEquals(2, programContext.decisionRule().size(),
                "Ci si aspetta esattamente due decisionRules nel programma.");
    }
}

