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
    private String borrower;

    private int borrowedQuantity;

    private LocalDateTime rentDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime returnDateTime;

    private int overdueDays;
}
