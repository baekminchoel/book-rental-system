package com.example.bookrentalsystem.repository;

import com.example.bookrentalsystem.entity.RentState;
import com.example.bookrentalsystem.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>{
    List<Rental> findByRentState(RentState rentState);

    List<Rental> findByMemberUsername(String username);

    List<Rental> findByMemberUsernameAndRentState(String username, RentState rentState);
}
