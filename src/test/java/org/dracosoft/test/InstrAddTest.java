package org.dracosoft.test;


import org.dracosoft.SimVM;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrAddTest {

    /**
     * Verifica che ADD con un valore letterale funzioni:
     * (V2=10), poi ADD V1 V2 1 => V1=10+1=11
     */
    @Test
    void testAddV1V2Literal() {
        SimVM vm = new SimVM();

        // Programma di test
        List<String> program = new ArrayList<>();
        program.add("LET V2 10");          // V2=10
        program.add("ADD V1 V2 1");    // V1= (V2 + 1) => 11

        vm.loadProgram(program);
        vm.run();

        // Controlli
        assertEquals(11, vm.getVariable(1), "V1 dovrebbe essere 11");
        assertEquals(10, vm.getVariable(2), "V2 dovrebbe restare 10");
    }

    /**
     * Verifica che ADD con due variabili funzioni:
     * (V2=3, V3=5), poi ADD V1 V2 V3 => V1=3+5=8
     */
    @Test
    void testAddV1V2V3() {
        SimVM vm = new SimVM();

        // Programma di test
        List<String> program = new ArrayList<>();
        program.add("LET V2 3");           // V2=3
        program.add("LET V3 5");           // V3=5
        program.add("ADD V1 V2 V3");   // V1= (3 + 5) => 8

        vm.loadProgram(program);
        vm.run();

        // Controlli
        assertEquals(8, vm.getVariable(1), "V1 dovrebbe essere 8");
        assertEquals(3, vm.getVariable(2), "V2 dovrebbe restare 3");
        assertEquals(5, vm.getVariable(3), "V3 dovrebbe restare 5");
    }
}

