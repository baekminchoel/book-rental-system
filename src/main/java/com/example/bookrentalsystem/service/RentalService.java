package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.RentState;
import com.example.bookrentalsystem.entity.Rental;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.MemberRepository;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    // 도서 대여 기능
    public void rentalBook(Long bookId, String username) {

        // 이미 대여 중인 책 여부 체크
        if (isBookAlreadyRented(bookId, username)) {
            throw new IllegalArgumentException("이미 대여 중인 책입니다.");
        }

        // 엔티티 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + bookId));
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username: " + username));

        // 재고 확인
        if (book.getRecentStock() <= 0) {
            throw new IllegalArgumentException("No stock available for this book.");
        }

        // 대여 상태 업데이트
        Rental rental = Rental.builder()
                .book(book)
                .member(member)
                .rentDateTime(LocalDateTime.now())
                .dueDateTime(LocalDateTime.now().plusWeeks(1))
                .rentState(RentState.ACTIVE)
                .build();

        rentalRepository.save(rental);

        // 재고 감소
        book.setRecentStock(book.getRecentStock() - 1);

        // 총 빌린 수량 증가(추천 목록)
        book.setRentedCount(book.getRentedCount() + 1);

        bookRepository.save(book);
    }

    // 현재 대여 책 리스트 가져오기
    public List<BookRentalDto> getCurrentRentalList(){
        List<Rental> activeRentals = rentalRepository.findByRentState(RentState.ACTIVE,
                Sort.by(Sort.Direction.DESC,"rentDateTime"));

        return convertToDtoList(activeRentals);
    }

    // 연체 목록 가져오기
    public List<BookRentalDto> getOverdueList(){
        List<Rental> overdueRentals = rentalRepository.findByRentState(RentState.OVERDUE,
                Sort.by(Sort.Direction.DESC, "dueDateTime"));

        return convertToDtoList(overdueRentals);
    }

    // 과거 대여 목록 가져오기
    public List<BookRentalDto> getPastRentals(){
        List<Rental> returnedRentals = rentalRepository.findByRentState(RentState.RETURNED,
                Sort.by(Sort.Direction.DESC, "returnDateTime"));

        return convertToDtoList(returnedRentals);
    }

    private List<BookRentalDto> convertToDtoList(List<Rental> rentals) {
        List<BookRentalDto> dtoList = new ArrayList<>();

        for (Rental rental : rentals) {
            BookRentalDto dto = BookRentalDto.builder()
                    .rentalId(rental.getId())
                    .bookTitle(rental.getBook().getTitle())
                    .bookAuthor(rental.getBook().getAuthor())
                    .publicationDate(rental.getBook().getPublicationDate())
                    .publisher(rental.getBook().getPublisher())
                    .borrower(rental.getMember().getUsername())
                    .rentDateTime(rental.getRentDateTime())
                    .dueDateTime(rental.getDueDateTime())
                    .returnDateTime(rental.getReturnDateTime())
                    .overdueDays(getOverdueDays(rental.getDueDateTime()))
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    private int getOverdueDays(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            return (int) ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
        }

        return 0;
    }

    // 도서 강제 반납
    public void returnBook(Long rentalID){
        Rental rental = rentalRepository.findById(rentalID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID: " + rentalID));

        rental.setRentState(RentState.RETURNED);
        rental.setReturnDateTime(LocalDateTime.now());

        //책 채고 복원
        Book book = rental.getBook();
        book.setRecentStock(book.getRecentStock() + 1);
        if(book.getRecentStock() > 0){
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

        //책 채고 복원
        Book book = rental.getBook();
        book.setRecentStock(book.getRecentStock() + 1);
        if(book.getRecentStock() > 0){
            book.setAvailable(true);
        }

        bookRepository.save(book);

        rentalRepository.save(rental);
    }

    // 이미 대여 중인 책이 있는지 확인
    private boolean isBookAlreadyRented(Long bookId, String username) {
        List<Rental> rentals = rentalRepository.findByMemberUsername(username);

        for (Rental rental : rentals) {
            if (rental.getBook().getId().equals(bookId) && rental.getRentState() == RentState.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    // 사용자가 빌린 도서 목록 가져오기
    public List<Long> getRentedBookIdsByUsername(String username) {
        List<Rental> rentals = rentalRepository.findByMemberUsernameAndRentState(username, RentState.ACTIVE);

        List<Long> rentalIds = new ArrayList<>();

        for (Rental rental : rentals) {
            rentalIds.add(rental.getBook().getId());
        }

        return rentalIds;
    }
}
