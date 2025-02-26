package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.dto.BookRequestDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.Member;
import com.example.bookrentalsystem.entity.Rental;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.service.BookService;
import com.example.bookrentalsystem.service.MemberService;
import com.example.bookrentalsystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;
    private final BookService bookService;
    private final MemberService memberService;

    @GetMapping("/rentalBook")
    public String rentBook(Model model, Principal principal) {

        // 현재 로그인한 사용자 이름
        String username = principal.getName();
        model.addAttribute("username", username);

        // 사용자 대여 중인 도서 목록 확인
        List<Long> rentalIds = rentalService.getRentedBookIdsByUsername(username);
        model.addAttribute("rentalIds", rentalIds);

        // 모든 도서 목록 가져오기
        List<BookRequestDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        // 반납 예정일 (현재 날짜 + 7일)
        LocalDate returnDate = LocalDate.now().plusDays(7);
        model.addAttribute("returnDate", returnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        return "/rental/rentalBook";
    }

    @PostMapping("/rent")
    public String rentBook(@RequestParam Long bookId,
                           @RequestParam String username,
                           RedirectAttributes redirectAttributes) {

        // 이미 대여 중인 도서인지 확인
        List<Long> rentalIds = rentalService.getRentedBookIdsByUsername(username);

        if (rentalIds.contains(bookId)) {
            redirectAttributes.addFlashAttribute("message", "이미 대여 중인 책입니다.");
            return "redirect:/rental/rentalBook";
        }

        Book book = bookService.getBookById(bookId);

        if (book != null && book.isAvailable()) {

            // 대여 전 연체 체크 및 패널티 적용
            rentalService.checkOverdue();

            // 연체자 여부 체크
            Member member = memberService.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("No member with username=" + username));

            if (member.getRole() == Role.OVERDUE) {
                redirectAttributes.addFlashAttribute("message", "연체로 인해 대여가 제한되었습니다.");
                return "redirect:/rental/rentalBook";
            }


            // 대여 처리
            rentalService.rentalBook(bookId, username);
            redirectAttributes.addFlashAttribute("message", "대여 완료되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "대여 실패했습니다.");
        }

        return "redirect:/rental/rentalBook";
    }

    @GetMapping("/history")
    public String rentalHistory(Model model, Principal principal) {

        // 현재 로그인한 사용자 이름
        String username = principal.getName();

        Member member = memberService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No member with username=" + username));
        Long memberId = member.getId();

        List<BookRentalDto> currentRentals = rentalService.getCurrentRentalList(username);
        List<BookRentalDto> pastRentals = rentalService.getPastRentals(username);

        model.addAttribute("currentRentals", currentRentals);
        model.addAttribute("pastRentals", pastRentals);

        return "rental/history";
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            // 연체 여부 체크 및 반납 처리
            rentalService.returnOrClearOverdueBook(id);

            redirectAttributes.addFlashAttribute("message", "책이 성공적으로 반납되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "책 반납에 실패하였습니다.");
        }

        return "redirect:/rental/history";
    }
}
