package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

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
    public String getAction() {
        return "PUSH";
    }

    @Override
    public int getWeight() {
        return weight;
    }
}

