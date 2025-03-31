package com.zenejarat.backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ezzel jelzem, hogy ez az osztály egy adatbázis entitást képvisel.
@Table(name = "tickets") // A hozzá tartozó tábla neve az adatbázisban: tickets.
@EntityListeners(AuditingEntityListener.class) // Engedélyezem az automatikus létrehozás/módosítás dátumkezelést.
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Azonosító automatikusan generálódik.
    private Long id;

    // A jegy egy adott eseményhez tartozik.
    @ManyToOne(fetch = FetchType.LAZY) // Lusta betöltést használok az Event esetén.
    @JoinColumn(name = "event_id", nullable = false) // Az adatbázisban event_id néven tárolom az eseményhez való kapcsolatot.
    private Event event;

    // A jegy egy adott felhasználóhoz tartozik.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // A jegy státusza (pl. FOGLALT, MEGVÁSÁROLVA).
    @Column(nullable = false)
    private String status;

    // A jegy ára.
    @Column(nullable = false)
    private double price;

    @CreatedDate // Automatikusan beállítom a létrehozás dátumát.
    @Column(updatable = false) // Ez a dátum nem módosítható utólag.
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatikusan frissül, ha módosítom a jegyet.
    private LocalDateTime updatedAt;

    public Ticket() {} // Alapértelmezett konstruktor a JPA-hoz.

    // Paraméteres konstruktor – egyszerűsített példányosítás.
    public Ticket(Event event, User user, String status, double price) {
        this.event = event;
        this.user = user;
        this.status = status;
        this.price = price;
    }

    // Getterek és setterek – ezekkel érem el és módosítom az egyes mezőket.
    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
