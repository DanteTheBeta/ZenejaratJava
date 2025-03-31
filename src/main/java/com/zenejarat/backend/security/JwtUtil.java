package com.zenejarat.backend.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component // Ezzel regisztrálom a JwtUtil-t a Spring Context-be.
public class JwtUtil {

    // 🔐 Titkos kulcs a JWT aláíráshoz (base64-ben kódolt)
    private final String jwtSecret = Base64.getEncoder().encodeToString("mySecretKey".getBytes());

    // ⏰ Token lejárati idő: 24 óra (ezredmásodpercben)
    private final long jwtExpirationMs = 86400000;

    // 🔐 JWT token generálása a felhasználónév alapján
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Beállítom a felhasználónevet a token tartalmaként (subject).
                .setIssuedAt(new Date()) // Beállítom a kiadás időpontját.
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Beállítom a lejárati időt.
                .signWith(SignatureAlgorithm.HS256, jwtSecret) // HMAC SHA-256 algoritmussal írom alá.
                .compact(); // Végül összeállítom a tokent.
    }

    // 👤 Felhasználónév (subject) kinyerése a tokenből
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ⏳ Lejárati dátum kinyerése a tokenből
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 📦 Általános claim kinyerő – megadott művelettel dolgozom fel a claim-et
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Kinyerem az összes claim-et
        return claimsResolver.apply(claims); // Majd alkalmazom a műveletet (pl. getSubject, getExpiration stb.)
    }

    // 🔍 Token összes claim-jének kinyerése (belső segédfüggvény)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret) // Beállítom a titkos kulcsot.
                .parseClaimsJws(token) // Elemzem a tokent.
                .getBody(); // Visszaadom a token tartalmát (payload).
    }

    // ✅ Token érvényességének ellenőrzése
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token); // Megpróbálom dekódolni.
            return true; // Ha nincs kivétel, a token érvényes.
        } catch (ExpiredJwtException e) {
            System.out.println("⚠️ Token lejárt: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("⚠️ Nem támogatott token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("⚠️ Sérült token: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("⚠️ Aláírás hiba: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Üres token: " + e.getMessage());
        }
        return false; // Bármely hiba esetén hamissal térek vissza.
    }
}
