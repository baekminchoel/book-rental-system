package com.example.bookrentalsystem.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;

    private int stock;  // 재고
    private int rentedCount;    // 총 빌린 수량

    private boolean available;  // 대출 가능 여부

    private int overdueCount;   // 현재 연체된 도서 수
}
