package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.MemberDto;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
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

    // 현재 로그인한 회원 정보 조회
    public MemberDto getMemberProfile(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() ->new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .role(member.getRole())
                .penaltyPoint(member.getPenaltyPoint())
                .penaltyReleaseDate(member.getPenaltyReleaseDate())
                .build();
    }
}
