package com.zenejarat.backend.repository;

import com.zenejarat.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Ezzel jelzem, hogy ez egy adatbázisos repository osztály (Spring Data JPA)
public interface EventRepository extends JpaRepository<Event, Long> {
    // Nem kell semmit kézzel írnom, mert a JpaRepository már biztosítja az alap CRUD műveleteket:
    // - findAll()
    // - findById()
    // - save()
    // - deleteById()
    // - stb.

}
