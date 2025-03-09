package org.dracosoft.weightedrulepl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarpoleTest {

    @Test
    public void testParseOk() {
        // Test per un programma DSL con una singola regola
        String input = """
                if applies { 
                    see distance < 5 and color is RED and speed > 2 
                } do { 
                    ROTATE 
                } with importance { 
                    100 
                } 
                ;    
                                
                                
                if applies { 
                    see distance < 5 and color is RED and speed > 2 
                } do { 
                    ROTATE 
                } with importance { 
                    100 
                } 
                ;               
                """;
        Warpole warpole = new Warpole(input);
        assertNotNull(warpole);
        assertTrue(warpole.decisionRulesSize() == 2, "Le decision rule sono 2");
        assertTrue(warpole.isLexerOk(), "lexer has to fail");
        assertTrue(warpole.isParsingOk(), "parsing has to fail");

        System.out.println("tokens:" + warpole.tokensAsString());
        System.out.println("tree:" + warpole.treeAsString());

    }

    @Test
    public void testParseError() {
        // Test per un programma DSL con una singola regola
        String input = """
                if applies { 
                    pippo distance < 5 and color is RED and speed > 2 
                } 
                ;                   
                """;
        Warpole warpole = new Warpole(input);
        assertNotNull(warpole);
        assertFalse(warpole.isLexerOk(), "lexer has to fail");
        assertFalse(warpole.isParsingOk(), "parsing has to fail");

        System.out.println("tokens:" + warpole.tokensAsString());
        System.out.println("tree:" + warpole.treeAsString());
        System.out.println("parser error:" + warpole.getParserError());
        System.out.println("lexer error:" + warpole.getLexerError());

    }
}
