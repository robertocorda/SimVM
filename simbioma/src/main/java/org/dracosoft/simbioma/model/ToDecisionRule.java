package org.dracosoft.simbioma.model;

import java.util.List;

public interface ToDecisionRule {
    List<DecisionRule> parseRules(final String dslProgram);
}
