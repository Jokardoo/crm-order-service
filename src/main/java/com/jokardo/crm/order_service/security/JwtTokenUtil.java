package com.jokardo.crm.order_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public boolean validateToken(String token) {
        return !Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getEncoded()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().before(new Date());
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getEncoded()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> resolveRoles(String token) {
        return List.of(Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key.getEncoded()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", String.class));
    }




}
