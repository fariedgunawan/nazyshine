package com.nazyshine.repository;

import com.nazyshine.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // Anda bisa menambahkan method kustom jika diperlukan, contoh:
    // Optional<Service> findByName(String name);
}