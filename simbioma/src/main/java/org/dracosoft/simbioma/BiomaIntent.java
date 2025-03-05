package org.dracosoft.simbioma;

public class BiomaIntent {

    private final BiomaCommand command; // Es. "PUSH"

    public BiomaIntent(BiomaCommand command) {
        this.command = command;
    }

    public BiomaCommand getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "BiomaIntent{" + "command='" + command + '\'' + '}';
    }

}

