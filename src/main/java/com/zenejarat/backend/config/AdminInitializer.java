package com.zenejarat.backend.config;

import com.zenejarat.backend.model.Role;
import com.zenejarat.backend.model.User;
import com.zenejarat.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    // Ezt a CommandLineRunner-t a Spring automatikusan meghívja az alkalmazás indulásakor
    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Itt megnézem, hogy hány admin szerepel jelenleg az adatbázisban
            long adminCount = userRepository.findAll()
                    .stream()
                    .filter(user -> user.getRole() == Role.ROLE_ADMIN)
                    .count();

            // Ha egyetlen admin sincs, akkor létrehozok egyet
            if (adminCount == 0) {
                User admin = new User();
                admin.setUsername("admin"); // Beállítom a felhasználónevet
                admin.setEmail("admin@zenejarat.hu"); // Beállítom az e-mail címet

                // A jelszót titkosítom a PasswordEncoder segítségével
                admin.setPassword(passwordEncoder.encode("admin123"));

                // A szerepkört ADMIN-ra állítom
                admin.setRole(Role.ROLE_ADMIN);

                // Elmentem az adatbázisba
                userRepository.save(admin);

                // Kiírom konzolra, hogy létrejött
                System.out.println(" Admin user létrehozva: admin / admin123");
            } else {
                // Ha már van legalább egy admin, akkor semmit nem csinálok
                System.out.println(" Admin user(ek) már léteznek, nem hozok létre újat.");
            }
        };
    }
}
