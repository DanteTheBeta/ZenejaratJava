package com.zenejarat.backend.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String jwtSecret = Base64.getEncoder().encodeToString("mySecretKey".getBytes()); // 🔹 Titkos kulcs biztonságosabban
    private final long jwtExpirationMs = 86400000; // 🔹 24 óra (milliszekundumban)

    // 🔹 **JWT generálása**
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret) // 🔹 HS256 használata (ajánlott)
                .compact();
    }

    // 🔹 **Felhasználónév kinyerése a JWT tokenből**
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 **Token lejárati idejének kinyerése**
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🔹 **Általános metódus a token claim-ek kibontására**
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔹 **Token érvényességének ellenőrzése**
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Hiba: A token lejárt!");
        } catch (UnsupportedJwtException e) {
            System.out.println("Hiba: Nem támogatott JWT formátum!");
        } catch (MalformedJwtException e) {
            System.out.println("Hiba: Hibás JWT token!");
        } catch (SignatureException e) {
            System.out.println("Hiba: Helytelen aláírás!");
        } catch (IllegalArgumentException e) {
            System.out.println("Hiba: Üres vagy null token!");
        }
        return false;
    }
}
