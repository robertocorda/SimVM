package org.dracosoft;

import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;
import org.dracosoft.simbioma.dsl.manualdsl.DslDecisionRule;
import org.dracosoft.simbioma.dsl.manualdsl.ManualDecisionRuleParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dracosoft.simbioma.model.SenseDataFactory.createSenseData;
import static org.junit.jupiter.api.Assertions.*;

public class ManualDslParserTest {

    /**
     * Testa il parsing e l'applicazione di una regola che prevede ROTATE.
     * La regola DSL è:
     * if applies { if see object with distance <5 and color RED and speed >2 };
     * do { ROTATE } with importance { 100 - (distance * 10) + (speed * 2) }
     * Con SenseData(distance=3, color="RED", speed=4) ci aspettiamo:
     * - La regola si applichi.
     * - Il comando sia ROTATE.
     * - Il peso calcolato sia: 100 - (3*10) + (4*2) = 78.
     */
    @Test
    public void testRotateRuleApplies() {
        String dslProgram =
                """
                        if applies {
                           if see object with
                                distance < 5 and
                                color is RED
                                and speed > 2
                        } do {
                            ROTATE
                        }
                        with importance {
                            100 - (distance * 10) + (speed * 2)
                        }
                        """;

        ManualDecisionRuleParser parser = new ManualDecisionRuleParser();
        List<DecisionRule> rules = parser.parseRules(dslProgram);
        assertNotNull(rules, "La lista di regole non deve essere null.");
        assertEquals(1, rules.size(), "Ci si aspetta una e una sola regola.");

        DecisionRule rule = rules.get(0);
        // Controlliamo che il nome della regola contenga parte della condizione (come indicatore)
        assertTrue(rule.getName().contains("if see object with"),
                "Il nome della regola dovrebbe contenere la descrizione della condizione.");

        // Creiamo un SenseData che soddisfa la condizione: distance=3, color=RED, speed=4.
        SenseData senseData = createSenseData(3, "RED", 4);
        assertTrue(rule.applies(senseData), "La regola deve applicarsi con i dati forniti.");

        // Controlliamo il comando: ci aspettiamo ROTATE.
        assertEquals(BiomaCommand.ROTATE, rule.getCommand(),
                "La regola deve restituire il comando ROTATE.");

        // Calcoliamo il peso utilizzando il metodo specifico (computeWeight) di DSLDecisionRule.
        assertInstanceOf(DslDecisionRule.class, rule, "La regola deve essere un'istanza di DSLDecisionRule.");
        DslDecisionRule dslRule = (DslDecisionRule) rule;
        int computedWeight = dslRule.getWeight(senseData);
        int expectedWeight = 100 - (3 * 10) + (4 * 2); // 100 - 30 + 8 = 78
        assertEquals(expectedWeight, computedWeight,
                "Il peso calcolato deve essere " + expectedWeight + ".");
    }

    /**
     * Testa il parsing di una regola che prevede PUSH e verifica che non si applichi
     * con dati sensoriali che non soddisfano la condizione.
     * La regola DSL è:
     * if applies { if see object with distance <10 and color BLUE and speed <3 };
     * do { PUSH } with importance { 50 }
     * Con SenseData(distance=8, color="RED", speed=2) la condizione non è soddisfatta,
     * quindi la regola non deve applicarsi.
     */
    @Test
    public void testPushRuleDoesNotApply() {
        String cond = "if see object with distance < 10 and color BLUE and speed < 3";
        String command = "PUSH";
        String importance = "50";
        String dslProgram = "if applies " +
                "{ " + cond + " }; " +
                "do { " + command + " } " +
                "with importance { " + importance + " }";

        System.out.println("program: " + dslProgram);

        ManualDecisionRuleParser parser = new ManualDecisionRuleParser();
        List<DecisionRule> rules = parser.parseRules(dslProgram);
        assertNotNull(rules, "La lista di regole non deve essere null.");
        assertEquals(1, rules.size(), "Ci si aspetta una sola regola.");

        DecisionRule rule = rules.get(0);
        // Creiamo un SenseData che NON soddisfa la condizione (color è RED invece di BLUE)
        SenseData senseData = createSenseData(8, "RED", 2);
        assertFalse(rule.applies(senseData),
                "La regola non deve applicarsi con i dati forniti (color non è BLUE).");
    }
}

