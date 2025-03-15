package org.dracosoft.condition.runtime;

import org.dracosoft.condition.parser.ConditionGrammarBaseVisitor;
import org.dracosoft.condition.parser.ConditionGrammarParser;
import org.dracosoft.condition.parser.ConditionGrammarParser.ExprContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class Evaluator extends ConditionGrammarBaseVisitor<Integer> {

    public Integer visitExpr(ConditionGrammarParser.ExprContext ctx) {

        if (ctx.INT() != null) {
            return Integer.valueOf(ctx.INT().getText());
        }

        int left = visit(ctx.expr(0));
        int opType = ctx.addSub().op.getType();
        int right = visit(ctx.expr(1));
        if (opType == ConditionGrammarParser.ADD) {
            return left + right;
        } else if (opType == ConditionGrammarParser.SUB) {
            return left - right;
        } else {
            throw new UnsupportedOperationException("Unsupported operator: " + opType);
        }
    }



}