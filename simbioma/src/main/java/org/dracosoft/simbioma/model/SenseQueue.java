package org.dracosoft.simbioma.model;

import java.util.LinkedList;
import java.util.Queue;

public class SenseQueue {
    private Queue<SenseData> queue;

    public SenseQueue() {
        queue = new LinkedList<>();
    }

    // returns null if void
    public SenseData dequeue() {
        return queue.poll();
    }

    public void enqueue(SenseData senseData) {
        queue.add(senseData);
    }

    @Override
    public String toString() {
        return "FIRST SenseData{" +
                queue.peek() +
                '}';
    }
}

