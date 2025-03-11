package org.dracosoft.simbioma.model;

import java.util.List;

public abstract class DecisionEngineBase implements DecisionEngine {

    protected final List<DecisionRule> rules;
    protected final DecisionRule defaultRule;

    /**
     * @param rules         Lista di regole da valutare.
     * @param defaultAction Azione di default se nessuna regola si applica.
     */
    public DecisionEngineBase(List<DecisionRule> rules, BiomaCommand defaultAction) {
        this.rules = rules;
        this.defaultRule = new DefaultRule(defaultAction);
    }

    /**
     * Esegue il ciclo Sense–Compute–Act:
     * - Rileva i dati sensoriali tramite sense()
     * - Valuta tutte le regole e sceglie quella con il peso maggiore
     * - Se nessuna regola si applica, restituisce l'azione di default.
     */
    public abstract BiomaIntent decideNextAction(Bioma bioma, SenseData sense);
}

