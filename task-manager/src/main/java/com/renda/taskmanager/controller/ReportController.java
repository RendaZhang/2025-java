package com.renda.taskmanager.controller;

import com.renda.taskmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ReportController {

    @Autowired
    private NotificationService notificationService;

}
