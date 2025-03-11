package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;

public class PushRule extends DecisionRule {
    private final int weight; // es. 60

    public PushRule(int weight) {
        this.weight = weight;
    }

    @Override
    public String getName() {
        return "PushRule";
    }

    @Override
    public boolean applies(SenseData data) {
        return data.getDistance() < 5 && "RED".equalsIgnoreCase(data.getColor());
    }

    @Override
    public BiomaCommand getCommand() {
        return BiomaCommand.PUSH;
    }

    @Override
    public int getWeight(SenseData sense) {
        return weight;
    }
}

