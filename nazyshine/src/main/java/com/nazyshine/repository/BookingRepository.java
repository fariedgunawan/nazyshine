package com.nazyshine.repository;

import com.nazyshine.model.Booking;
import com.nazyshine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user); // Untuk melihat booking berdasarkan user
    // Anda bisa menambahkan method lain jika diperlukan, misal findByBookingTimeBetween
}