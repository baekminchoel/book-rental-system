package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.SignupRequestDto;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 페이지
    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }

    // 회원가입 페이지
    @GetMapping("/member/signup")
    public String signupForm() {
        return "member/signup";
    }

    // 회원가입 처리
    @PostMapping("/member/register")
    @Transactional
    public String register(@ModelAttribute SignupRequestDto request, Model model) {

        // 아이디 중복 검사
        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "member/signup";
        }

        // 이메일 중복 검사
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "member/signup";
        }

        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "member/signup";
        }

        // 비밀번호 유효성 검사 (최소 8자, 영문+숫자 포함)
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            model.addAttribute("error", "비밀번호는 최소 8자 이상이며, 숫자와 영문을 포함해야 합니다.");
            return "/member/signup";
        }

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Member 엔티티 생성
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(encodedPassword);
        member.setEmail(request.getEmail());
        member.setRole(Role.USER);

        memberRepository.save(member);

        return "redirect:/member/login";
    }


}
