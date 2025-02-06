package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.MemberDto;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // 필터링 검색 로직
    public List<MemberDto> searchMembers(String username, String email, Role role) {

        List<Member> members;

        if (username != null && !username.isEmpty()) {
            members = memberRepository.findByUsernameContaining(username);
        } else if (email != null && !email.isEmpty()) {
            members = memberRepository.findByEmailContaining(email);
        } else if (role != null) {
            members = memberRepository.findByRole(role);
        } else {
            members = memberRepository.findAll();
        }

        // Convert Member entities to MemberDto using builder pattern
        return members.stream()
                .map(member -> MemberDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .email(member.getEmail())
                        .role(member.getRole())
                        .penaltyPoint(member.getPenaltyPoint())
                        .penaltyReleaseDate(member.getPenaltyReleaseDate())
                        .build())
                .collect(Collectors.toList());
    }
}
