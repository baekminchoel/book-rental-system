package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.MemberDto;
import com.example.bookrentalsystem.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public String memberProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/member/login";
        }

        MemberDto memberDto = memberService.getMemberProfile(userDetails.getUsername());

        model.addAttribute("member", memberDto);
        return "member/profile";
    }
}
