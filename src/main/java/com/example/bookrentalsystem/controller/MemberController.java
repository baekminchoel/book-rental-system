package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 아이디 중복 확인 API
    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return memberService.isUsername(username);
    }

    // 이메일 중복 확인 API
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return memberService.isEmail(email);
    }
}
