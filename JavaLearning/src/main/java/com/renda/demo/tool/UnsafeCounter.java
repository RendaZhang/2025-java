package com.renda.demo.tool;

public class UnsafeCounter implements CounterInterface {

    int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
