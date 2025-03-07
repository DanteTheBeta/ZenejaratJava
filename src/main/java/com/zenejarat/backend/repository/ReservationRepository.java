package com.zenejarat.backend.repository;

import com.zenejarat.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Egyedi lekérdezéseket itt definiálhatsz, ha szükséges
}
