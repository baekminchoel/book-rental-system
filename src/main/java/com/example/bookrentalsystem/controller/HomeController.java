package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.BookRequestDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    private final BookService bookService;

    @GetMapping("/")
    public String home(Model model) {
        log.info("home controller");

        List<BookRequestDto> popularList = bookService.getTop10PopularBooks();

        model.addAttribute("popularBooks", popularList);

        return "home";

    }
}
