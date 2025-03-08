package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.BiomaIntent;
import org.dracosoft.simbioma.SenseData;

public class MainDummyDemo {
    public static void main(String[] args) {
        // Creiamo l'ambiente
        DummyEnvironmentIO environment = new DummyEnvironmentIO();

        // Creiamo un bioma
        DummyBioma bioma = new DummyBioma("test");
        // Impostiamo il cervello del bioma
        bioma.setBrain(new DummyFixedDecisionEngine());

        // Registriamo il bioma nell'ambiente: l'ambiente sa dove si trova
        // (es. posizione iniziale (0, 0))
        environment.placeBioma(bioma, 10, 10);

        // Ciclo di aggiornamento: l'ambiente gestisce il ciclo di Sense-Compute-Action
        for (int tick = 1; tick <= 5; tick++) {
            System.out.println("=== TICK " + tick + " ===");
            // 1. L'ambiente rileva i dati sensoriali per il bioma
            SenseData senseData = environment.sense(bioma);
            environment.log("SenseData: " + senseData);

            // 2. Il bioma, in base ai dati in ingresso, decide l'intenzione
            BiomaIntent intent = bioma.senseAndDecide(senseData);
            environment.log("BiomaIntent: " + intent);

            // 3. L'ambiente applica l'intenzione, aggiornando lo stato del mondo
            boolean applied = environment.applyIntent(bioma, intent);
            environment.log("Azione applicata: " + applied);
            environment.log("Posizione: " + environment.position(bioma));

            // (Opzionale) l'ambiente può anche aggiornare altri stati (es. energia residua, collisioni, ecc.)
        }
    }


}
