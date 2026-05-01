package com.example.demo.modules.registration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/student")
    public String studentDashboard() {
        return "student";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }
}
