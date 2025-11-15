package com.jokardo.crm.order_service.util.jwtTokenGenerator;

import com.jokardo.crm.order_service.security.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAccessTokenGenerator {

    private final JwtProperties jwtProperties;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    private Key key;

    public String createAdminAccessToken() {
        Claims claims = Jwts.claims().setSubject("admin");
        claims.put("id", 2);
        claims.put("roles", Role.ROLE_ADMIN);

        Date now = new Date();
        Date validity = new Date(now.getTime() + Integer.parseInt(jwtProperties.getAccess()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public String createUserAccessToken() {
        Claims claims = Jwts.claims().setSubject("john");
        claims.put("id", 3);
        claims.put("roles", Role.ROLE_USER);

        Date now = new Date();
        Date validity = new Date(now.getTime() + Integer.parseInt(jwtProperties.getAccess()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }
}
