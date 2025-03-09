package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;
import org.dracosoft.simbioma.dsl.AntlrDecisionRuleParser;
import org.dracosoft.simbioma.dsl.ManualDecisionRuleParser;

import java.util.List;

public class MainGraph1Demo {
    public static void main(String[] args) {
        GraphicalEnvironmentIO env = new GraphicalEnvironmentIO();

        DummyBioma fixedSim = new DummyBioma("fixed");
        DummyBioma maxSim = new DummyBioma("max");
        DummyBioma antlrSim = new DummyBioma("antlr");

        String dslProgram = "if applies { if see object with distance < 5 and color is RED and speed > 2 }; do { PUSH } with importance { 100 - (distance * 10) + (speed * 2) }";
        List<DecisionRule> parsedRules = new ManualDecisionRuleParser().parseRules(dslProgram);
        if (parsedRules.isEmpty()) {
            System.out.println("Errore: nessuna regola parsata.");
            return;
        }

        String warpoleProgram = """
                if applies { see distance < 5 and color is RED and speed > 2 } do { ROTATE } with importance { 100 };
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
            DecisionEngine[] engines = {fixedEng, maxEng, maxAntlrEng};

            for (int i = 0; i < biomas.length; i++) {
                DummyBioma bioma = biomas[i];
                DecisionEngine engine = engines[i];

                SenseData senseData = env.sense(bioma);
                System.out.println(bioma.getName() + " sense: " + senseData);

                BiomaIntent intent = engine.decideNextAction(bioma, senseData);
                System.out.println(bioma.getName() + " decide: " + intent);

                applyAction(env, bioma, intent);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void applyAction(GraphicalEnvironmentIO env, DummyBioma bioma, BiomaIntent intent) {
        switch (intent.getCommand()) {
            case PUSH -> env.push(bioma);
            case ROTATE -> bioma.performInnerAction(intent);
        }
    }
}
