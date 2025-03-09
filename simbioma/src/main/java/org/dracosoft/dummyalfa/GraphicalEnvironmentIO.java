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
public class GraphicalEnvironmentIO extends EnvironmentIOBase {
    // Componenti grafici Swing.
    private final JFrame frame;
    private final DrawingPanel panel;

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
        super.placeBioma(bioma, x, y);

        log("placeBioma at (" + x + ", " + y + ")");
        repaint();
    }

    @Override
    public boolean push(Bioma bioma) {
        boolean canDo = super.push(bioma);
        repaint();

        return canDo;
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
            for (int i = 0; i < getWidth(); i += getGridSize()) {
                g.drawLine(i, 0, i, getHeight());
            }
            for (int j = 0; j < getHeight(); j += getGridSize()) {
                g.drawLine(0, j, getWidth(), j);
            }
            // Disegna ogni Bioma come un cerchio blu.
            for (Map.Entry<Bioma, Point> entry : positions.entrySet()) {
                Point p = entry.getValue();
                int screenX = p.x * getGridSize();
                int screenY = p.y * getGridSize();
                g.setColor(Color.BLUE);
                g.fillOval(screenX, screenY, getGridSize(), getGridSize());

                Bioma bioma = entry.getKey();
                String name = bioma.getName();

                // Disegna il nome del bioma in bianco al centro del cerchio.
                g.setColor(Color.BLACK);
                int stringX = screenX + (getGridSize() / 2) - (g.getFontMetrics().stringWidth(name) / 2);
                int stringY = screenY + (getGridSize() / 2) + (g.getFontMetrics().getAscent() / 2);
                g.drawString(name, stringX, stringY);
            }
        }
    }

    public void applyAction(DummyBioma bioma, BiomaIntent intent) {
        switch (intent.getCommand()) {
            case PUSH -> this.push(bioma);
            case ROTATE -> bioma.performInnerAction(intent);
        }
    }
}

