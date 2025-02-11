package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.RentState;
import com.example.bookrentalsystem.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>{
    List<Rental> findByRentState(RentState rentState);

    // 특정회원 + 특정상태
    List<Rental> findByMemberIdAndRentState(Long memberId, RentState rentState);

    List<Rental> findByMemberIdAndRentStateIn(Long memberId, List<RentState> states);
}
