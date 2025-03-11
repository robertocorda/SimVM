package org.dracosoft.simbioma.model;

import static org.dracosoft.simbioma.model.EnvConstants.BLUE;
import static org.dracosoft.simbioma.model.EnvConstants.RED;

public class SenseDataFactory {
    //SenseDataFactory.createSenseData(3, "RED", 3); // distance=3, color=RED, speed=3
    public static SenseData createSenseData(int distance, int color, int speed ) {
        SenseData senseData = new SenseData();
        senseData.put("distance", distance);
        senseData.put("color", color);
        senseData.put("speed", speed);
        return senseData;
    }

    public static SenseData createSenseData(int distance, String color, int speed ) {
        return createSenseData(distance, mapConstantToInt(color), speed );
    }

    public static int mapConstantToInt(String valText) {
        int valInt = switch (valText) {
            case "RED" -> RED;
            case "BLUE" -> BLUE;
            default -> 0;
        };
        return valInt;
    }

    public static boolean compareColor(SenseData data, String value) {
        return getColor(data) == mapConstantToInt(value);
    }

    public static int getColor(SenseData data) {
        return data.getSenseValue("color");
    }

    public static int getDistance(SenseData data) {
        return data.getSenseValue("distance");
    }

    public static int getSpeed(SenseData data) {
        return data.getSenseValue("speed");
    }
}
