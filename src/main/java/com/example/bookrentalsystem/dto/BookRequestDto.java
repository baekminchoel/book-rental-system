package com.example.bookrentalsystem.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class BookRequestDto {
    private Long bookId;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String publisher;
    private int stock;
    private int recentStock;
    private boolean available;
    private int overdueCount;
    private int rentedCount;
}