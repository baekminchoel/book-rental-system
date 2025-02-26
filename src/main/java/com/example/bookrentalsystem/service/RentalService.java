package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.entity.*;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.MemberRepository;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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

        // 연체자 여부 체크
        if (member.getRole() == Role.OVERDUE) {
            throw new IllegalArgumentException("연체로 인해 대여가 제한되었습니다.");
        }

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

    // 도서 반납
    public void returnBook(Long rentalID){
        Rental rental = rentalRepository.findById(rentalID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID: " + rentalID));

        // 관리자 권한 유지
        if (rental.getMember().getRole() == Role.ADMIN) {
            rental.setRentState(RentState.RETURNED);
            rental.setReturnDateTime(LocalDateTime.now());

            // 책 재고 복원
            Book book = rental.getBook();
            book.setRecentStock(book.getRecentStock() + 1);
            if (book.getRecentStock() > 0) {
                book.setAvailable(true);
            }

            bookRepository.save(book);
            rentalRepository.save(rental);
            return;
        }

        // 일반 사용자일 경우
        rental.setRentState(RentState.RETURNED);
        rental.setReturnDateTime(LocalDateTime.now());

        // 책 재고 복원
        Book book = rental.getBook();
        book.setRecentStock(book.getRecentStock() + 1);
        if (book.getRecentStock() > 0){
            book.setAvailable(true);
        }

        bookRepository.save(book);

        // 패널티 점수 차감 (1일에 1점씩 감소)
        Member member = rental.getMember();
        int overdueDays = getOverdueDays(rental.getDueDateTime());
        member.reducePenaltyPoint(overdueDays);

        // 패널티 점수 차감 후 즉시 저장
        memberRepository.save(member);  // 즉시 저장

        boolean isRoleChanged = false;

        // 연체 상태에 따른 역할 업데이트 (일반 사용자만 적용)
        if (member.getPenaltyPoint() <= 0 && member.getRole() != Role.USER) {
            member.setRole(Role.USER);
            isRoleChanged = true;
        }

        rentalRepository.save(rental);

        // 만약 역할이 변경되었으면, 마지막에 한 번만 저장
        if (isRoleChanged) {
            // 로그를 추가해 역할 변경 시점 확인
            System.out.println("Role changed to USER for member: " + member.getUsername());
            memberRepository.save(member);  // 역할 변경 후 한 번만 저장
        }
    }

    // 연체 도서 반납
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

        // 패널티 점수 차감 (1일에 1점씩 감소)
        Member member = rental.getMember();
        int overdueDays = getOverdueDays(rental.getDueDateTime());
        member.reducePenaltyPoint(overdueDays);
        memberRepository.save(member);

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

    /**
     * 패널티 로직
     */
    // 패널티 부과 로직
    private void applyPenalty(Member member, int overdueDays) {
        int penalty = 0;

        for (int i = 0; i <= overdueDays; i++) {
            if (i <= 3) {
                penalty += 1;
            } else {
                penalty += (i - 2);
            }
        }

        member.addPenaltyPoint(penalty);
        memberRepository.save(member);
    }

    // 연체 상태 체크
    @Scheduled(fixedRate = 86400000)
    public void checkOverdue() {
        List<Rental> activeRentals = rentalRepository.findByRentState(RentState.ACTIVE,
                Sort.by(Sort.Direction.ASC, "dueDateTime"));

        for (Rental rental : activeRentals) {

            if (rental.getDueDateTime().isBefore(LocalDateTime.now())) {
                rental.setRentState(RentState.OVERDUE);
                rentalRepository.save(rental);

                // 연체된 사용자에게 패널티 부과
                Member member = rental.getMember();
                int overdueDays = getOverdueDays(rental.getDueDateTime());
                applyPenalty(member, overdueDays);
            }
        }
    }

    public void returnOrClearOverdueBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID: " + rentalId));

        // 연체 상태일 경우 연체 처리
        if (rental.getRentState() == RentState.OVERDUE) {
            clearOverdue(rentalId);
        } else {
            returnBook(rentalId);
        }
    }

    // 현재 로그인한 사용자의 대여 중인 책 목록 조회
    public List<BookRentalDto> getCurrentRentalList(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No member with username=" + username));

        // 정렬 기준을 추가 (예: 대여 날짜 기준으로 내림차순 정렬)
        Sort sort = Sort.by(Sort.Direction.DESC, "rentDateTime");

        // 정렬된 대여 목록 조회
        List<Rental> rentals = rentalRepository.findByMemberAndRentState(member, RentState.ACTIVE, sort);

        // Rental 객체들을 BookRentalDto로 변환하여 반환
        return convertToDtoList(rentals);
    }

    // 현재 로그인한 사용자의 과거 대여 목록 조회
    public List<BookRentalDto> getPastRentals(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No member with username=" + username));

        // 정렬 기준을 추가 (예: 반납 날짜 기준으로 내림차순 정렬)
        Sort sort = Sort.by(Sort.Direction.DESC, "returnDateTime");

        // 정렬된 과거 대여 목록 조회
        List<Rental> rentals = rentalRepository.findByMemberAndRentState(member, RentState.RETURNED, sort);

        // Rental 객체들을 BookRentalDto로 변환하여 반환
        return convertToDtoList(rentals);

    }
}
