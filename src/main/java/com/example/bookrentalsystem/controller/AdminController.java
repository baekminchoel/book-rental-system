package com.example.bookrentalsystem.controller;

import com.example.bookrentalsystem.dto.BookRentalDto;
import com.example.bookrentalsystem.dto.BookRequestDto;
import com.example.bookrentalsystem.dto.MemberDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.entity.Role;
import com.example.bookrentalsystem.service.BookService;
import com.example.bookrentalsystem.service.MemberService;
import com.example.bookrentalsystem.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final MemberService memberService;
    private final BookService bookService;
    private final RentalService rentalService;

    // 관리자 홈
    @GetMapping("/home")
    public String adminHome() {
        return "admin/home";
    }

    // 회원 목록 조회
    @GetMapping("/memberList")
    public String adminMemberList(@RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) Role role,
                                  Model model) {

        // DTO 리스트 가져오기
        List<MemberDto> members = memberService.searchMembers(username, email, role);

        // 필터값을 목록에 추가
        model.addAttribute("members", members);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("role", role);

        return "admin/memberList";
    }

    //책 등록
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        return "redirect:/admin/bookList";
    }

    //책 조회
    @GetMapping("/bookList")
    public String showBookList(Model model){
        List<BookRequestDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        return "admin/bookList";
    }

    //책 삭제
    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long id){
        bookService.deleteBook(id);
        return "redirect:/admin/bookList";
    }

    // 대여 조회 관리
    @GetMapping("/rentalList")
    public String rentalList(Model model) {
        List<BookRentalDto> rentalList = rentalService.getCurrentRentalList();
        List<BookRentalDto> overdueList = rentalService.getOverdueList();

        model.addAttribute("rentalList", rentalList);
        model.addAttribute("overdueList", overdueList);

        return "admin/rentalList";
    }

    // 도서 강제 반납(관리자)
    @PostMapping("/rental/{id}/return")
    public String returnBook(@PathVariable("id") Long rentalId){
        rentalService.returnBook(rentalId);
        return "redirect:/admin/rentalList";
    }

    // 연체 도서 강제 반납(관리자)
    @PostMapping("/rental/overdue/{id}/clear")
    public String clearOverdue(@PathVariable("id") Long rentalId){
        rentalService.clearOverdue(rentalId);
        return "redirect:/admin/rentalList";
    }
}
