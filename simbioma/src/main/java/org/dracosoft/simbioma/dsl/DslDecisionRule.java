package org.dracosoft.simbioma.dsl;

/*
  Implementazione base della DecisionRule basata sul DSL.
  Questa classe memorizza:
  - un nome (ad esempio, derivato dalla condizione),
  - una condizione (come Predicate<SenseData>),
  - un comando (BiomaCommand),
  - una funzione di calcolo del peso (ToIntFunction<SenseData>).
  Poiché il peso deve essere calcolato in base ai dati sensoriali, la funzione
  di calcolo restituisce un intero che verrà poi normalizzato in [1,100].
 */
import org.dracosoft.simbioma.BiomaCommand;
import org.dracosoft.simbioma.DecisionRule;
import org.dracosoft.simbioma.SenseData;


import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class DslDecisionRule extends DecisionRule {

    private final String name;
    private final Predicate<SenseData> condition;
    private final BiomaCommand command;
    private final ToIntFunction<SenseData> weightFunction;

    // Per l'interfaccia, getWeight() senza parametro non può calcolare in base al sense,
    // perciò definiamo anche un metodo computeWeight(data)
    public DslDecisionRule(String name, Predicate<SenseData> condition,
                           BiomaCommand command, ToIntFunction<SenseData> weightFunction) {
        this.name = name;
        this.condition = condition;
        this.command = command;
        this.weightFunction = weightFunction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean applies(SenseData data) {
        return condition.test(data);
    }

    @Override
    public BiomaCommand getCommand() {
        return command;
    }

    /**
     * Calcola il peso in base ai dati sensoriali e lo normalizza tra 1 e 100.
     */
    private int computeWeight(SenseData data) {
        int raw = weightFunction.applyAsInt(data);
        // Normalizzazione: se raw < 1, restituisce 1; se raw > 100, restituisce 100
        if (raw < 1) {
            return 1;
        } else return Math.min(raw, 100);
    }

    @Override
    public int getWeight(SenseData sense) {
        return computeWeight(sense);
    }

}
