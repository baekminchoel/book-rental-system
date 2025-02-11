package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.dto.BookRequestDto;
import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    //책 등록 기능
    public void saveBook (Book book) {
        bookRepository.save(book);
    }

    //책 조회 기능
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + bookId));
    }

    public List<BookRequestDto> getAllBooks(){
        List<Book> books = bookRepository.findAll();

        // dto 변환
        return books.stream()
                .map(book -> new BookRequestDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublicationDate(),
                        book.getPublisher(),
                        book.getStock(),
                        book.getRecentStock(),
                        book.isAvailable(),
                        book.getOverdueCount()
                ))
                .collect(Collectors.toList());
    }

    //책 삭제 기능
    public void deleteBook(long id){
        bookRepository.deleteById(id);
    }

}
