package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB에서 username으로 Member 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username " + username));

        // Spring Security의 User 객체 혹은 커스텀 UserDetails 구현체 반환
        return new User(
                member.getUsername(),
                member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
        );
    }

}
