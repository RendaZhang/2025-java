package com.renda.demo.thread;

import java.util.concurrent.Callable;

public class RendaCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return 12345;
    }
}
