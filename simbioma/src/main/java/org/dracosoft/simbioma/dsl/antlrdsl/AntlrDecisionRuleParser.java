package org.dracosoft.simbioma.dsl.antlrdsl;

import org.dracosoft.simbioma.dsl.antlrdsl.interpreter.ConditionInterpreter;
import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;
import org.dracosoft.simbioma.model.ToDecisionRule;
import org.dracosoft.weightedrulepl.Warpole;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import static org.dracosoft.simbioma.model.SenseDataFactory.getDistance;

public class AntlrDecisionRuleParser implements ToDecisionRule {

    public AntlrDecisionRuleParser() {
        //
    }

    @Override
    public List<DecisionRule> parseRules(String dslProgram) {
        Warpole warpole = new Warpole(dslProgram);

        if (warpole.isParsingOk() && warpole.isLexerOk()) {
            List<WeightedRulesPlParser.DecisionRuleContext> decisionList = warpole.getDecisionRulesContext();
            List<DecisionRule> decisionRules = new ArrayList<>();
            for (WeightedRulesPlParser.DecisionRuleContext decisionRuleContext : decisionList) {
                decisionRules.add(fromContext(decisionRuleContext));
            }
            return decisionRules;
        } else {
            // errore
            System.out.println("AntlrDecisionRuleParserProgram.parseRules failed " + warpole.getParserError());
        }

        // altrimenti ritorna una lista vuota
        return List.of();
    }

    /**
     * Converte un DecisionRuleContext parseato in un DSLDecisionRule,
     * interpretando ifClause, doClause e withImportanceClause.
     */
    private AntlrDecisionRule fromContext(WeightedRulesPlParser.DecisionRuleContext ctx) {
        // 1) ifClause (condizione)
        WeightedRulesPlParser.IfClauseContext ifCtx = ctx.ifClause();
        Predicate<SenseData> cond = parseCondition(ifCtx);

        // 2) doClause (comando)
        WeightedRulesPlParser.DoClauseContext doCtx = ctx.doClause();
        BiomaCommand cmd = parseCommand(doCtx);

        // 3) withImportanceClause (peso)
        WeightedRulesPlParser.WithImportanceClauseContext impCtx = ctx.withImportanceClause();
        ToIntFunction<SenseData> weightF = parseImportance(impCtx);

        // Crea un nome, ad esempio estrai la condizione testuale o un generico "rule..."
        String ruleName = "Rule@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine();

        return new AntlrDecisionRule(ruleName, cond, cmd, weightF);
    }

    // --------------- Metodi di parsing dei sotto-contesti -------------------
    private Predicate<SenseData> parseCondition(WeightedRulesPlParser.IfClauseContext ifCtx) {

        // Esempio minimalista (stub):
        //String text = ifCtx.conditionExpr().getText();
        //return (SenseData data) -> getDistance(data) < 5;

        // WARN mini interprete per "distance < 5 and color is RED ..."
        Predicate<SenseData> cond = ConditionInterpreter.parseConditionExpr(ifCtx.conditionExpr());
        return cond;
    }

    private BiomaCommand parseCommand(WeightedRulesPlParser.DoClauseContext doCtx) {
        // In doClause -> commandExpr -> COMMAND
        String cmdText = doCtx.commandExpr().getText();
        // es. "ROTATE" -> BiomaCommand.ROTATE
        return BiomaCommand.valueOf(cmdText.toUpperCase());
    }

    private ToIntFunction<SenseData> parseImportance(WeightedRulesPlParser.WithImportanceClauseContext impCtx) {
        String text = impCtx.importanceExpr().getText();

        try {
            // Se è un numero costante
            int val = Integer.parseInt(text);
            return (SenseData data) -> val;
        } catch(NumberFormatException ex) {
            // TODO Altrimenti una formula semplice (stub).
            // Esempio: se text contiene "distance", facciamo "100 - distance*10"
            return (SenseData data) -> 0;
        }
    }
}
