package com.zenejarat.backend.config;

import com.zenejarat.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration // Ezzel jelzem, hogy ez egy konfigurációs osztály.
@EnableWebSecurity // Engedélyezem a Spring Security webes támogatását.
@EnableMethodSecurity // Engedélyezem a metódus szintű biztonságot, pl. @PreAuthorize használatát.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Ezt a szűrőt használom a JWT tokenek ellenőrzésére.
    private final UserDetailsService userDetailsService; // Ez szolgáltatja a felhasználói adatokat az ellenőrzéshez.

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // A CSRF védelmet letiltom, mert REST API-hoz és tokenes hitelesítéshez nincs rá szükségem.
                .csrf(csrf -> csrf.disable())

                // Nem használok szerver oldali session-t, minden kérés stateless (JWT alapú).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Itt határozom meg, hogy mely végpontokat engedek elérésre kinek.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll() // Ezek bárki számára elérhetőek.
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // A dokumentáció is nyilvános.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Ezeket csak admin érheti el.
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN") // Ezeket felhasználó és admin is elérheti.
                        .anyRequest().authenticated() // Minden más végponthoz hitelesítés szükséges.
                )

                // Beállítom, hogy milyen hitelesítési szolgáltatót használjak.
                .authenticationProvider(authenticationProvider())

                // A JWT szűrőt beillesztem a filterláncba a jelszó alapú hitelesítő elé.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Ezzel titkosítom a jelszavakat bcrypt algoritmussal.
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // A Spring-től kérem el az AuthenticationManager példányt.
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Megadom, hogy honnan jöjjenek a felhasználói adatok.
        authProvider.setPasswordEncoder(passwordEncoder()); // Beállítom a jelszavak ellenőrzéséhez használt kódolót.
        return authProvider;
    }

    // Modern CORS konfiguráció (Spring Security 6.1+)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // A frontendem erről a címről jön, ezt engedem.
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Engedélyezett HTTP metódusok.
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Ezeket a fejléceket fogadom el.
        configuration.setAllowCredentials(true); // Engedem, hogy hitelesítő adatok (pl. token) is menjenek.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Minden végpontra alkalmazom a szabályokat.
        return source;
    }
}
