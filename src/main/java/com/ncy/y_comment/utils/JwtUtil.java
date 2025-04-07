package com.ncy.y_comment.utils;


import com.ncy.y_comment.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";

    public static String createToken(UserDTO user, long expireSeconds) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("nickname", user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireSeconds * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
