package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.Book;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

    // "title"로 Book 조회
    Optional<Book> findByTitle(String title);

    // "author"로 Book 조회
    Optional<Book> findByAuthor(String author);

    // "publisher"로 Book 조회
    Optional<Book> findByPublisher(String publisher);

    Optional<Book> findById(Long id);

    //대여횟수가 가장 많은 10권의 도서 조회
    List<Book> findTop10ByOrderByRentedCountDesc();



}
