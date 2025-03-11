package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.model.BiomaCommand;
import org.dracosoft.simbioma.model.DecisionRule;
import org.dracosoft.simbioma.model.SenseData;

import static org.dracosoft.simbioma.model.BiomaCommand.*;
import static org.dracosoft.simbioma.model.SenseDataFactory.compareColor;
import static org.dracosoft.simbioma.model.SenseDataFactory.getDistance;

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
        return getDistance(data) < 5 && compareColor(data, "RED");
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

