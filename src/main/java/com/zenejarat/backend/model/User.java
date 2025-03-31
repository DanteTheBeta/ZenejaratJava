package com.zenejarat.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Ezzel jelzem, hogy ez egy JPA entitás.
@Table(name = "users") // A hozzárendelt tábla neve az adatbázisban: users.
@EntityListeners(AuditingEntityListener.class) // Automatikusan kezelem a létrehozási és módosítási időbélyegeket.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Azonosítót automatikusan generálom.
    private Long id;

    @NotBlank(message = "A felhasználónév nem lehet üres")
    @Size(min = 3, max = 50, message = "A felhasználónév 3 és 50 karakter között legyen")
    @Column(nullable = false, unique = true) // Nem lehet üres és nem ismétlődhet.
    private String username;

    @NotBlank(message = "A jelszó nem lehet üres")
    @Size(min = 6, message = "A jelszónak legalább 6 karakter hosszúnak kell lennie")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Az email cím nem lehet üres")
    @Email(message = "Az email cím formátuma helytelen")
    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING) // A szerepkört szövegként tárolom (nem ordinal).
    @Column(nullable = false)
    private Role role;

    @CreatedDate // Automatikusan beállítom a létrehozás idejét.
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatikusan frissül, ha módosul a rekord.
    private LocalDateTime updatedAt;

    // Alapértelmezett konstruktor – szükséges a JPA működéséhez.
    public User() {}

    // Paraméteres konstruktor – gyors példányosításra használom.
    public User(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getterek és setterek – ezekkel érem el és állítom be a mezőket.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
