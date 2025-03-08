package org.dracosoft.simbioma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DecisionEngineRandom extends DecisionEngineBase {
    /**
     * @param rules Lista di regole da valutare.
     */
    public DecisionEngineRandom(List<DecisionRule> rules) {
        super(rules, BiomaCommand.STILL);
    }

    /**
     * Seleziona una regola applicabile in modo random ponderato.
     *
     * @param rules lista completa di DecisionRule
     * @param sense i dati sensoriali da utilizzare per verificare le regole
     * @return la DecisionRule scelta in base ai pesi, oppure null se nessuna regola si applica
     */
    private static DecisionRule chooseRuleRandomly(List<DecisionRule> rules, SenseData sense) {
        // Filtra le regole applicabili e somma i loro pesi
        List<DecisionRule> applicableRules = new ArrayList<>();
        int totalWeight = 0;
        for (DecisionRule rule : rules) {
            if (rule.applies(sense)) {
                applicableRules.add(rule);
                totalWeight += rule.getWeight(sense);
            }
        }

        // Se nessuna regola si applica, ritorna null
        if (totalWeight == 0) {
            return new DefaultRule(BiomaCommand.STILL);
        }

        // Genera un numero casuale tra 0 (incluso) e totalWeight (escluso)
        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);

        System.out.println("decideNextAction totalWeight:" + totalWeight);
        System.out.println("decideNextAction randomValue:" + randomValue);
        // Itera sulle regole sottraendo il peso di ciascuna dal valore casuale
        for (DecisionRule rule : applicableRules) {
            randomValue -= rule.getWeight(sense);
            if (randomValue < 0) {
                return rule;
            }
        }

        // Questo punto non dovrebbe mai essere raggiunto
        return null;
    }


    @Override
    public BiomaIntent decideNextAction(Bioma bioma, SenseData sense) {

        DecisionRule chosenRule = chooseRuleRandomly(this.rules, sense);

        System.out.println("decideNextAction chosenRule:" + chosenRule.toString());

        if (chosenRule == null) {
            chosenRule = defaultRule;
        }

        BiomaIntent intent = new BiomaIntent(chosenRule.getCommand());

        return intent;
    }
}
