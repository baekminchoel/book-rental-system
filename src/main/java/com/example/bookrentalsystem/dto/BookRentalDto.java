package com.example.bookrentalsystem.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookRentalDto {

    private Long rentalId;
    private Long bookId;

    private String bookTitle;
    private String bookAuthor;
    private LocalDate publicationDate;
    private String publisher;
    private String borrower;

    private int stock;  // 전체 수량
    private int recentStock; // 현재 수량

    private LocalDateTime rentDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime returnDateTime;

    private boolean available;
    private int overdueDays;
}
