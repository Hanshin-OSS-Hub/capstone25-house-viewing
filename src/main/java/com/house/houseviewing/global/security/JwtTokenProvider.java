package com.house.houseviewing.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessToken;

    private Key key;

    @PostConstruct
    protected void init(){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long userId, String loginId){
        Date now = new Date(); // 현재시간
        Date expiry = new Date(now.getTime() + accessToken); // 만료시간

        return Jwts.builder()
                .setSubject(loginId)
                .claim("userId", userId)
                .claim("loginId", loginId)
                .setIssuedAt(now) // 토큰 발행된 시간
                .setExpiration(expiry) // 유효기간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Long getUserId(String token){
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    public String getLoginId(String token){
        Claims claims = parseClaims(token);
        return claims.get("loginId", String.class);
    }

    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
