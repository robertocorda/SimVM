package org.dracosoft.simbioma.dsl;

/**
 * Parser DSL per decision rules.
 * Il DSL accetta regole nella forma:
 *
 * if applies { if see object with distance <5 and color RED and speed >2 };
 * do { ROTATE } with importance { 100 - (distance * 10) + (speed * 2) }
 *
 * L'implementazione qui mostrata è semplificata e assume che la sintassi sia esattamente quella attesa.
 */
import org.dracosoft.simbioma.BiomaCommand;
import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class DecisionRuleParser {

    /**
     * Data una stringa contenente una o più regole DSL, restituisce una lista di DSLDecisionRule.
     */
    public static List<DecisionRule> parseRules(String dslProgram) {
        List<DecisionRule> rules = new ArrayList<>();
        // Supponiamo che ogni regola sia separata da una nuova riga vuota o un delimitatore specifico.
        // Per semplicità, dividiamo per newline; ogni riga non vuota è una regola.
        String[] lines = dslProgram.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            // Per esempio, la regola attesa:
            // if applies { if see object with distance <5 and color RED and speed >2 }; do { ROTATE } with importance { 100 - (distance * 10) + (speed * 2) }
            DecisionRule rule = parseSingleRule(line);
            if (rule != null) {
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Parser molto semplificato di una singola regola DSL.
     * NOTA: Questa implementazione è approssimativa e deve essere migliorata per gestire casi generali.
     */
    private static DecisionRule parseSingleRule(String ruleText) {
        // Divide la regola nelle tre parti principali:
        // 1. La condizione, racchiusa tra "if applies {" e "}"
        // 2. I comandi, racchiusi tra "do {" e "}"
        // 3. L'importanza, racchiusa tra "with importance {" e "}"
        try {
            int ifStart = ruleText.indexOf("if applies {");
            int ifEnd = ruleText.indexOf("}", ifStart);
            String conditionText = ruleText.substring(ifStart + "if applies {".length(), ifEnd).trim();

            int doStart = ruleText.indexOf("do {", ifEnd);
            int doEnd = ruleText.indexOf("}", doStart);
            String commandText = ruleText.substring(doStart + "do {".length(), doEnd).trim();

            int impStart = ruleText.indexOf("with importance {", doEnd);
            int impEnd = ruleText.indexOf("}", impStart);
            String importanceText = ruleText.substring(impStart + "with importance {".length(), impEnd).trim();

            // Per il nome della regola, possiamo usare la condizione (o una sua parte)
            String ruleName = "Rule(" + conditionText + ")";

            // Converti la condizione in un Predicate<SenseData>
            // Esempio: "if see object with distance <5 and color RED and speed >2"
            // In una implementazione reale useremmo un parser espressioni. Qui simuliamo un parser "fisso".
            Predicate<SenseData> condition = data -> {
                boolean cond1 = data.getDistance() < 5;
                boolean cond2 = "RED".equalsIgnoreCase(data.getColor());
                boolean cond3 = data.getSpeed() > 2;
                return cond1 && cond2 && cond3;
            };

            // Converti il comando: assumiamo che commandText contenga un solo comando (ROTATE, PUSH o STILL)
            BiomaCommand command;
            switch (commandText.toUpperCase()) {
                case "ROTATE":
                    command = BiomaCommand.ROTATE;
                    break;
                case "PUSH":
                    command = BiomaCommand.PUSH;
                    break;
                case "STILL":
                    command = BiomaCommand.STILL;
                    break;
                default:
                    throw new IllegalArgumentException("Comando non riconosciuto: " + commandText);
            }

            // Converti l'importanza in una funzione ToIntFunction<SenseData>.
            // In questo esempio, supponiamo che l'espressione sia "100 - (distance * 10) + (speed * 2)"
            // e sostituiamo "distance" e "speed" con i valori di SenseData.
            ToIntFunction<SenseData> weightFunction = data -> {
                int raw = 100 - (data.getDistance() * 10) + (data.getSpeed() * 2);
                // Normalizza automaticamente in [1,100]
                if (raw < 1) raw = 1;
                if (raw > 100) raw = 100;
                return raw;
            };

            return new DslDecisionRule(ruleName, condition, command, weightFunction);
        } catch (Exception e) {
            System.err.println("Errore nel parsing della regola: " + ruleText);
            e.printStackTrace();
            return null;
        }
    }
}
