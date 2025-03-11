package org.dracosoft.antlrdsl;

import org.dracosoft.simbioma.dsl.antlrdsl.interpreter.ConditionInterpreter;
import org.dracosoft.simbioma.model.SenseData;
import org.dracosoft.simbioma.model.SenseDataFactory;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlLexer;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;


import static org.dracosoft.simbioma.model.EnvConstants.BLUE;
import static org.dracosoft.simbioma.model.EnvConstants.RED;
import static org.dracosoft.simbioma.model.SenseDataFactory.createSenseData;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionInterpreterTest {




    @Test
    public void testParseConditionExpr() {
        // DSL di esempio
        String dsl = "see distance < 5 and color is RED and speed > 2";

        // Creiamo il contesto conditionExpr con un input fittizio
        // Costruiremo una stringa minima che matchi 'conditionExpr'.
        // Oppure puoi parseare l'intera ifClause...

        // Esempio: "if applies { see distance < 5 and color is RED and speed > 2 }"
        String input = "if applies {" + dsl + "} do { STILL } with importance { 50 };";

        CharStream cs = CharStreams.fromString(input);
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WeightedRulesPlParser parser = new WeightedRulesPlParser(tokens);

        WeightedRulesPlParser.ProgramContext programCtx = parser.program();
        // Supponendo che la regola program -> decisionRule ...
        WeightedRulesPlParser.DecisionRuleContext ruleCtx =
                (WeightedRulesPlParser.DecisionRuleContext) programCtx.getChild(0);
        WeightedRulesPlParser.IfClauseContext ifCtx = ruleCtx.ifClause();

        // Ora interpretiamo la condizione
        Predicate<SenseData> cond = ConditionInterpreter.parseConditionExpr(ifCtx.conditionExpr());

        // Verifichiamo
        SenseData sd1 = createSenseData(3, RED, 3); // distance=3, color=RED, speed=3
        assertTrue(cond.test(sd1), "Dovrebbe essere true: distance<5, color=RED, speed>2");

        SenseData sd2 = createSenseData(6, RED, 3);
        assertFalse(cond.test(sd2), "distance=6 => false");

        SenseData sd3 = createSenseData(3, BLUE, 3);
        assertFalse(cond.test(sd3), "color=BLUE => false");

        SenseData sd4 = createSenseData(3, RED, 1);
        assertFalse(cond.test(sd4), "speed=1 => non >2 => false");
    }
}

