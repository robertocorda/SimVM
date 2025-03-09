package org.dracosoft.weightedrulepl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlLexer;
import org.dracosoft.weightedrulespl.parser.WeightedRulesPlParser;

import java.util.List;

// Weighted Rule Program Language
public class Warpole {
    private final List<String> parserErrors;
    private final List<String> lexerErrors;

    public boolean isLexerOk() {
        return lexerOk;
    }

    public boolean isParsingOk() {
        return parsingOk;
    }

    private final boolean lexerOk;
    private final boolean parsingOk;
    private CommonTokenStream tokens;
    private ParseTree tree;
    private WeightedRulesPlParser parser;

    public List<WeightedRulesPlParser.DecisionRuleContext> getDecisionRulesContext() {
        return decisionRulesContext;
    }

    // cached
    private List<WeightedRulesPlParser.DecisionRuleContext> decisionRulesContext;
    private String tokensString;

    public Warpole(String program) {
        CharStream charStream = CharStreams.fromString(program);
        CollectingErrorListener errorLexerListener = new CollectingErrorListener("lexer parserErrors");
        CollectingErrorListener errorParserListener = new CollectingErrorListener("parser parserErrors");

        // lexer
        WeightedRulesPlLexer lexer = new WeightedRulesPlLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorLexerListener);

        // parser
        tokens = new CommonTokenStream(lexer);
        parser = new WeightedRulesPlParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorParserListener);

        // it calls parser
        tree = parser.program();
        if (tree == null) {
            throw new RuntimeException("Program could not be parsed");
        }

        // Dopo il parsing, recupera gli errori nel parsing
        parserErrors = errorParserListener.getErrorMessages();
        if (!parserErrors.isEmpty()) {
            parsingOk = false;
        } else {
            // NO error
            parsingOk = true;
        }

        // Dopo il parsing, recupera gli errori nei tokens
        lexerErrors = errorLexerListener.getErrorMessages();
        if (!lexerErrors.isEmpty()) {
            lexerOk = false;
        } else {
            lexerOk = true;
        }

        // tokens list
        tokens.fill();
        StringBuilder tokensBuilder = new StringBuilder();
        for (Token t : tokens.getTokens()) {
            tokensBuilder.append("(T: " + t.getText() + ", N: " + WeightedRulesPlLexer.VOCABULARY.getSymbolicName(t.getType()) + ")");
        }
        tokensString = tokensBuilder.toString();

        // decision rules
        decisionRulesContext = DecisionRuleCollector.findDecisionRules(tree);
    }

    public int decisionRulesSize() {
        return decisionRulesContext.size();
    }

    public String treeAsString() {
        return tree.toStringTree(parser);
    }

    public String tokensAsString() {
        return tokensString;
    }

    public List<String> getLexerError() {
        return lexerErrors;
    }

    public List<String> getParserError() {
        return parserErrors;
    }

}
