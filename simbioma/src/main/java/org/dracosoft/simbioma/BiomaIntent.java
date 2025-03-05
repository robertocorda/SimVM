package org.dracosoft.simbioma;

public class BiomaIntent {

    private final String command; // Es. "PUSH"

    public BiomaIntent(String command) {
        this.command = command;
    }

    public String getAction() {
        return command;
    }

    @Override
    public String toString() {
        return "BiomaIntent{" + "command='" + command + '\'' + '}';
    }

}

