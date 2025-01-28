package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.SignupRequestDto;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/register")
    @Transactional
    public String register(@ModelAttribute SignupRequestDto request) {

        // 1. 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Member 엔티티 생성
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(encodedPassword);
        member.setEmail(request.getEmail());
        member.setRole(Role.USER);

        memberRepository.save(member);

        return "redirect:/login";
    }
}
