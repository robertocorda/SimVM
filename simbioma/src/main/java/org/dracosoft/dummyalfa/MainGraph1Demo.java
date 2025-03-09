package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;
import org.dracosoft.simbioma.dsl.AntlrDecisionRuleParser;
import org.dracosoft.simbioma.dsl.ManualDecisionRuleParser;
import org.dracosoft.simbioma.dsl.ToDecisionRule;

import java.util.List;

// MainGraph1DemoChe.java

public class MainGraph1Demo {
    public static void main(String[] args) {
        // Crea l'ambiente grafico
        GraphicalEnvironmentIO env = new GraphicalEnvironmentIO();

        // Crea bioma
        DummyBioma fixedSim = new DummyBioma("fixed");
        DummyBioma maxSim = new DummyBioma("max");
        DummyBioma antlrSim = new DummyBioma("antlr");

        // Definisci un DSL program per la regola decisionale.
        // Esempio: se percepisce un oggetto con distance < 5, color is RED e speed > 2,
        // allora l'azione è PUSH, con importanza calcolata dalla formula 100 - (distance * 10) + (speed * 2)
        String dslProgram = "if applies { if see object with distance < 5 and color is RED and speed > 2 }; " +
                "do { PUSH } with importance { 100 - (distance * 10) + (speed * 2) }";
        // Utilizza il parser DSL per ottenere la lista di regole
        ManualDecisionRuleParser parser = new ManualDecisionRuleParser();
        List<DecisionRule> parsedRules = parser.parseRules(dslProgram);
        if(parsedRules.isEmpty()){
            System.out.println("Errore: nessuna regola parsata.");
            return;
        }

        String warpoleProgram = """
                if applies { 
                    see distance < 5 and color is RED and speed > 2 
                } do { 
                    ROTATE 
                } with importance { 
                    100 
                } 
                ;    
                                
                                
                if applies { 
                    see distance < 5 and color is RED and speed > 2 
                } do { 
                    PUSH 
                } with importance { 
                    100 
                } 
                ;               
                """;

        ToDecisionRule parserAntlr = new AntlrDecisionRuleParser();
        List<DecisionRule> parsedAntlrRules = parserAntlr.parseRules(warpoleProgram);
        // Crea un cervello (DummyBrain) che utilizza questa regola per decidere l'azione
        DecisionEngine fixedEng = new DummyFixedDecisionEngine();
        DecisionEngine maxEng = new DecisionEngineMax(parsedRules);
        DecisionEngine maxAntlrEng = new DecisionEngineMax(parsedAntlrRules);

        fixedSim.setBrain(fixedEng);
        maxSim.setBrain(maxEng);
        antlrSim.setBrain(maxAntlrEng);

        // Posiziona i bioma nell'ambiente (l'ambiente conosce le coordinate, non i bioma)
        env.placeBioma(fixedSim, 30, 30);
        env.placeBioma(maxSim, 25, 25);
        env.placeBioma(antlrSim, 10, 10);

        // Ciclo di simulazione: per 10 tick, ogni bioma riceve i dati sensoriali e agisce
        for (int tick = 1; tick <= 100; tick++) {
            System.out.println("=== TICK " + tick + " ===");

            // Per ogni bioma, l'ambiente fornisce i dati sensoriali
            SenseData sense1 = env.sense(fixedSim);
            SenseData sense2 = env.sense(maxSim);
            SenseData sense3 = env.sense(antlrSim);
            System.out.println(fixedSim.getName() + " sense: " + sense1);
            System.out.println(maxSim.getName() + " sense2: " + sense2);
            System.out.println(antlrSim.getName() + " sense2: " + sense3);

            // Il cervello decide l'azione basandosi sui dati ricevuti
            BiomaIntent intent1 = fixedEng.decideNextAction(fixedSim, sense1);
            BiomaIntent intent2 = maxEng.decideNextAction(maxSim, sense2);
            BiomaIntent intent3 = maxAntlrEng.decideNextAction(maxSim, sense2);

            System.out.println(fixedSim.getName() + " decide: " + intent1);
            System.out.println(maxSim.getName() + " decide: " + intent2);
            System.out.println(antlrSim.getName() + " decide: " + intent3);

            // Applica l'azione:
            // Se l'azione è PUSH, l'ambiente muove il bioma in base alla sua direzione
            // Se l'azione è ROTATE, il bioma ruota (cambia direzione) e l'ambiente visualizza il cambiamento
            if (intent1.getCommand() == BiomaCommand.PUSH) {
                env.push(fixedSim);
            } else if (intent1.getCommand() == BiomaCommand.ROTATE) {
                fixedSim.performInnerAction(intent1);
                // Dopo la rotazione, puoi decidere se eseguire anche un PUSH
            }
            if (intent2.getCommand() == BiomaCommand.PUSH) {
                env.push(maxSim);
            } else if (intent2.getCommand() == BiomaCommand.ROTATE) {
                maxSim.performInnerAction(intent1);
            }
            if (intent3.getCommand() == BiomaCommand.PUSH) {
                env.push(antlrSim);
            } else if (intent3.getCommand() == BiomaCommand.ROTATE) {
                antlrSim.performInnerAction(intent1);
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

