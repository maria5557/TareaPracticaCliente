package com.example.client.interceptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class JwtUtil {

    private static final String SECRET_KEY = "clave-secreta-muy-fuerte-para-jwt-123456";

    public static String generateToken(String id, String name, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("name", name);
        claims.put("email", email);

        System.out.println("claims: " + claims);

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)  // <--- firma con clave segura
                .compact();
        System.out.println("jwt: " + jwt);
        return jwt;
    }
}
