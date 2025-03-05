package org.dracosoft.dummyalfa;

import org.dracosoft.simbioma.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DummyBioma implements Bioma {

    private Direction direction = Direction.NORTH;
    private int energy = 10; // esempio
    private DecisionEngine decisionEngine;

    // Mappa di azioni integrate (PUSH, STILL, ROTATE...).
    // Ogni azione riduce energy di 1 tranne STILL.
    private final Map<String, Consumer<DummyBioma>> builtInActions;

    public DummyBioma() {
        this.builtInActions = createBuiltInActions();
    }

    @Override
    public void setBrain(DecisionEngine decisionEngine) {
        this.decisionEngine = decisionEngine;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getEnergy() {
        return energy;
    }


    @Override
    public BiomaIntent senseAndDecide(SenseData input) {
        // Se non ho un cervello, non faccio nulla
        NoIntent nointent = new NoIntent();
        if (decisionEngine == null) {
            return nointent;
        }
        // Se non ho energia, sto fermo (non eseguo nulla)
        if (energy <= 0) {
            return nointent;
        }
        // Chiedo al brain quale azione fare
        BiomaIntent intent = decisionEngine.decideNextAction(this, input);

        performInnerAction(intent);

        return intent;
    }

    /*
    cambia lo stato interno del sistema
     */
    private void performInnerAction(BiomaIntent intent) {

        BiomaCommand command = intent.getCommand();

        if (energy <= 0) {
            return;
        }
        // Cerco l'azione built-in
        Consumer<DummyBioma> action = builtInActions.get(command);
        if (action == null) {
            // Azione sconosciuta
            return;
        }
        // Eseguo
        action.accept(this);
    }

    private Map<String, Consumer<DummyBioma>> createBuiltInActions() {
        Map<String, Consumer<DummyBioma>> actions = new HashMap<>();

        // PUSH: chiede all'env di spostare il bioma, consuma 1 energy
        actions.put("PUSH", (self) -> {
            self.energy--;
        });

        // STILL: non fa nulla, non consuma energy
        actions.put("STILL", (self) -> {
            // nessun decremento di energy
        });

        // ROTATE: ruota di 90° a destra e consuma 1 energy
        actions.put("ROTATE", (self) -> {
            self.direction = rotateRight(self.direction);
            self.energy--;
        });

        return actions;
    }

    private static Direction rotateRight(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
        };
    }
}

