package com.renda.taskmanager.exception;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " not found");
    }
}
