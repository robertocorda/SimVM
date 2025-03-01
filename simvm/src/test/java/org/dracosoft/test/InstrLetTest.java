package org.dracosoft.test;


import org.dracosoft.SimVM;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrLetTest {

    /**
     * Testa due istruzioni di assegnazione:
     * - V1 V2  (copiando il valore di V2 in V1)
     * - V1 1   (assegna il valore intero "1" a V1)
     */
    @Test
    void testVariableAssignment() {
        SimVM vm = new SimVM();

        // Programma di test
        List<String> program = new ArrayList<>();
        // Assegniamo 10 a V2
        program.add("LET V2 10");
        // Copiamo V2 in V1 => V1 = (valore di V2) = 10
        program.add("LET V1 V2");
        // Assegniamo 1 a V1 => sovrascrivendo il valore precedente
        program.add("LET V1 1");

        vm.loadProgram(program);
        vm.run();

        // Al termine, V1 dovrebbe valere 1 (l'ultima istruzione lo forza a 1)
        // e V2 dovrebbe restare 10
        assertEquals(1, vm.getVariable(1), "V1 dovrebbe essere 1");
        assertEquals(10, vm.getVariable(2), "V2 dovrebbe essere 10");
    }
}
