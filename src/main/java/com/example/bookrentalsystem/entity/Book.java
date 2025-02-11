package com.example.bookrentalsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;

    private int recentStock; // 현재 수량
    private int stock;  // 총 재고

    private int rentedCount;    // 총 빌린 수량(추천 목록)

    private boolean available;  // 대출 가능 여부

    private int overdueCount;   // 현재 연체된 도서 수

    public Book(String title, String author, LocalDate publicationDate, String publisher, int stock) {
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        this.stock = stock;
        this.recentStock = stock; // stock과 동일하게 설정
        this.available = stock > 0;
    }

    // persist 되기 전에 recentStock을 설정하는 방법
    @PrePersist
    public void prePersist() {
        if (this.recentStock == 0) {
            this.recentStock = this.stock; // stock 값으로 recentStock 초기화
        }

        this.available = this.recentStock > 0;
    }
}
