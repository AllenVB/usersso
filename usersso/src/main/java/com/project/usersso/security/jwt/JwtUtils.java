package com.project.usersso.security.jwt;

import com.project.usersso.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${usersso.app.jwtSecret}")
    private String jwtSecret;

    @Value("${usersso.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Token'ı imzalamak için kullanılacak anahtarı oluşturur
    private Key key() {
        // Eğer secret key Base64 değilse düz string olarak byte'a çeviriyoruz.
        // Güvenlik standardı: HMAC-SHA algoritmaları için en az 256 bit (32 byte) anahtar gerekir.
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); 
        // NOT: Eğer uygulaman çalışırken "Illegal base64 character" hatası verirse, 
        // application.properties dosyasındaki secret key'i Base64 formatında bir string ile değiştirmen gerekir.
        // Ya da test için şu satırı kullanabilirsin: 
        // return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. Token Oluşturma (Login başarılı olunca çağrılır)
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Token kimin için?
                .setIssuedAt(new Date()) // Ne zaman oluşturuldu?
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Ne zaman ölecek?
                .signWith(key(), SignatureAlgorithm.HS256) // İmzala
                .compact();
    }

    // 2. Token'dan Kullanıcı Adını Çıkarma
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. Token Doğrulama (Her istekte çalışır)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Geçersiz JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Süresi dolmuş JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Desteklenmeyen JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claim string boş: {}", e.getMessage());
        }

        return false;
    }
}