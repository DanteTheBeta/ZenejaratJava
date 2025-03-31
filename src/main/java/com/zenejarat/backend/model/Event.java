package com.zenejarat.backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ezzel jelzem, hogy ez egy adatbázis entitás.
@Table(name = "events") // Az adatbázisban az "events" nevű táblához tartozik.
@EntityListeners(AuditingEntityListener.class) // Automatikusan figyelem a létrehozás és módosítás dátumát.
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Azonosító automatikusan generálódik.
    private Long id;

    @Column(nullable = false) // A név mező kötelező.
    private String name;

    private String description; // Leírás, nem kötelező.

    @Column(nullable = false) // Az esemény időpontja kötelező.
    private LocalDateTime eventDate;

    // Egy eseményhez tartozik egy helyszín. Lusta betöltéssel kezelem a kapcsolatot.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false) // A kapcsolódó oszlop neve az adatbázisban.
    private Venue venue;

    @CreatedDate // Automatikusan beállítom a létrehozás idejét.
    @Column(updatable = false) // Ez az érték nem módosítható utólag.
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatikusan frissül, ha módosítom a rekordot.
    private LocalDateTime updatedAt;

    // Alapértelmezett konstruktor – szükséges a JPA működéséhez.
    public Event() {}

    // Paraméteres konstruktor – ezzel egyszerűen létre tudok hozni egy példányt.
    public Event(String name, String description, LocalDateTime eventDate, Venue venue) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
    }

    // Getter és setter metódusok – ezekkel érem el és módosítom a mezőket.
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
