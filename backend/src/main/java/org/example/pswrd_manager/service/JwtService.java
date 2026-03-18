package org.example.pswrd_manager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostConstruct
    public void init() {
        log.info("JWT Secret loaded: length={}", secretKey != null ? secretKey.length() : 0);
        log.info("JWT Expiration: {} ms ({} hours)",
                jwtExpiration, jwtExpiration / 1000 / 60 / 60);

        if (secretKey == null || secretKey.length() < 32) {
            log.warn("JWT secret is too short! Should be at least 32 characters");
        }
    }

    public String generateToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        // для наглядности разбора механизма
        var token = Jwts.builder().addClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public Boolean validateToken(String token) throws IllegalArgumentException, JwtException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid JWT token");
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractClaim(String token, Function<Claims, String> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        var parser = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return parser;
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role").toString());
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }


}
