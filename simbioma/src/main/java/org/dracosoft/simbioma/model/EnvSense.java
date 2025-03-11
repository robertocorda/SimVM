package org.dracosoft.simbioma.model;

public class EnvSense {
    String name;
    int value;
    long timestamp;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public EnvSense(String name, int value, long timestamp) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }
}
