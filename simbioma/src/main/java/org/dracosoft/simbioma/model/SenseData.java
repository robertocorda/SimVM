package org.dracosoft.simbioma.model;

import java.util.*;

public class SenseData {
    private Map<String, EnvSense> senseMap;

    public SenseData() {
        senseMap = new HashMap<>();
    }

    public void put(String name, int value) {
        long nowTime = new Date().getTime();
        EnvSense sense = new EnvSense(name, value, nowTime);
        senseMap.put(name, sense);
    }

    public int getSenseValue(String name) {
        EnvSense sense = senseMap.get(name);

        if (sense == null) {
            return 0;
        } else {
            return sense.getValue();
        }
    }

    @Override
    public String toString() {
        return "SenseData{" +
                senseMap +
                '}';
    }
}

