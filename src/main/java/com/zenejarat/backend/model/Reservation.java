package com.zenejarat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ez az osztály egy adatbázis entitást képvisel.
@Table(name = "reservations") // A "reservations" nevű adatbázistáblához tartozik.
@EntityListeners(AuditingEntityListener.class) // Figyelem a létrehozás és módosítás időpontját.
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Az ID automatikusan generálódik.
    private Long id;

    // Egy helyszínhez több foglalás is tartozhat.
    @ManyToOne(fetch = FetchType.LAZY) // A kapcsolódó helyszínt csak szükség esetén töltöm be.
    @JoinColumn(name = "venue_id", nullable = false) // A "venue_id" oszlop kapcsolja össze a Venue-val.
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Kikerülöm a lazy betöltésből származó JSON hibákat.
    private Venue venue;

    @Column(nullable = false) // A kezdési idő kötelező.
    private LocalDateTime startTime;

    @Column(nullable = false) // A zárási idő is kötelező.
    private LocalDateTime endTime;

    @CreatedDate // Automatikusan beállítom, mikor lett létrehozva a foglalás.
    @Column(updatable = false) // A létrehozás dátuma nem módosítható.
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatikusan frissül, ha módosítom a foglalást.
    private LocalDateTime updatedAt;

    // Alapértelmezett konstruktor a JPA működéséhez.
    public Reservation() {}

    // Paraméteres konstruktor – így egyszerűen tudok új foglalást létrehozni.
    public Reservation(Venue venue, LocalDateTime startTime, LocalDateTime endTime) {
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter és setter metódusok – ezekkel érem el és módosítom a mezőket.
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
