package com.example.bookrentalsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/admin_home")
    public String adminHome() {
        return "admin/admin_home";
    }
}
