package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.*;

public class VerboseErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        String error = "PIPPO line " + line + ":" + charPositionInLine + " " + msg;
        // Puoi stampare, loggare o salvare l'errore in una struttura dati
        System.err.println(error);
    }
}

