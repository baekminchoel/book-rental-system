package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 아이디 중복 확인
    public boolean isUsername(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    // 이메일 중복 확인
    public boolean isEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
