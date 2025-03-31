package com.zenejarat.backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ez az osztály adatbázis entitásként működik.
@Table(name = "reviews") // Az adatbázisban a "reviews" nevű táblához tartozik.
@EntityListeners(AuditingEntityListener.class) // Bekapcsolom az automatikus időbélyeg-kezelést.
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Az ID értékét automatikusan generálom.
    private Long id;

    @Column(nullable = false) // A "rating" mező kötelező, például 1-5 közötti szám.
    private int rating;

    @Column(length = 1000) // A megjegyzés maximum 1000 karakter hosszú lehet.
    private String comment;

    // Egy értékelés mindig egy adott helyszínhez tartozik.
    @ManyToOne(fetch = FetchType.LAZY) // Lusta betöltéssel kapcsolom össze a Venue entitással.
    @JoinColumn(name = "venue_id", nullable = false) // A kapcsolódó oszlop neve az adatbázisban.
    private Venue venue;

    // Egy felhasználó is írhat értékelést – kötelező kapcsolat.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private com.zenejarat.backend.model.User user;

    @CreatedDate // A létrehozás dátumát automatikusan kezelem.
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // A módosítás dátumát is automatikusan frissítem.
    private LocalDateTime updatedAt;

    // Alapértelmezett konstruktor a JPA számára.
    public Review() {}

    // Paraméteres konstruktor – így egyszerűen létre tudok hozni egy új értékelést.
    public Review(int rating, String comment, Venue venue, com.zenejarat.backend.model.User user) {
        this.rating = rating;
        this.comment = comment;
        this.venue = venue;
        this.user = user;
    }

    // Getter és setter metódusok – ezekkel érem el és módosítom a mezőket.
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public com.zenejarat.backend.model.User getUser() {
        return user;
    }
    public void setUser(com.zenejarat.backend.model.User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
