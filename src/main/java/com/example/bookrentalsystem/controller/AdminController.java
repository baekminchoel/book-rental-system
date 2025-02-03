package com.example.bookrentalsystem.controller;

import org.springframework.ui.Model;
import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/admin/admin_home")
    public String adminHome() {
        return "admin/admin_home";
    }

    // 대여관리

    private final RentalService rentalService;

    @GetMapping("/admin/admin_rentalList")
    public String admin_rentalList(Model model) {
        List<BookRentalDto> rentalList = rentalService.getCurrentRentalList();
        List<BookRentalDto> overdueList = rentalService.getOverdueList();

        model.addAttribute("rentalList", rentalList);
        model.addAttribute("overdueList", overdueList);

        return "admin/admin_rentalList";
    }

    @PostMapping("/admin/rental/{id}/return")
    public String returnBook(@PathVariable("id") Long rentalId){
        rentalService.returnBook(rentalId);
        return "redirect:/admin/admin_rentalList";
    }

    @PostMapping("/admin/rental/overdue/{id}/clear")
    public String clearOverdue(@PathVariable("id") Long rentalId){
        rentalService.clearOverdue(rentalId);
        return "redirect:/admin/admin_rentalList";
    }
}
