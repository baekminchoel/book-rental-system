package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.RentState;
import com.example.bookrentalsystem.entity.Rental;
import com.example.bookrentalsystem.repository.MemberRepository;
import com.example.bookrentalsystem.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;

    public List<BookRentalDto> getCurrentRentalList(){
        List<Rental> activeRentals = rentalRepository.findByRentState(RentState.ACTIVE);
        List<BookRentalDto> dtoList = new ArrayList<>();

        for(Rental rental : activeRentals){
            String bookTitle = rental.getBook().getTitle();
            String borrowerEmail = rental.getMember().getEmail();

            int borrowedQuantity = 1;

            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(bookTitle)
                    .borrowerEmail(borrowerEmail)
                    .borrowedQuantity(borrowedQuantity)
                    .rentDate(rental.getRentDate())
                    .dueDate(rental.getDueDate())
                    .build();

            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<BookRentalDto> getOverdueList(){
        List<Rental> overdueRentals = rentalRepository.findByRentState(RentState.OVERDUE);
        List<BookRentalDto> dtoList = new ArrayList<>();

        for (Rental rental : overdueRentals){
            String bookTitle = rental.getBook().getTitle();
            String borrowerEmail = rental.getMember().getEmail();

            LocalDate due = rental.getDueDate();
            long daysBetween = 0;
            if (due != null){
                daysBetween = ChronoUnit.DAYS.between(due, LocalDate.now());
            }
            int overdueDays = (int)Math.max(daysBetween, 0);

            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(bookTitle)
                    .borrowerEmail(borrowerEmail)
                    .rentDate(rental.getRentDate())
                    .overdueDays(overdueDays)
                    .dueDate(rental.getDueDate())
                    .build();

            dtoList.add(dto);
        }
        return dtoList;
    }

    public void returnBook(Long rentalID){
        Rental rental = rentalRepository.findById(rentalID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID: " + rentalID));

        rental.setRentState(RentState.RETURNED);
        rental.setReturnDate(LocalDate.now());

        rentalRepository.save(rental);
    }

    public void clearOverdue(Long rentalId){
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental Id: "+ rentalId));

        rental.setRentState(RentState.RETURNED);
        rentalRepository.save(rental);
    }
}
