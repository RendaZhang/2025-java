package com.renda.demo;

import com.renda.demo.thread.RendaRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolMain {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 10; i++) {
                executor.submit(new RendaRunnable(i));
            }
            executor.shutdown();
        }
    }
}



