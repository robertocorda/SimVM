package org.dracosoft.dummyalfa;

import org.dracosoft.dslprogram.RandomDSLProgramGenerator;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;
import org.dracosoft.simbioma.dsl.manualdsl.DslDecisionRule;
import org.dracosoft.simbioma.dsl.manualdsl.ManualDecisionRuleParser;

import java.util.List;

import static org.dracosoft.simbioma.model.SenseDataFactory.createSenseData;

public class MainRandomDslGenerator {

    public static void main(String[] args) {


        while (true) {
            String dslProgram = RandomDSLProgramGenerator.generateProgram(1);
            ManualDecisionRuleParser parser = new ManualDecisionRuleParser();
            List<DecisionRule> rules = parser.parseRules(dslProgram);
            DecisionRule rule = rules.get(0);


            // Creiamo un SenseData che soddisfa la condizione: distance=3, color=RED, speed=4.
            SenseData senseData =createSenseData(3, "RED", 4);
            boolean applies = rule.applies(senseData);


            DslDecisionRule dslRule = (DslDecisionRule) rule;
            int computedWeight = dslRule.getWeight(senseData);

            System.out.println(
                    "prog: dslProgram " + dslProgram + "\n"
                            + "senseData:" + senseData + "\n"
                            + "applies:" + applies + "\n"
                            + "weight:" + computedWeight + "\n");

            if (applies) break;

        }
    }
}
