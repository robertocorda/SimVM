package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

public class RotateRule extends DecisionRule {
    private final int weight; // es. 80

    public RotateRule(int weight) {
        this.weight = weight;
    }

    @Override
    public String getName() {
        return "RotateRule";
    }

    @Override
    public boolean applies(SenseData data) {
        return data.getDistance() < 5 && data.getSpeed() > 2;
    }

    @Override
    public String getAction() {
        return "ROTATE";
    }

    @Override
    public int getWeight() {
        return weight;
    }
}

