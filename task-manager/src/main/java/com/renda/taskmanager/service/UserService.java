package com.renda.taskmanager.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String registerUser(String username) {
        System.out.println("Registering User: " + username);
        return "Registration Successful: " + username;
    }

}
