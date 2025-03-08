package org.dracosoft.simbioma;

import java.util.List;

import static org.dracosoft.simbioma.BiomaCommand.*;

public class DecisionEngineMax extends DecisionEngineBase {

    /**
     * @param rules         Lista di regole da valutare.
     */
    public DecisionEngineMax(List<DecisionRule> rules) {
        super(rules, STILL);
    }

    /**
     * Esegue il ciclo Sense–Compute–Act:
     * - Rileva i dati sensoriali tramite sense()
     * - Valuta tutte le regole e sceglie quella con il peso maggiore
     * - Se nessuna regola si applica, restituisce l'azione di default.
     */
    public BiomaIntent decideNextAction(Bioma bioma, SenseData sense) {
        SenseData data = sense;
        int maxWeight = -1;
        DecisionRule chosenRule = null;
        for (DecisionRule rule : rules) {
            if (rule.applies(data)) {
                int w = rule.getWeight(data);
                if (w > maxWeight) {
                    maxWeight = w;
                    chosenRule = rule;

                }
            }
        }

        if (chosenRule == null) {
            chosenRule = defaultRule;
        }

        System.out.println("decideNextAction chosenRule:" + chosenRule.toString());

        BiomaIntent intent = new BiomaIntent(chosenRule.getCommand());

        return intent;
    }
}

