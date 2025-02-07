package com.example.bookrentalsystem.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookRentalDto {

    private Long rentalId;
    private String bookTitle;
    private String borrowerEmail;

    private int borrowedQuantity;

    private LocalDate rentDate;
    private LocalDate dueDate;
    private int overdueDays;
}
