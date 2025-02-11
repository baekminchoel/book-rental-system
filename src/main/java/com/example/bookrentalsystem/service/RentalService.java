package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.RentState;
import com.example.bookrentalsystem.entity.Rental;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;

    // 현재 대여 책 리스트 가져오기
    public List<BookRentalDto> getCurrentRentalList(){
        List<Rental> activeRentals = rentalRepository.findByRentState(RentState.ACTIVE);
        List<BookRentalDto> dtoList = new ArrayList<>();

        for(Rental rental : activeRentals){
            String bookTitle = rental.getBook().getTitle();
            String borrower = rental.getMember().getUsername();
            String bookAuthor = rental.getBook().getAuthor();
            String publisher = rental.getBook().getPublisher();

            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(bookTitle)
                    .bookAuthor(bookAuthor)
                    .publicationDate(rental.getBook().getPublicationDate())
                    .publisher(publisher)
                    .borrower(borrower)
                    .rentDateTime(rental.getRentDateTime())
                    .dueDateTime(rental.getDueDateTime())
                    .build();

            dtoList.add(dto);
        }
        return dtoList;
    }

    // 연체 목록 가져오기
    public List<BookRentalDto> getOverdueList(){
        List<Rental> overdueRentals = rentalRepository.findByRentState(RentState.OVERDUE);
        List<BookRentalDto> dtoList = new ArrayList<>();

        for (Rental rental : overdueRentals){
            String bookTitle = rental.getBook().getTitle();
            String borrower = rental.getMember().getUsername();
            String bookAuthor = rental.getBook().getAuthor();
            String publisher = rental.getBook().getPublisher();

            LocalDateTime due = rental.getDueDateTime();
            int overdueDays = 0;

            if (due != null && due.isBefore(LocalDateTime.now())){
                overdueDays = (int) ChronoUnit.DAYS.between(due, LocalDateTime.now());
            }

            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(bookTitle)
                    .bookAuthor(bookAuthor)
                    .publicationDate(rental.getBook().getPublicationDate())
                    .publisher(publisher)
                    .borrower(borrower)
                    .rentDateTime(rental.getRentDateTime())
                    .overdueDays(overdueDays)
                    .dueDateTime(rental.getDueDateTime())
                    .build();

            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<BookRentalDto> getPastRentals(){
        List<Rental> returnedRentals = rentalRepository.findByRentState(RentState.RETURNED);

        List<BookRentalDto> dtoList = new ArrayList<>();
        for(Rental rental: returnedRentals){
            String bookTitle = rental.getBook().getTitle();
            String borrower = rental.getMember().getUsername();
            String bookAuthor = rental.getBook().getAuthor();
            String publisher = rental.getBook().getPublisher();

            LocalDateTime rentDateTime = rental.getRentDateTime();
            LocalDateTime returnDateTime = rental.getReturnDateTime();

            LocalDateTime due = rental.getDueDateTime();
            int overdueDays = 0;

            if (due != null && due.isBefore(LocalDateTime.now())){
                overdueDays = (int) ChronoUnit.DAYS.between(due, LocalDateTime.now());
            }


            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(bookTitle)
                    .bookAuthor(bookAuthor)
                    .publicationDate(rental.getBook().getPublicationDate())
                    .publisher(publisher)
                    .borrower(borrower)
                    .rentDateTime(rentDateTime)
                    .returnDateTime(returnDateTime)
                    .overdueDays(overdueDays)
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<Rental> findCurrentRentalsByMember(Long memberId){
        return rentalRepository.findByMemberIdAndRentStateIn(memberId,
                List.of(RentState.ACTIVE, RentState.OVERDUE)
        );
    }

    public  List<Rental> findPastRentalsByMember(Long memberId){
        return rentalRepository.findByMemberIdAndRentState(memberId, RentState.RETURNED);
    }

    // 도서 강제 반납
    public void returnBook(Long rentalID){
        Rental rental = rentalRepository.findById(rentalID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID: " + rentalID));

        rental.setRentState(RentState.RETURNED);
        rental.setReturnDateTime(LocalDateTime.now());

        //책 채고 복원
        Book book = rental.getBook();
        book.setStock(book.getStock() + 1);
        if(book.getStock() > 0){
            book.setAvailable(true);
        }
        bookRepository.save(book);

        rentalRepository.save(rental);
    }

    // 연체 도서 강제 반납
    public void clearOverdue(Long rentalId){
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental Id: "+ rentalId));

        rental.setRentState(RentState.RETURNED);
        rental.setReturnDateTime(LocalDateTime.now());

        rentalRepository.save(rental);
    }

}
