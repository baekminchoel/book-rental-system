package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;

    @GetMapping("/admin_home")
    public String adminHome() {
        return "admin/admin_home";
    }

    //책 등록
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        return "redirect:/admin/admin_bookList";
    }

    //책 조회
    @GetMapping("/admin_bookList")
    public String showBookList(Model model){
        model.addAttribute("admin_bookList", bookService.getAllBooks());
        model.addAttribute("books", new Book());
        return "admin/admin_bookList";
    }

    //책 삭제
    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long id){
        bookService.deleteBook(id);
        return "redirect:/admin/admin_bookList";
    }
}
