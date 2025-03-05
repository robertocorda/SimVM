package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;

public class DefaultRule extends DecisionRule {

    private String defaultAction;

    public DefaultRule(String defaultAction) {
        this.defaultAction = defaultAction;
    }

    @Override
    public String getName() {
        return "DefaultRule("+defaultAction+")";
    }

    @Override
    public boolean applies(SenseData data) {
        return true;
    }

    @Override
    public String getAction() {
        return defaultAction;
    }

    @Override
    public int getWeight() {
        return 100;
    }
}
