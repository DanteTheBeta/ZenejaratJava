package com.zenejarat.backend.repository;

import com.zenejarat.backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Itt egyedi lekérdezéseket is definiálhatsz, ha szükséges
}
