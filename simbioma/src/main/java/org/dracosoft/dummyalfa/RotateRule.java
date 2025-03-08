package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.BiomaCommand;
import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

import static org.dracosoft.simbioma.BiomaCommand.*;

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
        return data.getDistance() < 5 && "RED".equalsIgnoreCase(data.getColor());
    }

    @Override
    public BiomaCommand getCommand() {
        return ROTATE;
    }

    @Override
    public int getWeight(SenseData sense) {
        return weight;
    }
}

