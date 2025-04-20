package com.renda.demo.tool;

public class SafeCounterWithSync implements CounterInterface {
    private int counter;

    public synchronized void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

}
