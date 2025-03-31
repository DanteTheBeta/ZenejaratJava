package com.zenejarat.backend.controller;

import com.zenejarat.backend.model.User;
import com.zenejarat.backend.security.JwtUtil;
import com.zenejarat.backend.service.UserService;
//import com.zenejarat.backend.payload.LoginRequest;
//import com.zenejarat.backend.payload.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController // Ezzel jelzem, hogy ez egy REST vezérlő osztály.
@RequestMapping("/api/auth") // Az összes végpont az /api/auth útvonalon érhető el.
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Ezzel kezelem a hitelesítést.

    @Autowired
    private JwtUtil jwtUtil; // Ezzel generálok és ellenőrzök JWT tokeneket.

    @Autowired
    private UserService userService; // Ezzel kezelem a felhasználókkal kapcsolatos műveleteket.

    @Autowired
    private PasswordEncoder passwordEncoder; // Ezzel kódolom a jelszavakat regisztrációkor.

    // ✅ Regisztráció végpont
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        // Ellenőrzöm, hogy a felhasználónév foglalt-e.
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.badRequest().body("Hiba: A felhasználónév már foglalt!");
        }
        // Ellenőrzöm, hogy az e-mail cím már használatban van-e.
        if (userService.isEmailTaken(user.getEmail())) {
            return ResponseEntity.badRequest().body("Hiba: Az e-mail már foglalt!");
        }

        // Jelszót titkosítok a mentés előtt.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 🔒 Alapértelmezett szerepkört állítok be, ha nincs megadva.
        if (user.getRole() == null) {
            user.setRole(com.zenejarat.backend.model.Role.ROLE_USER);
        }

        // Elmentem a felhasználót az adatbázisba.
        userService.saveUser(user);
        return ResponseEntity.ok("Sikeres regisztráció!");
    }

    // 🔑 Bejelentkezési végpont + JWT token generálás
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Megpróbálom hitelesíteni a felhasználót a megadott adatokkal.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Sikeres hitelesítés után JWT tokent generálok.
            String jwt = jwtUtil.generateJwtToken(loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (BadCredentialsException e) {
            // Ha a hitelesítés sikertelen, hibát küldök vissza.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hiba: Hibás felhasználónév vagy jelszó!");
        }
    }

    // 👤 Visszaadom a jelenleg bejelentkezett felhasználó adatait a JWT token alapján
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Kinyerem a felhasználónevet a tokenből.
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Optional<User> user = userService.getUserByUsername(username);

            // Ha nincs ilyen felhasználó, 404-es hibát küldök.
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Felhasználó nem található.");
            }

            // Visszaadom a felhasználó adatait.
            return ResponseEntity.ok(user.get());
        } catch (Exception e) {
            // Ha a token hibás vagy lejárt, visszautasítom a kérést.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hibás vagy lejárt token.");
        }
    }
}
