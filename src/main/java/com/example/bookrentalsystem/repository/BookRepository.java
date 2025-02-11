package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

    // "title"로 Book 조회
    Optional<Book> findByTitle(String title);

    // "author"로 Book 조회
    Optional<Book> findByAuthor(String author);

    // "publisher"로 Book 조회
    Optional<Book> findByPublisher(String publisher);

}
