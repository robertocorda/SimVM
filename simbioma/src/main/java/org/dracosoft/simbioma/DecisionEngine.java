package org.dracosoft.simbioma;

public interface DecisionEngine {
    /**
     * Dato il Bioma, decide la prossima azione (nome di un'azione built-in).
     * Tipicamente userà i "sensi" forniti dall'ambiente:
     * environmentIO.sense(bioma) -> SenseData
     */
    BiomaIntent decideNextAction(Bioma bioma, SenseData input);
}

