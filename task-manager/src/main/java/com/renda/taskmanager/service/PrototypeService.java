package com.renda.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

//@Service
@Scope("prototype")
public class PrototypeService {

    public PrototypeService() {
        System.out.println("PrototypeService instance created: " + this.hashCode());
    }

    public void doSomething() {
        System.out.println("PrototypeService instance doing something: " + this.hashCode());
    }

}
