package org.dracosoft.simbioma.dsl.manualdsl;

import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;
import org.dracosoft.simbioma.model.ToDecisionRule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Parser generalizzato per il DSL decisionale.
 */
public class ManualDecisionRuleParser implements ToDecisionRule {

    /**
     * Data una stringa contenente un solo programma DSL (TODO multi program)
     * restituisce una lista di DecisionRule.
     */
    public List<DecisionRule> parseRules(final String dslProgram) {

        String cleanProgram = dslProgram.replaceAll("\\s+", " ").trim();
        List<DecisionRule> rules = new ArrayList<>();
        String[] ruleLines = cleanProgram.split("\\r?\\n");
        for(String line : ruleLines) {
            line = line.trim();
            if(line.isEmpty()) continue;
            DecisionRule rule = parseSingleRule(line);
            if(rule != null) {
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Parser di una singola regola DSL.
     * Aspettiamo che la regola sia della forma:
     * if applies { <condition> }; do { <command> } with importance { <importance expression> }
     */
    private DecisionRule parseSingleRule(String ruleText) {
        try {
            int ifStart = ruleText.indexOf("if applies {");
            int ifEnd = ruleText.indexOf("}", ifStart);
            if(ifStart == -1 || ifEnd == -1) {
                throw new IllegalArgumentException("Regola non valida, manca il blocco 'if applies': " + ruleText);
            }
            String conditionText = ruleText.substring(ifStart + "if applies {".length(), ifEnd).trim();

            int doStart = ruleText.indexOf("do {", ifEnd);
            int doEnd = ruleText.indexOf("}", doStart);
            if(doStart == -1 || doEnd == -1) {
                throw new IllegalArgumentException("Regola non valida, manca il blocco 'do': " + ruleText);
            }
            String commandText = ruleText.substring(doStart + "do {".length(), doEnd).trim();

            int impStart = ruleText.indexOf("with importance {", doEnd);
            int impEnd = ruleText.indexOf("}", impStart);
            if(impStart == -1 || impEnd == -1) {
                throw new IllegalArgumentException("Regola non valida, manca il blocco 'with importance': " + ruleText);
            }
            String importanceText = ruleText.substring(impStart + "with importance {".length(), impEnd).trim();

            String ruleName = "Rule: " + conditionText;

            // Parse della condizione in un Predicate<SenseData>
            Predicate<SenseData> condition = parseCondition(conditionText);

            // Parse del comando: assumiamo un solo comando, che corrisponde a un valore di BiomaCommand.
            BiomaCommand command;
            try {
                command = BiomaCommand.valueOf(commandText.toUpperCase());
            } catch(Exception e) {
                throw new IllegalArgumentException("Comando non riconosciuto: " + commandText, e);
            }

            // Parse dell'importanza in un ToIntFunction<SenseData>
            ToIntFunction<SenseData> weightFunction = parseImportance(importanceText);

            return new DslDecisionRule(ruleName, condition, command, weightFunction);
        } catch(Exception ex) {
            System.err.println("Errore nel parsing della regola: " + ruleText + " ex message:" + ex.getMessage());
            //ex.printStackTrace();
            return null;
        }
    }

    private Predicate<SenseData> parseCondition(String conditionText) {
        // Rimuove un eventuale prefisso "if see object with "
        String lower = conditionText.toLowerCase();
        String prefix = "if see object with ";
        if (lower.startsWith(prefix)) {
            conditionText = conditionText.substring(prefix.length()).trim();
        }

        // Divide la condizione in sotto-condizioni separate da "and" (ignorando il case)
        String[] parts = conditionText.split("(?i)\\sand\\s");
        List<Predicate<SenseData>> predicates = new ArrayList<>();

        for (String part : parts) {
            part = part.trim();
            String[] tokens = part.split("\\s+");

            if (tokens.length == 2) {
                // Ad esempio: "color RED"
                String key = tokens[0].toLowerCase();
                String value = tokens[1];
                if (key.equals("color")) {
                    predicates.add(data -> data.getColor().equalsIgnoreCase(value));
                } else {
                    throw new IllegalArgumentException("Condizione sconosciuta: " + part);
                }
            } else if (tokens.length == 3) {
                // Gestisce casi come:
                // - "distance < 5" oppure "speed > 2"
                // - "color is RED"
                String key = tokens[0].toLowerCase();
                String operator = tokens[1];
                String value = tokens[2];

                if (key.equals("color")) {
                    // Supporta la forma "color is RED"
                    if (operator.equalsIgnoreCase("is")) {
                        predicates.add(data -> data.getColor().equalsIgnoreCase(value));
                    } else {
                        throw new IllegalArgumentException("Operatore non supportato per 'color': " + operator);
                    }
                } else if (key.equals("distance") || key.equals("speed")) {
                    int num = Integer.parseInt(value);
                    switch (operator) {
                        case "<":
                            if (key.equals("distance")) {
                                predicates.add(data -> data.getDistance() < num);
                            } else {
                                predicates.add(data -> data.getSpeed() < num);
                            }
                            break;
                        case ">":
                            if (key.equals("distance")) {
                                predicates.add(data -> data.getDistance() > num);
                            } else {
                                predicates.add(data -> data.getSpeed() > num);
                            }
                            break;
                        case "<=":
                            if (key.equals("distance")) {
                                predicates.add(data -> data.getDistance() <= num);
                            } else {
                                predicates.add(data -> data.getSpeed() <= num);
                            }
                            break;
                        case ">=":
                            if (key.equals("distance")) {
                                predicates.add(data -> data.getDistance() >= num);
                            } else {
                                predicates.add(data -> data.getSpeed() >= num);
                            }
                            break;
                        case "==":
                            if (key.equals("distance")) {
                                predicates.add(data -> data.getDistance() == num);
                            } else {
                                predicates.add(data -> data.getSpeed() == num);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Operatore non supportato in condizione: " + operator);
                    }
                } else {
                    throw new IllegalArgumentException("Chiave non riconosciuta nella condizione: " + key);
                }
            } else {
                throw new IllegalArgumentException("Formato condizione non riconosciuto: " + part);
            }
        }

        // Combina tutte le condizioni con AND logico
        return predicates.stream().reduce(x -> true, Predicate::and);
    }


    /**
     * Converte l'espressione di importanza in un ToIntFunction<SenseData>.
     * Se l'espressione è un numero costante, restituisce quella costante.
     * Altrimenti, usa il motore JavaScript per valutare l'espressione,
     * sostituendo le variabili "distance" e "speed".
     */
    private static ToIntFunction<SenseData> parseImportance(String importanceText) {
        importanceText = importanceText.trim();
        try {
            int constant = Integer.parseInt(importanceText);
            return data -> {
                if (constant < 1) return 1;
                return Math.min(constant, 100);
            };
        } catch(NumberFormatException e) {
            // Utilizza il motore JavaScript per eseguire l'espressione.
            String finalImportanceText = importanceText;
            return data -> {
                try {
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                    engine.put("distance", data.getDistance());
                    engine.put("speed", data.getSpeed());
                    // Se necessario, si potrebbe anche passare "color" o altri parametri.
                    Object result = engine.eval(finalImportanceText);
                    int intResult = ((Number) result).intValue();
                    if (intResult < 1) intResult = 1;
                    if (intResult > 100) intResult = 100;
                    return intResult;
                } catch(Exception ex) {
                    throw new RuntimeException("Errore nella valutazione dell'importance: " + finalImportanceText, ex);
                }
            };
        }
    }
}

