package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementazione grafica di EnvironmentIO.
 */
public class GraphicalEnvironmentIO implements EnvironmentIO {

    // Mappa logica delle posizioni per ogni Bioma.
    private final Map<Bioma, Point> positions = new HashMap<>();

    // Componenti grafici Swing.
    private final JFrame frame;
    private final DrawingPanel panel;

    // Dimensioni per la visualizzazione (ogni cella della griglia)
    private final int gridSize = 50;

    public GraphicalEnvironmentIO() {
        // Creazione del frame e del pannello di disegno.
        frame = new JFrame("Simbioma Environment");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new DrawingPanel();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    /**
     * Posiziona inizialmente il bioma nell'ambiente.
     */
    public void placeBioma(Bioma bioma, int x, int y) {
        positions.put(bioma, new Point(x, y));
        log("Placed Bioma at (" + x + ", " + y + ")");
        repaint();
    }

    private static final Random RANDOM = new Random();
    private static final String[] COLORS = {"RED", "GREEN", "BLUE", "YELLOW", "ORANGE", "PURPLE"};

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
    ritorna valori casuali
     */
    @Override
    public SenseData sense(Bioma bioma) {
        return generateRandomSenseData();
    }

    @Override
    public boolean push(Bioma bioma) {
        Point p = positions.get(bioma);
        if (p == null) {
            log("Bioma not placed. Cannot push.");
            return false;
        }
        // Aggiorna la posizione in base alla direzione del Bioma.
        switch (bioma.getDirection()) {
            case NORTH: p.y -= 1; break;
            case SOUTH: p.y += 1; break;
            case EAST:  p.x += 1; break;
            case WEST:  p.x -= 1; break;
        }
        log("Bioma moved to " + p);
        repaint();
        return true;
    }

    @Override
    public void log(String message) {
        // Per questo esempio logghiamo su console.
        System.out.println("[Environment] " + message);
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

    private void repaint() {
        // Assicura il repaint nel thread Swing.
        SwingUtilities.invokeLater(() -> panel.repaint());
    }

    /**
     * Pannello personalizzato per disegnare la griglia e le posizioni dei Bioma.
     */
    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Disegna una griglia.
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < getWidth(); i += gridSize) {
                g.drawLine(i, 0, i, getHeight());
            }
            for (int j = 0; j < getHeight(); j += gridSize) {
                g.drawLine(0, j, getWidth(), j);
            }
            // Disegna ogni Bioma come un cerchio blu.
            for (Map.Entry<Bioma, Point> entry : positions.entrySet()) {
                Point p = entry.getValue();
                int screenX = p.x * gridSize;
                int screenY = p.y * gridSize;
                g.setColor(Color.BLUE);
                g.fillOval(screenX, screenY, gridSize, gridSize);
            }
        }
    }
}

