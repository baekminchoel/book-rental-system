package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // "username"으로 Member 조회
    Optional<Member> findByUsername(String username);

    // "Email"로 Member 조회
    Optional<Member> findByEmail(String email);

}
