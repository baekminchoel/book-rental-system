package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Rental;
import com.example.bookrentalsystem.repository.MemberRepository;
import com.example.bookrentalsystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rental")
public class HistoryController {

    private final RentalService rentalService;
    private final MemberRepository memberRepository;

    @GetMapping("/history")
    public String myRentalHistory(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(()-> new IllegalStateException("No member with username=" + username));
        Long memberId = member.getId();

        List<Rental> currentRentals = rentalService.findCurrentRentalsByMember(memberId);

        List<Rental> pastRentals = rentalService.findPastRentalsByMember(memberId);

        model.addAttribute("currentRentals", currentRentals);
        model.addAttribute("pastRentals", pastRentals);

        return "rental/history";
    }

    @PostMapping("/history/return/{rentalId}")
    public String returnBook(@PathVariable Long rentalId){
        rentalService.returnBook(rentalId);
        return "redirect:/rental/history";
    }

}
