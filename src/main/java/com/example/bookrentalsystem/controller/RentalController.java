package com.example.bookrentalsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    @GetMapping("/rentalBook")
    public String rentalBook() {
        return "/rental/rentalBook";
    }

    @GetMapping("/returnBook")
    public String returnBook() {
        return "/rental/returnBook";
    }

}
