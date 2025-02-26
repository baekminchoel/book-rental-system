package com.example.bookrentalsystem;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.entity.*;
import com.example.bookrentalsystem.repository.BookRepository;
import com.example.bookrentalsystem.repository.MemberRepository;
import com.example.bookrentalsystem.repository.RentalRepository;
import com.example.bookrentalsystem.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class RentalServiceTest {

    @Autowired
    private RentalService rentalService;

    @MockitoBean
    private RentalRepository rentalRepository;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private BookRepository bookRepository;

    private Book book;
    private Member member;
    private Rental rental;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setPublicationDate(LocalDate.now());
        book.setRecentStock(5);
        book.setAvailable(true);

        member = new Member();
        member.setId(1L);
        member.setUsername("testUser");
        member.setRole(Role.USER);
        member.setPenaltyPoint(0);

        rental = new Rental();
        rental.setId(1L);
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentDateTime(LocalDateTime.now());
        rental.setDueDateTime(LocalDateTime.now().plusWeeks(1));
        rental.setRentState(RentState.ACTIVE);
    }

    @Test
    public void testRentalBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findByUsername("testUser")).thenReturn(Optional.of(member));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        rentalService.rentalBook(1L, "testUser");

        // Then
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        assertEquals(4, book.getRecentStock());
        assertEquals(1, book.getRentedCount());
    }

    @Test
    public void testReturnBook() {
        // Given
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Setting penalty point to 0 to trigger role change
        member.setPenaltyPoint(0);  // Ensure penalty point is 0 to trigger role change

        // When
        rentalService.returnBook(1L);

        // Then
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(memberRepository, times(1)).save(any(Member.class));  // Ensure save is called once
        assertEquals(6, book.getRecentStock());

        // Check if the member's role is updated to USER after return
        assertEquals(Role.USER, member.getRole());
    }

    @Test
    public void testClearOverdue() {
        // Given
        rental.setRentState(RentState.OVERDUE);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        rentalService.clearOverdue(1L);

        // Then
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(memberRepository, times(1)).save(any(Member.class));
        assertEquals(6, book.getRecentStock());
    }

    @Test
    public void testCheckOverdue() {
        // Given
        rental.setDueDateTime(LocalDateTime.now().minusDays(2));
        rental.setRentState(RentState.ACTIVE);
        when(rentalRepository.findByRentState(eq(RentState.ACTIVE), any(Sort.class))).thenReturn(List.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // When
        rentalService.checkOverdue();

        // Then
        verify(rentalRepository, times(1)).save(any(Rental.class));
        assertEquals(RentState.OVERDUE, rental.getRentState());
    }

    @Test
    public void testGetCurrentRentalList() {
        // Given
        when(rentalRepository.findByRentState(eq(RentState.ACTIVE), any(Sort.class))).thenReturn(List.of(rental));

        // When
        List<BookRentalDto> currentRentals = rentalService.getCurrentRentalList();

        // Then
        assertNotNull(currentRentals);
        assertEquals(1, currentRentals.size());
        assertEquals("Test Book", currentRentals.get(0).getBookTitle());
    }

    @Test
    public void testGetOverdueList() {
        // Given
        rental.setRentState(RentState.OVERDUE);
        when(rentalRepository.findByRentState(eq(RentState.OVERDUE), any(Sort.class))).thenReturn(List.of(rental));

        // When
        List<BookRentalDto> overdueRentals = rentalService.getOverdueList();

        // Then
        assertNotNull(overdueRentals);
        assertEquals(1, overdueRentals.size());
        assertEquals("Test Book", overdueRentals.get(0).getBookTitle());
    }
}
