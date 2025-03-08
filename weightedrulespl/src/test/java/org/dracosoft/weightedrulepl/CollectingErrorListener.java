package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class CollectingErrorListener extends BaseErrorListener {

    private final List<String> errorMessages = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        String error = "line " + line + ":" + charPositionInLine + " " + msg;
        // Salva l'errore in una lista
        errorMessages.add(error);
        // Puoi anche loggarlo su un logger o stamparlo su console se preferisci:
        // System.err.println("Custom error: " + error);
    }

    /**
     * Restituisce la lista dei messaggi di errore raccolti.
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Permette di resettare la lista degli errori (se necessario).
     */
    public void clearErrors() {
        errorMessages.clear();
    }
}

