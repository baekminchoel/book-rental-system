package com.example.bookrentalsystem.service;

import com.example.bookrentalsystem.entity.Book;
import com.example.bookrentalsystem.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    //책 등록 기능
    public Book saveBook (Book book) {
        return bookRepository.save(book);
    }

    //책 조회 기능
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    //책 삭제 기능
    public void deleteBook(long id){
        bookRepository.deleteById(id);
    }


}
