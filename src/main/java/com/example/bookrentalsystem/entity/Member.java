package com.example.bookrentalsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;      // USER, ADMIN, OVERDUE

    private int penaltyPoint;

    private LocalDateTime penaltyReleaseDate;

    // 패널티 점수 추가
    public void addPenaltyPoint(int point) {
        this.penaltyPoint += point;

        if (this.penaltyPoint > 0) {
            this.role = Role.OVERDUE;
        }
    }

    // 반납 후 패널티 점수 차감
    public void reducePenaltyPoint(int point) {
        this.penaltyPoint = Math.max(0, this.penaltyPoint - point);

        if (this.penaltyPoint == 0) {
            this.role = Role.USER;
        }
    }

}
