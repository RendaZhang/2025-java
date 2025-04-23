package com.renda.taskmanager.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void registerUser(String username) {
        System.out.println("User successfully registered: " + username);
    }
}
