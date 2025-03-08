package org.dracosoft.dummyalfa;

import org.dracosoft.dslprogram.RandomDSLProgramGenerator;
import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;
import org.dracosoft.simbioma.dsl.DslDecisionRule;
import org.dracosoft.simbioma.dsl.GeneralizedDecisionRuleParser;

import java.util.List;

public class MainRandomDslGenerator {

    public static void main(String[] args) {


        while (true) {
            String dslProgram = RandomDSLProgramGenerator.generateProgram(1);
            List<DecisionRule> rules = GeneralizedDecisionRuleParser.parseRules(dslProgram);
            DecisionRule rule = rules.getFirst();


            // Creiamo un SenseData che soddisfa la condizione: distance=3, color=RED, speed=4.
            SenseData senseData = new SenseData(3, "RED", 4);
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
