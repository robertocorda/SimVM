package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;

import java.util.List;

public class DummyBrain implements Brain {

    private final DecisionEngine decisionEngine;

    public DummyBrain() {
        // Configuriamo le regole: l'ordine determina la priorità
        List<DecisionRule> rules = List.of(
                new RotateRule(80),  // Se la distanza < 5 e la velocità > 2, ruota
                new PushRule(60)     // Se la distanza < 5 e il colore è RED, spingi
        );
        // Azione di default: se nessuna regola si applica, stai fermo
        this.decisionEngine = new DecisionEngine(rules, "STILL");
    }

    @Override
    public BiomaIntent decideNextAction(Bioma bioma, SenseData input) {
        return decisionEngine.decideNextAction(bioma, input);
    }


}

