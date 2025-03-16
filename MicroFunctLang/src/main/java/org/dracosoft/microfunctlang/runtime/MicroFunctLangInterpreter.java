package org.dracosoft.microfunctlang.runtime;

import org.dracosoft.microfunctlang.parser.MicroFunctLangBaseVisitor;
import org.dracosoft.microfunctlang.parser.MicroFunctLangParser;

import java.util.ArrayList;
import java.util.List;

public class MicroFunctLangInterpreter extends MicroFunctLangBaseVisitor<Void> {

    @Override
    public Void visitProgram(MicroFunctLangParser.ProgramContext ctx) {
        // program : (functionDefinition)+ EOF

        visitFunctionDefinition(ctx.functionDefinition());

        return null; // base visitor usa T = Void
    }

    public Void visitFunctionDefinition(MicroFunctLangParser.FunctionDefinitionContext ctx) {
        // functionDefinition : typeDecl functionName '(' paramList? ')' '->' expr

        // tipo
        String type = ctx.typeDecl().getText();   // "int" o "bool"

        // nome
        String fname = ctx.functionName().getText();  // es. "funct"

        // paramList
        List<String> params = new ArrayList<>();
        List<MicroFunctLangParser.ParamListContext> plCtxList = ctx.paramList();

        for (MicroFunctLangParser.ParamListContext plCtx : plCtxList) {
            /*
            // each param : typeDecl ID
            String ptype = plCtx.param().typeDecl().getText();
            String pname = plCtx.ID().getText();
            params.add(ptype + " " + pname);

             */
        }


        // expr (corpo)
        MicroFunctLangParser.ExprContext body = ctx.expr(); // radice dell'espressione

        // Stampa o memorizza
        System.out.println("Funzione: " + fname + " (tipo=" + type + ")");
        System.out.println("  Parametri: " + params);

        // Visitiamo l'espressione per stamparne la forma in notazione testuale
        String exprText = visitExpressionToString(body);
        System.out.println("  Corpo -> " + exprText);

        return null;
    }

    // Visita l'expr ricorsivamente, restituendo una stringa
    // (Potresti invece costruire un AST, o calcolare un valore, ecc.)
    private String visitExpressionToString(MicroFunctLangParser.ExprContext ctx) {
        if (ctx instanceof MicroFunctLangParser.MulDivExprContext) {
            MicroFunctLangParser.MulDivExprContext mctx = (MicroFunctLangParser.MulDivExprContext) ctx;
            String left = visitExpressionToString(mctx.expr(0));
            String right = visitExpressionToString(mctx.expr(1));
            String op = mctx.opMulDiv().getText(); // '*' o '/'
            return "(" + left + " " + op + " " + right + ")";
        } else if (ctx instanceof MicroFunctLangParser.AddSubExprContext) {
            MicroFunctLangParser.AddSubExprContext actx = (MicroFunctLangParser.AddSubExprContext) ctx;
            String left = visitExpressionToString(actx.expr(0));
            String right = visitExpressionToString(actx.expr(1));
            String op = actx.opAddSub().getText(); // '+' o '-'
            return "(" + left + " " + op + " " + right + ")";
        } else if (ctx instanceof MicroFunctLangParser.LogicExprContext) {
            MicroFunctLangParser.LogicExprContext lctx = (MicroFunctLangParser.LogicExprContext) ctx;
            String left = visitExpressionToString(lctx.expr(0));
            String right = visitExpressionToString(lctx.expr(1));
            String op = lctx.opLogic().getText(); // 'and' o 'or'
            return "(" + left + " " + op + " " + right + ")";
        } else if (ctx instanceof MicroFunctLangParser.NotExprContext) {
            MicroFunctLangParser.NotExprContext notctx = (MicroFunctLangParser.NotExprContext) ctx;
            String sub = visitExpressionToString(notctx.expr());
            return "(not " + sub + ")";
        } else if (ctx instanceof MicroFunctLangParser.ParenExprContext) {
            MicroFunctLangParser.ParenExprContext parctx = (MicroFunctLangParser.ParenExprContext) ctx;
            String inside = visitExpressionToString(parctx.expr());
            return "(" + inside + ")";
        } else if (ctx instanceof MicroFunctLangParser.IntLiteralContext) {
            return ctx.getText(); // il numero
        } else if (ctx instanceof MicroFunctLangParser.TrueLiteralContext) {
            return "true";
        } else if (ctx instanceof MicroFunctLangParser.FalseLiteralContext) {
            return "false";
        } else if (ctx instanceof MicroFunctLangParser.VarRefContext) {
            return ctx.getText(); // l'identificatore
        }

        // fallback
        return ctx.getText();
    }
}
