package com.renda.taskmanager.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

//@Service
public class LifecycleDemoService {

    @PostConstruct
    public void init() {
        System.out.println("LifecycleDemoService Bean Initialized");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("LifecycleDemoService Bean Destroyed");
    }

}
