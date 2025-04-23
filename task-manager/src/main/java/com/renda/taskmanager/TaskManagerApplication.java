package com.renda.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TaskManagerApplication.class, args);
    }

}
