package com.example.bookrentalsystem.dto;

import com.example.bookrentalsystem.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private int penaltyPoint;
    private LocalDateTime penaltyReleaseDate;
}
