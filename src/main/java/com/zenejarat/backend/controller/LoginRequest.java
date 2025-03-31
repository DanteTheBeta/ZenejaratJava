package com.zenejarat.backend.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

// Ez az osztály a bejelentkezési kérés adatmodellje.
public class LoginRequest {

    @NotBlank(message = "Felhasználónév nem lehet üres!") // Ellenőrzöm, hogy a felhasználónév mező nincs-e üresen hagyva.
    @JsonProperty("username") // Megadom, hogy ezt a mezőt "username" néven várja a JSON-ben.
    private String username;

    @NotBlank(message = "Jelszó nem lehet üres!") // Ellenőrzöm, hogy a jelszómező sem lehet üres.
    @JsonProperty("password") // A JSON-ben "password" néven jelenik meg ez a mező.
    private String password;

    public LoginRequest() {}

    // Konstruktor, amivel példányosítani tudom a felhasználónevet és jelszót.
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter a felhasználónévhez
    public String getUsername() {
        return username;
    }

    // Setter a felhasználónévhez
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter a jelszóhoz
    public String getPassword() {
        return password;
    }

    // Setter a jelszóhoz
    public void setPassword(String password) {
        this.password = password;
    }
}
