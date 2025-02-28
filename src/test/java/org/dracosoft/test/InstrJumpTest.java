package org.dracosoft.test;



import org.dracosoft.SimVM;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

        import java.util.ArrayList;
import java.util.List;

public class InstrJumpTest {

    @Test
    void testUnconditionalJump() {
        // Ipotizziamo una VM che riconosca:
        //  - "JUMP <label>:"
        //  - "LET V1 999" (o altre istruzioni)
        //  - "<label>:" come definizione di etichetta
        // In questo test, "LET V1 999" NON deve essere eseguito
        // perché la riga "JUMP ENDLABEL:" salta direttamente all'etichetta "ENDLABEL:".

        SimVM vm = new SimVM();
        List<String> program = new ArrayList<>();

        // Salta subito a 'ENDLABEL:', saltando la riga successiva.
        program.add("JUMP ENDLABEL");

        // Istruzione che non dovrebbe mai essere eseguita
        program.add("LET V1 999");

        // L’etichetta di destinazione
        program.add("ENDLABEL:");

        // Istruzione successiva, che verrà eseguita
        program.add("LET V2 123");

        vm.loadProgram(program);
        vm.run();  // Eseguiamo fino alla fine

        // Verifichiamo l'effetto:
        // V1 deve rimanere 0 (non modificato)
        // V2 deve essere 123
        assertEquals(0, vm.getVariable(1), "V1 deve restare 0, l'istruzione LET V1 999 è saltata.");
        assertEquals(123, vm.getVariable(2), "V2 deve essere 123, istruzione eseguita dopo il salto.");
    }
}

