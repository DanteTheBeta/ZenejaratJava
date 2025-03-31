package com.zenejarat.backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ez az osztály az adatbázisban egy entitást képvisel.
@Table(name = "venues") // Az adatbázisban a „venues” nevű táblához tartozik.
@EntityListeners(AuditingEntityListener.class) // Engedélyezem az automatikus dátumkezelést (létrehozás, módosítás).
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Az azonosító automatikusan generálódik.
    private Long id;

    @Column(nullable = false) // A név mező kitöltése kötelező.
    private String name;

    @Column(nullable = false) // A cím mező kitöltése is kötelező.
    private String address;

    private String description; // Opcionális leírás a helyszínhez.

    @CreatedDate // A foglalás létrehozásának idejét automatikusan állítom.
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // A módosítás idejét is automatikusan frissítem.
    private LocalDateTime updatedAt;

    // Alapértelmezett konstruktor – szükséges a JPA működéséhez.
    public Venue() {}

    // Paraméteres konstruktor – ezzel gyorsan tudok példányt létrehozni.
    public Venue(String name, String address, String description) {
        this.name = name;
        this.address = address;
        this.description = description;
    }

    // Getterek és setterek – ezekkel tudom elérni és beállítani a mezők értékét.
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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
