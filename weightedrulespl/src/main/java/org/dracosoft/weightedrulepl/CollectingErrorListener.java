package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class CollectingErrorListener extends BaseErrorListener {

    private final List<String> errorMessages = new ArrayList<>();
    private String type;

    public CollectingErrorListener(String type) {
        this.type = type;
    }


    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        if (errorMessages.isEmpty()) {
            errorMessages.add("Error type is " + type + " -->");
        }

        String error = "line " + line + ":" + charPositionInLine + " " + msg;
        errorMessages.add(error);

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

