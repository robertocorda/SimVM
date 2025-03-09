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




}

