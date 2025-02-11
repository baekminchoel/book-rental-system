package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.BookRequestDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.service.BookService;
import com.example.bookrentalsystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            rentalService.rentalBook(bookId, username);
            redirectAttributes.addFlashAttribute("message", "대여 완료되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "대여 실패했습니다.");
        }

        return "redirect:/rental/rentalBook";
    }

    @GetMapping("/returnBook")
    public String returnBook() {
        return "/rental/returnBook";
    }

    @GetMapping("/history")
    public String history() {
        return "/rental/history";
    }
}
