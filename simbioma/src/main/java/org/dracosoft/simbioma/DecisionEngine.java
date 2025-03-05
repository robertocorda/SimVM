package org.dracosoft.simbioma;

import org.dracosoft.dummyalfa.DefaultRule;

import java.util.List;

public class DecisionEngine {

    private final List<DecisionRule> rules;
    private final String defaultAction;
    private final DecisionRule defaultRule;

    /**
     * @param rules         Lista di regole da valutare.
     * @param defaultAction Azione di default se nessuna regola si applica.
     */
    public DecisionEngine(List<DecisionRule> rules, String defaultAction) {
        this.rules = rules;
        this.defaultAction = defaultAction;
        this.defaultRule = new DefaultRule(defaultAction);
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
                int w = rule.getWeight();
                if (w > maxWeight) {
                    maxWeight = w;
                    chosenRule = rule;

                }
            }
        }

        System.out.println("decideNextAction chosenRule:" + chosenRule.toString());


        if (chosenRule == null) {
            chosenRule = defaultRule;
        }

        BiomaIntent intent = new BiomaIntent(chosenRule.getAction());

        return intent;
    }
}

