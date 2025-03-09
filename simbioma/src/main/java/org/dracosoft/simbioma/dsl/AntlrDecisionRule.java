package org.dracosoft.simbioma.dsl;



//package org.dracosoft.weightedrulespl;

import org.dracosoft.simbioma.BiomaCommand;
import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Classe concreta che implementa DecisionRule,
 * utilizzando una condizione, un comando e un calcolo di peso.
 */
public class AntlrDecisionRule extends DecisionRule {

    private final String name;
    private final Predicate<SenseData> condition;
    private final BiomaCommand command;
    private final ToIntFunction<SenseData> weightFunction;

    public AntlrDecisionRule(String name,
                           Predicate<SenseData> condition,
                           BiomaCommand command,
                           ToIntFunction<SenseData> weightFunction) {
        this.name = name;
        this.condition = condition;
        this.command = command;
        this.weightFunction = weightFunction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean applies(SenseData data) {
        return condition.test(data);
    }

    @Override
    public BiomaCommand getCommand() {
        return command;
    }

    @Override
    public int getWeight(SenseData sense) {
        int raw = weightFunction.applyAsInt(sense);
        // normalizzazione in [1..100] se vuoi
        if (raw < 1) raw = 1;
        if (raw > 100) raw = 100;
        return raw;
    }

    @Override
    public String toString() {
        return "(Rule: " + getName() +
                ", Command: " + getCommand() + ")";
    }

    /**
     * Converte un DecisionRuleContext parseato in un DSLDecisionRule,
     * interpretando ifClause, doClause e withImportanceClause.
     */
    public static AntlrDecisionRule fromContext(WeightedRulesPlParser.DecisionRuleContext ctx) {
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

    private static Predicate<SenseData> parseCondition(WeightedRulesPlParser.IfClauseContext ifCtx) {
        // Se la tua grammatica definisce in ifCtx un 'conditionExpr',
        // puoi estrarne i campi, oppure prendere la stringa e interpretarla.
        // Esempio minimalista (stub):
        String text = ifCtx.conditionExpr().getText();
        // TODO: scrivere un mini interprete per "distance < 5 and color is RED ..."
        // Per brevità, facciamo un example stub che verifica se distance<5
        return (SenseData data) -> data.getDistance() < 5;
    }

    private static BiomaCommand parseCommand(WeightedRulesPlParser.DoClauseContext doCtx) {
        // In doClause -> commandExpr -> COMMAND
        String cmdText = doCtx.commandExpr().getText();
        // es. "ROTATE" -> BiomaCommand.ROTATE
        return BiomaCommand.valueOf(cmdText.toUpperCase());
    }

    private static ToIntFunction<SenseData> parseImportance(WeightedRulesPlParser.WithImportanceClauseContext impCtx) {
        String text = impCtx.importanceExpr().getText();
        // Se è un numero costante
        try {
            int val = Integer.parseInt(text);
            return (SenseData data) -> val;
        } catch(NumberFormatException ex) {
            // Altrimenti una formula semplice (stub).
            // Esempio: se text contiene "distance", facciamo "100 - distance*10"
            return (SenseData data) -> 100 - (data.getDistance() * 10);
        }
    }
}

