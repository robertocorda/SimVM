package org.dracosoft;

import java.util.Map;

public class StepResult {

    // Stato di terminazione: se dopo l'esecuzione di questa istruzione
    // il programma risulta finito
    private final boolean terminated;

    // Istruzione così come appare nel sorgente
    private final String originalInstruction;

    // Nome del comando di base, es. "MOVE", "IF", "LABEL", ...
    private final String commandName;

    // Variabili coinvolte (nome -> valore al momento dell'esecuzione)
    private final Map<String, Integer> variablesUsed;

    // Info specifica per le label (se l'istruzione è una label),
    // altrimenti può essere null
    private final String labelName;

    // Info specifica per l'IF (condizione, risultato), se l'istruzione è un IF,
    // altrimenti può essere null
    private final IfDebugInfo ifDebugInfo;

    // Costruttore "completo": possiamo usare parametri null/empty per campi opzionali
    public StepResult(
            boolean terminated,
            String originalInstruction,
            String commandName,
            Map<String, Integer> variablesUsed,
            String labelName,
            IfDebugInfo ifDebugInfo
    ) {
        this.terminated = terminated;
        this.originalInstruction = originalInstruction;
        this.commandName = commandName;
        this.variablesUsed = variablesUsed;
        this.labelName = labelName;
        this.ifDebugInfo = ifDebugInfo;
    }

    // Getters...
    public boolean isTerminated() {
        return terminated;
    }

    public String getOriginalInstruction() {
        return originalInstruction;
    }

    public String getCommandName() {
        return commandName;
    }

    public Map<String, Integer> getVariablesUsed() {
        return variablesUsed;
    }

    public String getLabelName() {
        return labelName;
    }

    public IfDebugInfo getIfDebugInfo() {
        return ifDebugInfo;
    }

    // Inner class per IF
    public static class IfDebugInfo {
        private final String varToken;  // es. "V1"
        private final int varValue;     // es. 5
        private final String operator;  // "<", "==", ...
        private final int compareValue; // es. 10
        private final boolean conditionTrue;
        private final String jumpLabel; // es. "END"

        public IfDebugInfo(String varToken, int varValue, String operator,
                           int compareValue, boolean conditionTrue, String jumpLabel) {
            this.varToken = varToken;
            this.varValue = varValue;
            this.operator = operator;
            this.compareValue = compareValue;
            this.conditionTrue = conditionTrue;
            this.jumpLabel = jumpLabel;
        }

        // Getters...
        public String getVarToken() {
            return varToken;
        }

        public int getVarValue() {
            return varValue;
        }

        public String getOperator() {
            return operator;
        }

        public int getCompareValue() {
            return compareValue;
        }

        public boolean isConditionTrue() {
            return conditionTrue;
        }

        public String getJumpLabel() {
            return jumpLabel;
        }
    }

    public String toString() {
        return "[STEP] instr='" + getOriginalInstruction() + "' " +
                "cmd='" + getCommandName() + "' " +
                "varsUsed=" + getVariablesUsed() +
                " (terminated=" + isTerminated() + ")";
    }
}
