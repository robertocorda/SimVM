package org.dracosoft.simbioma;

public interface EnvironmentIO {
    /**
     * Fornisce al Bioma dati sensoriali (basso livello):
     * distanza, colore, velocità di un eventuale oggetto vicino.
     */
    SenseData sense(Bioma bioma);

    /**
     * Tenta di muovere (spingere) il Bioma in avanti di 1 passo
     * in base alla sua direzione, se possibile.
     * @return true se il movimento ha avuto successo.
     */
    boolean push(Bioma bioma);

    /**
     * Per scopi di debug/log.
     */
    void log(String message);


    /**
     * Riceve l'intenzione del bioma e tenta di applicare l'azione nel mondo.
     * Restituisce true se l'azione ha avuto successo, false altrimenti.
     */
    boolean applyIntent(Bioma bioma, BiomaIntent intent);


}

