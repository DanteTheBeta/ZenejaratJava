package com.zenejarat.backend.repository;

import com.zenejarat.backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Ezzel jelzem, hogy ez egy Spring Data JPA repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Az összes alap CRUD metódust örökölöm:
    // - findAll()
    // - findById()
    // - save()
    // - deleteById()
    // stb.


}
