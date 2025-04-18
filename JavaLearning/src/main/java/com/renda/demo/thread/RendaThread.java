package com.renda.demo.thread;

public class RendaThread extends Thread {
    @Override
    public void run() {
        super.run();
        System.out.println("Thread method running");
    }
}
