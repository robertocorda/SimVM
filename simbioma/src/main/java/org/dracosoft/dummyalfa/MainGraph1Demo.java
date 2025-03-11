package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.decisionengine.DecisionEngineMax;
import org.dracosoft.simbioma.dsl.antlrdsl.AntlrDecisionRuleParser;
import org.dracosoft.simbioma.dsl.manualdsl.ManualDecisionRuleParser;
import org.dracosoft.simbioma.model.BiomaIntent;
import org.dracosoft.simbioma.model.DecisionEngine;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;

import java.util.List;

public class MainGraph1Demo {
    public static void main(String[] args) {
        GraphicalEnvironmentIO env = new GraphicalEnvironmentIO();

        DummyBioma fixedSim = new DummyBioma("fixed");
        DummyBioma maxSim = new DummyBioma("max");
        DummyBioma antlrSim = new DummyBioma("antlr");

        String dslProgram =
                "if applies { if see object with distance < 5 and color is RED and speed > 2 }; do { PUSH } with importance { 100 - (distance * 10) + (speed * 2) }";
        List<DecisionRule> parsedRules = new ManualDecisionRuleParser().parseRules(dslProgram);
        if (parsedRules.isEmpty()) {
            System.out.println("Errore: nessuna regola parsata.");
            return;
        }

        String warpoleProgram = """
                if applies { see distance < 5 and color is RED and speed > 2 } do { ROTATE } with importance { 30 };
                if applies { see distance < 5 and color is RED and speed > 2 } do { PUSH } with importance { 100 };
                """;

        List<DecisionRule> parsedAntlrRules = new AntlrDecisionRuleParser().parseRules(warpoleProgram);

        DecisionEngine fixedEng = new DummyFixedDecisionEngine();
        DecisionEngine maxEng = new DecisionEngineMax(parsedRules);
        DecisionEngine maxAntlrEng = new DecisionEngineMax(parsedAntlrRules);

        fixedSim.setBrain(fixedEng);
        maxSim.setBrain(maxEng);
        antlrSim.setBrain(maxAntlrEng);

        env.placeBioma(fixedSim, 30, 30);
        env.placeBioma(maxSim, 25, 25);
        env.placeBioma(antlrSim, 10, 10);

        for (int tick = 1; tick <= 100; tick++) {
            System.out.println("=== TICK " + tick + " ===");

            DummyBioma[] biomas = {fixedSim, maxSim, antlrSim};

            for (int i = 0; i < biomas.length; i++) {
                DummyBioma bioma = biomas[i];

                SenseData senseData = env.sense(bioma);
                System.out.println(bioma.getName() + " sense: " + senseData);

                BiomaIntent intent = bioma.senseAndDecide(senseData);
                System.out.println(bioma.getName() + " decide: " + intent);

                env.applyAction(bioma, intent);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }


}
