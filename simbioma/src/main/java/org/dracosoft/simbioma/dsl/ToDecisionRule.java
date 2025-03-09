package org.dracosoft.simbioma.dsl;

import org.dracosoft.simbioma.DecisionRule;

import java.util.List;

public interface ToDecisionRule {
    List<DecisionRule> parseRules(final String dslProgram);
}
