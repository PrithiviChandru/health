package com.phc.healthcare.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenManager {


    private static String SECRET_KEY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final long EXPIRATION_TIME = 300000;

//    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//    private static final SecureRandom RANDOM = new SecureRandom();
//    private static final int TOKEN_LENGTH = 25;

    public static String generateAuthToken(String userId) {

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public static String generate() {
//
//        StringBuilder token = new StringBuilder();
//        for (int i = 0; i < TOKEN_LENGTH; i++) {
//            int rI = RANDOM.nextInt(ALPHABET.length());
//            char rC = ALPHABET.charAt(rI);
//            token.append(rC);
//        }
//
//        System.out.println(token);
//        return token.toString();
//    }
}
