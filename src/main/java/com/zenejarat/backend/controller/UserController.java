package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.User;
import com.zenejarat.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Ez egy REST vezérlő, amely HTTP kérésekre válaszol felhasználókkal kapcsolatban.
@RequestMapping("/api/users") // Az összes végpont az /api/users útvonal alá tartozik.
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService; // Konstruktoron keresztül megkapom a felhasználókat kezelő szolgáltatást.
    }

    @GetMapping // Lekérem az összes felhasználót.
    public List<User> getAllUsers() {
        return userService.getAllUsers(); // A szolgáltatáson keresztül kérem a teljes listát.
    }

    @GetMapping("/{id}") // Lekérek egy adott felhasználót ID alapján.
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id); // Megpróbálom lekérni a felhasználót.
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nincs ilyen, 404-et adok vissza.
    }

    @PostMapping // Létrehozok egy új felhasználót.
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // Ellenőrzöm, hogy a felhasználónév már foglalt-e.
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.badRequest().body(null); // Ha igen, hibát küldök vissza.
        }
        User savedUser = userService.saveUser(user); // Elmentem a felhasználót.
        return ResponseEntity.ok(savedUser); // Visszaküldöm a mentett objektumot.
    }

    @PutMapping("/{id}") // Frissítem egy felhasználó adatait ID alapján.
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(id, userDetails); // Meghívom a frissítő metódust.
        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Ha nem található, 404-et adok.
    }

    @DeleteMapping("/{id}") // Törlöm a felhasználót ID alapján.
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); // Meghívom a törlő metódust a szolgáltatásban.
        return ResponseEntity.noContent().build(); // Sikeres törlés esetén 204-es státuszt küldök.
    }
}
