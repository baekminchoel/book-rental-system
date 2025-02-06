package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.MemberDto;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final MemberService memberService;

    // 관리자 홈
    @GetMapping("/home")
    public String adminHome() {
        return "admin/home";
    }

    // 회원 목록 조회
    @GetMapping("/memberList")
    public String adminMemberList(@RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) Role role,
                                  Model model) {

        // DTO 리스트 가져오기
        List<MemberDto> members = memberService.searchMembers(username, email, role);

        // 필터값을 목록에 추가
        model.addAttribute("members", members);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("role", role);

        return "admin/memberList";
    }
}
