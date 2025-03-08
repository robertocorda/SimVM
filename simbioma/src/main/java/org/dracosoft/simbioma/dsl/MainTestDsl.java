package org.dracosoft.simbioma.dsl;

import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

import java.util.List;

public class MainTestDsl {
    public static void main(String[] args) {
        // Programma DSL: una regola che controlla se "vedo un oggetto"
        // a distanza < 5, colore RED e speed > 2, in tal caso esegue ROTATE
        // e calcola l'importanza con la formula: 100 - (distance * 10) + (speed * 2)
        String dslProgram = "if applies { if see object with distance <5 and color RED and speed >2 }; " +
                "do { ROTATE } with importance { 100 - (distance * 10) + (speed * 2) }";

        // Parsing del DSL: restituisce una lista di regole
        List<DecisionRule> rules = DecisionRuleParser.parseRules(dslProgram);
        System.out.println("Parsed rules:");
        for (DecisionRule rule : rules) {
            System.out.println(" - " + rule.getName() + " -> Command: " + rule.getCommand());
        }

        // Creiamo un esempio di dati sensoriali:
        // Ad esempio: distanza 3, colore RED, velocità 4
        SenseData sampleSense = new SenseData(3, "RED", 4);
        System.out.println("\nTesting with SenseData: " + sampleSense);

        // Valutiamo ciascuna regola sui dati di test
        for (DecisionRule rule : rules) {
            boolean applies = rule.applies(sampleSense);
            System.out.println("\nRule: " + rule.getName());
            System.out.println(" - Applies? " + applies);
            if (applies && rule instanceof DslDecisionRule) {
                DslDecisionRule dslRule = (DslDecisionRule) rule;
                int computedWeight = dslRule.getWeight(sampleSense);
                System.out.println(" - Computed weight: " + computedWeight);
                System.out.println(" - Action: " + rule.getCommand());
            }
        }
    }
}

