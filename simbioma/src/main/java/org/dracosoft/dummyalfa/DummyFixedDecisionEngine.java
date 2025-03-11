package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.decisionengine.DecisionEngineRandom;
import org.dracosoft.simbioma.model.*;

import java.util.List;

public class DummyFixedDecisionEngine implements DecisionEngine {

    private final DecisionEngine decisionEngine;

    public DummyFixedDecisionEngine() {
        // Configuriamo le regole: l'ordine determina la priorità
        List<DecisionRule> rules = List.of(
                new RotateRule(50),  // Se la distanza < 5 e la velocità > 2, ruota
                new PushRule(50)     // Se la distanza < 5 e il colore è RED, spingi
        );
        // Azione di default: se nessuna regola si applica, stai fermo
        //this.decisionEngine = new DecisionEngineMax(rules);
        this.decisionEngine = new DecisionEngineRandom(rules);
    }

    @Override
    public BiomaIntent decideNextAction(Bioma bioma, SenseData input) {
        return decisionEngine.decideNextAction(bioma, input);
    }


}

