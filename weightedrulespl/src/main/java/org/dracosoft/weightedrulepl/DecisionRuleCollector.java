package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;

import java.util.ArrayList;
import java.util.List;

public class DecisionRuleCollector {

    /**
     * Visita ricorsivamente l'albero di parsing e raccoglie tutti i nodi
     * di tipo DecisionRuleContext in una lista.
     *
     * @param tree la radice (o un nodo generico) dell'albero di parsing
     * @return la lista di nodi DecisionRuleContext trovati
     */
    public static List<WeightedRulesPlParser.DecisionRuleContext> findDecisionRules(ParseTree tree) {
        List<WeightedRulesPlParser.DecisionRuleContext> result = new ArrayList<>();
        collectDecisionRules(tree, result);
        return result;
    }

    /**
     * Funzione di supporto ricorsiva per popolare la lista result.
     */
    private static void collectDecisionRules(ParseTree node,
                                             List<WeightedRulesPlParser.DecisionRuleContext> result) {

        // Se il nodo è una DecisionRuleContext, aggiungilo alla lista
        if (node instanceof WeightedRulesPlParser.DecisionRuleContext) {
            result.add((WeightedRulesPlParser.DecisionRuleContext) node);
        }

        // Scorre ricorsivamente i figli
        for (int i = 0; i < node.getChildCount(); i++) {
            ParseTree child = node.getChild(i);
            collectDecisionRules(child, result);
        }
    }
}

