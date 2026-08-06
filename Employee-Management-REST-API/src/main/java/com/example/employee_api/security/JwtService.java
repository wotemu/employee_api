package com.example.employee_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "ThisIsMyVeryLongSecretKeyForJWTAuthentication123456789";

    private Key signingKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails user){

        return Jwts.builder()

                .subject(user.getUsername())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60 * 24))

                .signWith(signingKey())

                .compact();
    }

    public String extractUsername(String token){

        return Jwts.parser()

                .verifyWith((SecretKey) signingKey())

                .build()

                .parseSignedClaims(token)

                .getPayload()

                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails user){

        return extractUsername(token)
                .equals(user.getUsername())
                && !isExpired(token);
    }

    private boolean isExpired(String token){

        Date expiration =
                Jwts.parser()

                        .verifyWith((SecretKey) signingKey())

                        .build()

                        .parseSignedClaims(token)

                        .getPayload()

                        .getExpiration();

        return expiration.before(new Date());
    }

}
