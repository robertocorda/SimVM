package org.dracosoft.simbioma.model;

public interface Bioma {

    String getName();

    /**
     * Direzioni di base.
     */
    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    /**
     * Imposta il "cervello" (Brain) che deciderà le azioni del Bioma.
     */
    void setBrain(DecisionEngine decisionEngine);

    /**
     * Restituisce la direzione attuale.
     */
    Direction getDirection();

    /**
     * Riceve in input i dati sensoriali (ad es. distanza, colore, velocità) e, basandosi sullo stato interno,
     * restituisce un'intenzione (il comando da eseguire).
     */
    BiomaIntent senseAndDecide(SenseData input);

    /**
     * Restituisce l'energia residua.
     */
    int getEnergy();




}

