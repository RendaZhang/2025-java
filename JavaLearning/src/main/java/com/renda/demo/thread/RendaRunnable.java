package com.renda.demo.thread;

public class RendaRunnable implements Runnable {
    private final int taskNumber;

    public RendaRunnable(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " is executing task " + taskNumber);
    }
}
