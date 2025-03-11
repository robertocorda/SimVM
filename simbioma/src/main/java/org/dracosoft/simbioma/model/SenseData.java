package org.dracosoft.simbioma.model;

public class SenseData {
    private final int distance;
    private final String color;
    private final int speed;

    public SenseData(int distance, String color, int speed) {
        this.distance = distance;
        this.color = color;
        this.speed = speed;
    }

    public int getDistance() {
        return distance;
    }

    public String getColor() {
        return color;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "SenseData{" +
                "distance=" + distance +
                ", color='" + color + '\'' +
                ", speed=" + speed +
                '}';
    }
}

