package com.house.houseviewing.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 키 생성
    private final long tokenValidityInMilliseconds = 3600000; // ms 단위로 1시간

    public String createToken(String loginId){
        Claims claims = Jwts.claims().setSubject(loginId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims) // 유저 정보
                .setIssuedAt(now) // 발급 시간 기록
                .setExpiration(validity) // 만료 시간 기록
                .signWith(key) // 암호화
                .compact(); // 문자열로 변환
    }

    public String getLoginId(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key) // 식별용 키 설정
                .build()
                .parseClaimsJws(token)// 해석
                .getBody()
                .getSubject(); // 유저 아이디 꺼내기
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
