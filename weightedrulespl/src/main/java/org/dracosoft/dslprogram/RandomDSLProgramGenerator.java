package org.dracosoft.dslprogram;

import java.util.List;
import java.util.Random;

public class RandomDSLProgramGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] COLORS = {"RED", "BLUE", "GREEN", "YELLOW"};
    private static final String[] COMMANDS = {"ROTATE", "PUSH", "STILL"};
    private static final String[] OPERATORS = {"<", ">", "<=", ">=", "=="};


    /**
     * Restituisce un elemento casuale dall'array.
     */
    private static String randomElement(String[] arr) {
        return arr[RANDOM.nextInt(arr.length)];
    }
    /**
     * Genera un programma DSL composto da numberOfRules regole,
     * separate da una riga vuota.
     */
    public static String generateProgram(int numberOfRules) {
        StringBuilder program = new StringBuilder();
        for (int i = 0; i < numberOfRules; i++) {
            String rule = generateRule();
            program.append(rule);
            if (i < numberOfRules - 1) {
                program.append("\n\n"); // separa le regole con una riga vuota
            }
        }
        return program.toString();
    }

    /**
     * Genera una singola regola DSL casuale.
     */
    private static String generateRule() {
        // Genera la parte condizionale
        String condition = generateCondition();
        // Genera il comando casuale
        String command = randomElement(COMMANDS);
        // Genera l'espressione per l'importanza (costante o formula)
        String importance = generateImportanceExpression();

        // Assembla la regola DSL
        String rule = "if applies { if see object with " + condition +
                " }; do { " + command +
                " } with importance { " + importance + " }";
        return rule;
    }

    /**
     * Genera una stringa contenente una o più condizioni, combinate con "and".
     */
    private static String generateCondition() {
        // Genera tra 1 e 3 condizioni
        int count = RANDOM.nextInt(3) + 1;
        StringBuilder condBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String cond = generateSingleCondition();
            condBuilder.append(cond);
            if (i < count - 1) {
                condBuilder.append(" and ");
            }
        }
        return condBuilder.toString();
    }

    /**
     * Genera una singola condizione.
     * Possibili chiavi: "distance", "color", "speed".
     */
    private static String generateSingleCondition() {
        int choice = RANDOM.nextInt(3);
        switch (choice) {
            case 0:
                // Condizione su distance, ad es. "distance < 5"
                String op = randomElement(OPERATORS);
                int dist = RANDOM.nextInt(20) + 1; // 1..20
                return "distance " + op + " " + dist;
            case 1:
                // Condizione su color, nella forma "color is RED"
                String color = randomElement(COLORS);
                return "color is " + color;
            case 2:
                // Condizione su speed, ad es. "speed > 2"
                op = randomElement(OPERATORS);
                int speed = RANDOM.nextInt(10) + 1; // 1..10
                return "speed " + op + " " + speed;
            default:
                return ""; // mai raggiunto
        }
    }

    /**
     * Genera casulamente un'espressione per l'importanza.
     */
    private static String generateImportanceExpression() {
        // Con il 50% di probabilità restituisce una costante casuale da 1 a 100
        if (RANDOM.nextBoolean()) {
            int value = RANDOM.nextInt(100) + 1;
            return String.valueOf(value);
        } else {
            // Altrimenti, genera un'espressione casuale che coinvolge variabili e/o costanti.
            String[] operators = {"+", "-", "*", "/"};
            String op = operators[RANDOM.nextInt(operators.length)];

            // Scegli a caso la struttura: 0 per espressione con una variabile, 1 per espressione con due variabili
            int structure = RANDOM.nextInt(2);
            if (structure == 0) {
                // Espressione a variabile singola
                String[] variables = {"distance", "speed"};
                String var = variables[RANDOM.nextInt(variables.length)];
                int constant = RANDOM.nextInt(100) + 1;
                // Scegli in modo casuale se la variabile viene prima o dopo la costante
                if (RANDOM.nextBoolean()) {
                    return var + " " + op + " " + constant;
                } else {
                    return constant + " " + op + " " + var;
                }
            } else {
                // Espressione a due variabili: ad esempio "distance * speed"
                String[] variables = {"distance", "speed"};
                String var1 = variables[RANDOM.nextInt(variables.length)];
                String var2 = variables[RANDOM.nextInt(variables.length)];
                return var1 + " " + op + " " + var2;
            }
        }
    }
}