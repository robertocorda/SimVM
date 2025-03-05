package org.dracosoft.simbioma;

public abstract class DecisionRule {

    public abstract String getName();

    /**
     * Verifica se la regola si applica in base ai dati sensoriali.
     */
    public abstract boolean applies(SenseData data);

    /**
     * Restituisce l'azione associata a questa regola.
     */
    public abstract String getAction();

    /**
     * Restituisce il peso (urgenza) della regola, un valore da 1 a 100.
     */
    public abstract int getWeight();

    public String toString() {
        return "(Rule:" + getName() +
                " Action:" + getAction() +
                " Weight:" + getWeight() + ")";
    }


}
