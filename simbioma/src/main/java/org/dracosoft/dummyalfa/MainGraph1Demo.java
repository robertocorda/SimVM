package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;
import org.dracosoft.simbioma.dsl.GeneralizedDecisionRuleParser;

import java.util.List;

// MainGraph1DemoChe.java
public class MainGraph1Demo {
    public static void main(String[] args) {
        // Crea l'ambiente grafico
        GraphicalEnvironmentIO env = new GraphicalEnvironmentIO();

        // Crea due bioma
        DummyBioma bioma1 = new DummyBioma("Bioma1");
        DummyBioma bioma2 = new DummyBioma("Bioma2");

        // Definisci un DSL program per la regola decisionale.
        // Esempio: se percepisce un oggetto con distance < 5, color is RED e speed > 2,
        // allora l'azione è PUSH, con importanza calcolata dalla formula 100 - (distance * 10) + (speed * 2)
        String dslProgram = "if applies { if see object with distance < 5 and color is RED and speed > 2 }; " +
                "do { PUSH } with importance { 100 - (distance * 10) + (speed * 2) }";
        // Utilizza il parser DSL per ottenere la lista di regole
        List<DecisionRule> parsedRules = GeneralizedDecisionRuleParser.parseRules(dslProgram);
        if(parsedRules.isEmpty()){
            System.out.println("Errore: nessuna regola parsata.");
            return;
        }

        // Crea un cervello (DummyBrain) che utilizza questa regola per decidere l'azione
        DecisionEngine brain1 = new DummyFixedDecisionEngine();
        DecisionEngine brain2 = new DecisionEngineMax(parsedRules);
        bioma1.setBrain(brain1);
        bioma2.setBrain(brain2);

        // Posiziona i bioma nell'ambiente (l'ambiente conosce le coordinate, non i bioma)
        env.placeBioma(bioma1, 2, 2);
        env.placeBioma(bioma2, 10, 4);

        // Ciclo di simulazione: per 10 tick, ogni bioma riceve i dati sensoriali e agisce
        for (int tick = 1; tick <= 100; tick++) {
            System.out.println("=== TICK " + tick + " ===");

            // Per ogni bioma, l'ambiente fornisce i dati sensoriali
            SenseData sense1 = env.sense(bioma1);
            SenseData sense2 = env.sense(bioma2);
            System.out.println(bioma1.getName() + " sense: " + sense1);
            System.out.println(bioma2.getName() + " sense2: " + sense2);

            // Il cervello decide l'azione basandosi sui dati ricevuti
            BiomaIntent intent1 = brain1.decideNextAction(bioma1, sense1);
            BiomaIntent intent2 = brain2.decideNextAction(bioma2, sense2);

            System.out.println(bioma1.getName() + " decide: " + intent1);
            System.out.println(bioma2.getName() + " decide: " + intent2);

            // Applica l'azione:
            // Se l'azione è PUSH, l'ambiente muove il bioma in base alla sua direzione
            // Se l'azione è ROTATE, il bioma ruota (cambia direzione) e l'ambiente visualizza il cambiamento
            if (intent1.getCommand() == BiomaCommand.PUSH) {
                env.push(bioma1);
            } else if (intent1.getCommand() == BiomaCommand.ROTATE) {
                bioma1.performInnerAction(intent1);
                // Dopo la rotazione, puoi decidere se eseguire anche un PUSH
            }
            if (intent2.getCommand() == BiomaCommand.PUSH) {
                env.push(bioma2);
            } else if (intent2.getCommand() == BiomaCommand.ROTATE) {
                bioma1.performInnerAction(intent1);
            }

            // Attendi un po' per visualizzare i cambiamenti
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

