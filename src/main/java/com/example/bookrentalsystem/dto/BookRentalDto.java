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
    private String bookTitle;
    private String bookAuthor;
    private LocalDate publicationDate;
    private String publisher;
    private String borrower;

    private LocalDateTime rentDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime returnDateTime;

    private int overdueDays;
}
