package com.bazaarx.bazaarxbackend.service.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys; // Bu import doğru
import io.jsonwebtoken.io.Decoders; // Bu import doğru


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    // --- Token Oluşturma Metodu (Değişiklik yok) ---
    public String generateToken(String email, Set<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey()) // Varsayılan algoritma HS256 olacak
                .compact();
    }

    // --- İmzalama Anahtarını Alma Metodu (Değişiklik yok) ---
    private SecretKey getSigningKey() {
        // secretKey'i Base64'ten çözerek anahtarı oluşturur
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Kullanıcı Adını (Email) Çıkartma Metodu (Değişiklik yok) ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- Token Geçerliliğini Kontrol Etme Metodu (Değişiklik yok) ---
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- Yardımcı Metotlar ---

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✨ KRİTİK DÜZELTME: extractAllClaims metodu
    private Claims extractAllClaims(String token) {
        try {
            return (Claims) Jwts
                    .parser()     // Parser builder'ı başlat
                    .verifyWith(getSigningKey()) // Anahtarı set et (JJWT 0.12.x'te bu doğru kullanım)
                    .build()                   // Parser'ı oluştur
                    .parseSignedClaims(token)     // JWS (JSON Web Signature) olarak ayrıştır
                    .getPayload();                // Claims objesini al
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // İmza doğrulama hatası (token geçerli değil veya secret key yanlış)
            System.err.println("JWT Signature validation failed: " + e.getMessage());
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token süresi doldu hatası
            System.err.println("JWT expired: " + e.getMessage());
            throw new RuntimeException("JWT expired", e);
        } catch (Exception e) {
            // Diğer genel JWT ayrıştırma hataları
            System.err.println("Error parsing JWT: " + e.getMessage());
            throw new RuntimeException("Error parsing JWT", e);
        }
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}