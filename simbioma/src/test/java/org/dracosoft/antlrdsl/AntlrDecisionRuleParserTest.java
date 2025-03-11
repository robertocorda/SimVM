package org.dracosoft.antlrdsl;

import org.dracosoft.simbioma.dsl.antlrdsl.AntlrDecisionRuleParser;
import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.ToDecisionRule;
import org.junit.jupiter.api.Test;

import static org.dracosoft.simbioma.model.SenseDataFactory.createSenseData;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AntlrDecisionRuleParserTest {

    @Test
    public void testSingleRule() {
        // DSL di esempio con una sola regola
        // Nota: se la grammatica richiede un ';' alla fine della regola, aggiungilo
        String dslProgram =
                "if applies { see distance < 5 and color is RED } " +
                        "do { ROTATE } with importance { 100 };";

        // Istanzia il parser
        ToDecisionRule parser = new AntlrDecisionRuleParser();
        // Esegui il parsing
        List<DecisionRule> rules = parser.parseRules(dslProgram);

        // Verifica che la lista di regole non sia vuota
        assertFalse(rules.isEmpty(), "Ci si aspetta almeno una regola parseata.");
        // Verifichiamo che ci sia esattamente una regola
        assertEquals(1, rules.size(), "Ci si aspetta una sola DecisionRule.");

        // Verifichiamo qualche aspetto della regola (ad es. un comando riconosciuto)
        DecisionRule rule = rules.get(0);
        assertNotNull(rule.getCommand(), "Il comando non dovrebbe essere null.");
        // Se ti aspetti "ROTATE"
        assertEquals(BiomaCommand.ROTATE, rule.getCommand(),
                "La regola deve avere il comando ROTATE.");
        // Se la getName() non deve essere vuota
        assertNotNull(rule.getName(), "Il nome della regola non dovrebbe essere null.");

        // see distance < 5 and color is RED
        testRuleApplies(rule, 3, "RED", 12, true);
        testRuleApplies(rule, 5, "RED", 12, false);
        testRuleApplies(rule, 6, "RED", 12, false);

        testRuleApplies(rule, 3, "BLUE", 12, false);
        testRuleApplies(rule, 3, "BLUE", 12, false);
        testRuleApplies(rule, 3, "BLUE", 12, false);


    }

    private static void testRuleApplies(DecisionRule rule, int distance, String color, int speed, boolean expectedApply) {
        boolean actualApply = rule.applies(createSenseData(distance, color, speed));
        assertEquals(expectedApply,actualApply );
    }

    @Test
    public void testMultipleRules() {
        // DSL con due regole
        String dslProgram =
                "if applies { see distance < 5 } do { PUSH } with importance { 50 };" +
                        "if applies { see speed > 2 } do { STILL } with importance { 100 };";

        // Istanzia il parser
        ToDecisionRule parser = new AntlrDecisionRuleParser();
        // Esegui il parsing
        List<DecisionRule> rules = parser.parseRules(dslProgram);

        // Ora ci aspettiamo due regole parseate
        assertEquals(2, rules.size(), "Ci si aspetta due DecisionRule.");

        // Controllo di base per la prima regola
        DecisionRule first = rules.get(0);
        assertEquals(BiomaCommand.PUSH, first.getCommand(),
                "La prima regola deve avere il comando PUSH.");

        // Controllo di base per la seconda regola
        DecisionRule second = rules.get(1);
        assertEquals(BiomaCommand.STILL, second.getCommand(),
                "La seconda regola deve avere il comando STILL.");

        // Se vuoi verificare i parametri di weight
        // ad esempio che la regola abbia un valore "50" e "100"
        // potresti creare un SenseData e testare rule.getWeight(...)
        // ma qui facciamo test più basilari
    }
}

