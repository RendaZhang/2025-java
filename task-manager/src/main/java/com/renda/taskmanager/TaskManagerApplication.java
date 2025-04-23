package com.renda.taskmanager;

import com.renda.taskmanager.controller.PrototypeDemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TaskManagerApplication.class, args);

        // PrototypeDemoController prototypeDemoController = context.getBean(PrototypeDemoController.class);
        // prototypeDemoController.demonstratePrototypeScope();
    }

}
