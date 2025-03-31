package com.zenejarat.backend.controller;

// Ez az osztály a bejelentkezés válaszüzenetének modellje, amely csak a JWT tokent tartalmazza.
public class LoginResponse {

    private String token; // Ebben tárolom a generált JWT tokent.

    public LoginResponse() {} // Üres konstruktor a deszerializáláshoz.

    // Konstruktor, amellyel egyből beállíthatom a tokent.
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter a token lekéréséhez
    public String getToken() {
        return token;
    }

    // Setter a token beállításához
    public void setToken(String token) {
        this.token = token;
    }
}
