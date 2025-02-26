package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // "username"으로 Member 조회(아이디 중복 방지)
    Optional<Member> findByUsername(String username);

    // "Email"로 Member 조회(이메일 중복 방지)
    Optional<Member> findByEmail(String email);

    // "Role"로 Member 조회
    List<Member> findByRole(Role role);

    // 관리자가 회원 검색 시 리스트로 띄우기
    List<Member> findByUsernameContaining(String username);

    List<Member> findByEmailContaining(String email);

}
