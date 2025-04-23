package com.renda.taskmanager.controller;

import com.renda.taskmanager.service.PrototypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PrototypeDemoController {

    private final PrototypeService prototypeService1;
    private final PrototypeService prototypeService2;

    @Autowired
    public PrototypeDemoController(PrototypeService prototypeService1, PrototypeService prototypeService2) {
        this.prototypeService1 = prototypeService1;
        this.prototypeService2 = prototypeService2;
    }

    public void demonstratePrototypeScope() {
        System.out.println("PrototypeService 1: " + prototypeService1.hashCode());
        System.out.println("PrototypeService 2: " + prototypeService2.hashCode());

        prototypeService1.doSomething();
        prototypeService2.doSomething();
    }

}
