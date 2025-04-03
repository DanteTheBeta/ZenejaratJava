package com.zenejarat.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Ezzel regisztrálom a szűrőt a Spring kontextusba.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Ezzel dolgozom fel és ellenőrzöm a JWT tokeneket.

    @Autowired
    private UserDetailsService userDetailsService; // Ezzel töltöm be a felhasználói adatokat.

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ha a kérés Swagger-hez tartozik, nem futtatom le a JWT ellenőrzést.
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Kinyerem az Authorization fejlécet
        String authHeader = request.getHeader("Authorization");

        // Ha nincs fejléc vagy nem Bearer token, továbbengedem a kérést ellenőrzés nélkül.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Levágom a „Bearer ” részt és kiszedem a tokent
        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwt); // Kinyerem a felhasználónevet a tokenből.
        } catch (Exception e) {
            System.out.println(" Hibás vagy lejárt JWT token: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Ha még nincs bejelentkezve a felhasználó, megpróbálom autentikálni a JWT alapján
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateJwtToken(jwt)) {
                // Létrehozok egy Spring Security autentikációs tokent
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Beállítom a részleteket, például az IP címet, böngészőt stb.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Beállítom az autentikációt a SecurityContext-be
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println(" Token érvénytelen vagy lejárt.");
            }
        }

        // Továbbengedem a kérést a szűrőlánc következő eleméhez
        filterChain.doFilter(request, response);
    }
}
