package com.zenejarat.backend.security;

import com.zenejarat.backend.model.User;
import com.zenejarat.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service // Ezzel regisztrálom a Spring Context-ben mint szolgáltatást.
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Ezen keresztül érem el az adatbázisban tárolt felhasználókat.

    @Override
    @Transactional // Gondoskodom róla, hogy az adatbázis művelet tranzakcióban történjen.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Megpróbálom lekérni a felhasználót a megadott felhasználónév alapján.
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Létrehozok egy authority-t (jogosultságot) a szerepkör alapján.
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(appUser.getRole().name()) // Pl. ROLE_USER vagy ROLE_ADMIN
        );

        // Visszatérek egy UserDetails példánnyal, amit a Spring Security használ a hitelesítéshez.
        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),  //  A jelszó hash-elve van (BCrypt-tel)
                authorities
        );
    }
}
