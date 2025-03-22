package com.sopera.config;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final String SECRET_KEY=Base64.getEncoder().encodeToString("sajdfljasldkfasokdfjaslfjoisajfoasjfojsxflsoifjslfslkfjasljfjosnfjasfosnfjsofsdkjslkfjsorfjs".getBytes());

    private SecretKey getSigningKey(){
        byte [] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) 
                .signWith(getSigningKey())
                .compact();
    }
   public String extractUsername(String token) {
    if (token.startsWith("Bearer ")) {
        token = token.substring(7).trim();
    }


    System.out.println("token from extract user: "+token);
        String data= Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token.trim())
                .getPayload()
                .getSubject();

        System.out.println("extracted data: "+data);
        return data;
    }

    public boolean validateToken(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername());
    }

}
