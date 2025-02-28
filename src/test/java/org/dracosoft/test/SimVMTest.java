package org.dracosoft.test;

import org.dracosoft.SimVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimVMTest {

    private SimVM vm;

    @BeforeEach
    void setup() {
        vm = new SimVM();
    }

    @Test
    void testAssignment() {
        // Programma: V1=10, V2=20
        List<String> program = new ArrayList<>();
        program.add("LET V1 10");
        program.add("LET V2 20");
        vm.loadProgram(program);

        // Eseguiamo tutto
        vm.run();

        // Test finali
        assertEquals(10, vm.getVariable(1));
        assertEquals(20, vm.getVariable(2));
        assertEquals(0, vm.getVariable(3));
    }

    @Test
    void testAddAndSub() {
        // V1=10, V2=3, ADD V3 V1 V2 => 13, SUB V1 V1 5 => 5
        List<String> program = new ArrayList<>();
        program.add("LET V1 10");
        program.add("LET V2 3");
        program.add("ADD V3 V1 V2");  // V3 = 13
        program.add("SUB V1 V1 5");   // V1 = 10 - 5 = 5
        vm.loadProgram(program);

        vm.run();

        assertEquals(5, vm.getVariable(1));
        assertEquals(3, vm.getVariable(2));
        assertEquals(13, vm.getVariable(3));
    }

    @Test
    void testConditionalJump() {
        // Se V1 > 10, salta a DONE:
        // Altrimenti incrementa V1 di 5 e rifai il check
        List<String> program = new ArrayList<>();
        program.add("LET V1 0");
        program.add("LOOP:");
        program.add("IF V1 > 10 JUMP DONE");
        program.add("ADD V1 V1 5");
        program.add("JUMP LOOP");
        program.add("DONE:");
        vm.loadProgram(program);

        vm.run();

        // V1 parte da 0, loop con +5 finché non supera 10
        // 0 -> 5 -> 10 -> 15 e STOP (quando fa 15, l'IF V1>10 è true => salta DONE)
        assertEquals(15, vm.getVariable(1));
    }

    @Test
    void testMoveAndRotate() {
        List<String> program = new ArrayList<>();
        program.add("ROTATE EST"); // direction=EST
        program.add("MOVE");       // x=1,y=0
        program.add("ROTATE SUD"); // direction=SUD
        program.add("MOVE");       // x=1,y=1
        vm.loadProgram(program);

        vm.run();

        assertEquals(1, vm.getX());
        assertEquals(1, vm.getY());
        assertEquals(SimVM.Direction.SUD, vm.getDirection());
    }

    @Test
    void testWait() {
        List<String> program = new ArrayList<>();
        program.add("WAIT 200"); // attende 200ms
        program.add("LET V1 99");
        vm.loadProgram(program);

        long start = System.currentTimeMillis();
        vm.run();
        long end = System.currentTimeMillis();

        // Controllo che V1=99
        assertEquals(99, vm.getVariable(1));

        // Check (grossolano) che siano passati almeno ~200ms
        long elapsed = end - start;
        assertTrue(elapsed >= 180, "Dovrebbe attendere circa 200ms");
    }

    @Test
    void testCallReturn() {
        // Chiama funzione TESTFUNC, che fa: V1=10 e RETURN
        List<String> program = new ArrayList<>();
        program.add("CALL TESTFUNC");
        program.add("LET V2 200");  // istruzione dopo la call
        program.add("TESTFUNC:");
        program.add("LET V1 10");
        program.add("RETURN");
        vm.loadProgram(program);

        vm.run();

        // Al termine, V1=10 (settato dalla funzione), V2=200
        assertEquals(10, vm.getVariable(1));
        assertEquals(200, vm.getVariable(2));
    }

    @Test
    void testFibonacciLikeLoop() {
        // Esempio ridotto: calcolare 3 iterazioni di fib
        // V1=0, V2=1, costruiamo fib(n) in V3 = V1 + V2, poi shift
        List<String> program = new ArrayList<>();
        program.add("LET V1 0");
        program.add("LET V2 1");
        // 1° iterazione
        program.add("ADD V3 V1 V2"); // V3=1
        program.add("LET V1 V2");        // V1=1
        program.add("LET V2 V3");        // V2=1
        // 2° iterazione
        program.add("ADD V3 V1 V2"); // V3=2
        program.add("LET V1 V2");        // V1=1
        program.add("LET V2 V3");        // V2=2
        // 3° iterazione
        program.add("ADD V3 V1 V2"); // V3=3
        program.add("LET V1 V2");        // V1=2
        program.add("LET V2 V3");        // V2=3
        vm.loadProgram(program);

        vm.run();

        // Al termine: V3=3, V1=2, V2=3
        assertEquals(2, vm.getVariable(1));
        assertEquals(3, vm.getVariable(2));
        assertEquals(3, vm.getVariable(3));
    }

    // ... e così via, puoi creare ulteriori test @Test
    // (ad es. test EAT, LOOK, stack di call annidate, label skip, etc.)
}

