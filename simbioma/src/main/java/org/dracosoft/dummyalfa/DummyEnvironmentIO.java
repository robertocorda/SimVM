package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;

import java.util.HashMap;
import java.util.Map;

public class DummyEnvironmentIO implements EnvironmentIO {

    // Mappa di posizioni: per ogni Bioma, memorizzo coordinate x,y
    private final Map<Bioma, Point> positions = new HashMap<>();

    public Point position(Bioma bioma) {
        return positions.get(bioma);
    }

    // Classe/record per rappresentare coordinate
    public static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    /**
     * Registra un bioma in posizione iniziale (x, y).
     */
    public void placeBioma(Bioma b, int x, int y) {
        positions.put(b, new Point(x, y));
    }

    @Override
    public SenseData sense(Bioma bioma) {
        // Esempio dummy: restituiamo un oggetto "RED" a distanza 3, velocità 2
        // In un caso più avanzato, potresti guardare i reali "oggetti" intorno
        // e calcolare la distanza rispetto alla pos. del bioma.
        return new SenseData(3, "RED", 2);
    }

    @Override
    public boolean push(Bioma bioma) {
        // recuperiamo la posizione attuale
        Point p = positions.get(bioma);
        if (p == null) {
            log("[DummyEnvIO] push fallito (bioma non posizionato).");
            return false;
        }
        // guardiamo la direzione del bioma
        Bioma.Direction dir = bioma.getDirection();
        switch (dir) {
            case NORTH:
                if (p.y > 0) p.y -= 1;
                else return false;
                break;
            case SOUTH:
                if (p.y < 100) p.y += 1;
                else return false;

                break;
            case EAST:
                if (p.x < 100) p.x += 1;
                else return false;

                break;
            case WEST:
                if (p.x > 0) p.x -= 1;
                else return false;

                break;
        }
        // Aggiorniamo la posizione
        this.moveTo(bioma, p);
        log("[DummyEnvIO] push -> nuova pos = (" + p.x + "," + p.y + ")");
        return true;
    }

    private void moveTo(Bioma bioma, Point p) {
        positions.put(bioma, p);
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Override
    public boolean applyIntent(Bioma bioma, BiomaIntent intent) {

        String action = intent.getAction();

        EnvironmentPhy environmentPhy = EnvironmentPhy.valueOf(action.toUpperCase());
        boolean result = switch (environmentPhy) {
            case PUSH -> {
                yield push(bioma);
            }
            default -> false;
        };
        return result;
    }
}

