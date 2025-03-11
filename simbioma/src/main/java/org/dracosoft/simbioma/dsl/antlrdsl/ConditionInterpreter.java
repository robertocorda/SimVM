package org.dracosoft.simbioma.dsl.antlrdsl;

import org.dracosoft.simbioma.model.SenseData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;


public class ConditionInterpreter {

    /**
     * Converte conditionExpr in un Predicate<SenseData>.
     * Esempio di condizione: "see distance < 5 and color is RED"
     */
    public static Predicate<SenseData> parseConditionExpr(WeightedRulesPlParser.ConditionExprContext exprCtx) {
        // 1) Leggiamo inputType (SEE o MEM), potremmo usarlo o ignorarlo
        org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser.InputTypeContext inputTypeCtx = exprCtx.inputType();
        String inputType = (inputTypeCtx != null)? inputTypeCtx.getText().toLowerCase() : "see";
        // Se vuoi interpretare diversamente se "see" o "mem", puoi farlo qui.

        // 2) condition (AND condition)* => cond principale + zero o più cond secondarie
        List<WeightedRulesPlParser.ConditionContext> condList = new ArrayList<>();

        // condition() ti restituisce la prima condition
        WeightedRulesPlParser.ConditionContext firstCond = exprCtx.condition().getFirst();
        if (firstCond != null) {
            condList.add(firstCond);
        }

        // (AND condition)* potrebbe essere memorizzato nei child
        // Oppure ANTLR potrebbe generare un metodo exprCtx.condition(int i)
        // controlla come ANTLR genera i metodi.
        // Esempio: Se c’è un “exprCtx.condition(i)”, scorri finché c’è
        int i = 1;
        while (true) {
            try {
                WeightedRulesPlParser.ConditionContext c = exprCtx.condition(i);
                if (c != null) {
                    condList.add(c);
                    i++;
                } else {
                    break;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        // Combiniamo i predicati con un AND logico
        Predicate<SenseData> finalPredicate = data -> true;
        for (WeightedRulesPlParser.ConditionContext cCtx : condList) {
            finalPredicate = finalPredicate.and(parseSingleCondition(cCtx));
        }

        return finalPredicate;
    }

    /**
     * Converte una singola condition (field conditionOp value) in un Predicate<SenseData>.
     */
    private static Predicate<SenseData> parseSingleCondition(WeightedRulesPlParser.ConditionContext cCtx) {
        // field -> DISTANCE, COLOR, SPEED
        String field = cCtx.field().getText().toLowerCase();    // es. "distance"

        // conditionOp -> <, >, <=, >=, ==, is
        String op = cCtx.conditionOp().getText();               // es. "<"

        // value -> NUMBER o COLOR_LITERAL
        String valText = cCtx.value().getText();                // es. "5" o "RED"

        return data -> evaluateCondition(data, field, op, valText);
    }

    /**
     * Esegue il confronto su SenseData: ad es. "distance < 5", "color is RED", ...
     */
    private static boolean evaluateCondition(SenseData data, String field, String operator, String valText) {
        switch (field) {
            case "distance": {
                int actual = data.getDistance();
                int expected = parseIntOrZero(valText);
                return compareInt(actual, operator, expected);
            }
            case "speed": {
                int actual = data.getSpeed();
                int expected = parseIntOrZero(valText);
                return compareInt(actual, operator, expected);
            }
            case "color": {
                // Se operator == "is" o "==", confronta color ignoring case
                if (operator.equalsIgnoreCase("is") || operator.equals("==")) {
                    return data.getColor().equalsIgnoreCase(valText);
                }
                // Altri operatori non hanno senso -> false
                return false;
            }
            default:
                return false;
        }
    }

    /**
     * Confronta due int in base all'operatore (<, >, <=, >=, ==, is).
     */
    private static boolean compareInt(int actual, String operator, int expected) {
        switch (operator) {
            case "<":  return actual <  expected;
            case ">":  return actual >  expected;
            case "<=": return actual <= expected;
            case ">=": return actual >= expected;
            case "==":
            case "is": return actual == expected;
            default:   return false;
        }
    }

    /**
     * Prova a convertire la string in int, se fallisce ritorna 0
     */
    private static int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

