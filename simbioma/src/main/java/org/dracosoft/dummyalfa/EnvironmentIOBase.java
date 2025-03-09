package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public abstract class EnvironmentIOBase implements EnvironmentIO {

    private static final Random RANDOM = new Random();

    // Mappa logica delle posizioni per ogni Bioma.
    protected final Map<Bioma, Point> positions = new HashMap<>();

    public static final String[] COLORS = {"RED", "GREEN", "BLUE", "YELLOW", "ORANGE", "PURPLE"};

    public int getGridSize() {
        return gridSize;
    }

    // Dimensioni per la visualizzazione (ogni cella della griglia)
    private final int gridSize = 10;

    /**
     * Posiziona inizialmente il bioma nell'ambiente.
     */
    public void placeBioma(Bioma bioma, int x, int y) {
        positions.put(bioma, new Point(x, y));
    }

    /**
     * Genera un oggetto SenseData con valori casuali.
     * - La distanza sarà un intero compreso tra 1 e 20.
     * - La velocità sarà un intero compreso tra 1 e 10.
     * - Il colore viene scelto casualmente da un array predefinito.
     *
     * @return un nuovo oggetto SenseData con valori casuali
     */
    public static SenseData generateRandomSenseData() {
        int distance = RANDOM.nextInt(20) + 1;  // valori da 1 a 20
        int speed = RANDOM.nextInt(10) + 1;       // valori da 1 a 10
        String color = COLORS[RANDOM.nextInt(COLORS.length)];
        return new SenseData(distance, color, speed);
    }

    /*
    ritorna valori casuali, simulazione del vero sense
     */
    @Override
    public SenseData sense(Bioma bioma) {
        return generateRandomSenseData();
    }

    @Override
    public boolean push(Bioma bioma) {

        Point p = positions.get(bioma);
        if (p == null) {
            //log("Bioma not placed. Cannot push.");
            return false;
        }

        // Aggiorna la posizione in base alla direzione del Bioma.
        switch (bioma.getDirection()) {
            case NORTH:
                p.y -= 1;
                break;
            case SOUTH:
                p.y += 1;
                break;
            case EAST:
                p.x += 1;
                break;
            case WEST:
                p.x -= 1;
                break;
        }
        //log("Bioma moved to " + p);
        return true;

    }

    @Override
    public void log(String message) {
        // Per questo esempio logghiamo su console.
        System.out.println("[Environment] " + message);
    }

    protected EnvironmentAction mapToEnvAction(BiomaCommand command) {

        switch (command) {
            case PUSH:
                return EnvironmentAction.MOVE;
            case STILL, ROTATE:
                return EnvironmentAction.NOP;
            default:
                throw new IllegalArgumentException("Valore non supportato: " + command);
        }


    }

    @Override
    public boolean applyIntent(Bioma bioma, BiomaIntent intent) {
        BiomaCommand command = intent.getCommand();

        EnvironmentAction environmentAction = mapToEnvAction(command);
        boolean result = switch (environmentAction) {
            case MOVE -> {
                yield push(bioma);
            }
            default -> false;
        };
        return result;
    }

    public abstract void applyAction(DummyBioma bioma, BiomaIntent intent);


}
