package org.dracosoft.simbioma;

import static org.dracosoft.simbioma.BiomaCommand.*;

public class DefaultRule extends DecisionRule {

    private BiomaCommand defaultCommand;

    public DefaultRule(String defaultCommandName) {
        this.defaultCommand = BiomaCommand.valueOf(defaultCommandName);
    }

    public DefaultRule(BiomaCommand defaultCommand) {
        this.defaultCommand = STILL;
    }

    @Override
    public String getName() {
        return "DefaultRule("+ defaultCommand +")";
    }

    @Override
    public boolean applies(SenseData data) {
        return true;
    }

    @Override
    public BiomaCommand getCommand() {
        return defaultCommand;
    }

    @Override
    public int getWeight() {
        return 100;
    }
}
