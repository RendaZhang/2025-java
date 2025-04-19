package com.renda.demo.tool;

public class UnsafeCounter {

    int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
