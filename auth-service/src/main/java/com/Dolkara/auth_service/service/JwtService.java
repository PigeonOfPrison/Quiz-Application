package com.Dolkara.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.webauthn.api.CredProtectAuthenticationExtensionsClientInput.CredProtect;
import org.springframework.stereotype.Service;

import com.Dolkara.auth_service.dao.UserRepo;
import com.Dolkara.auth_service.entity.Credentials;
import com.Dolkara.auth_service.entity.Role;
import com.Dolkara.auth_service.entity.Users;

import javax.crypto.SecretKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final UserRepo userRepo;

    @Autowired
    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        Users user = userRepo.findByUsername(username);

        if(user != null) {
            claims.put("email", user.getEmail());
            claims.put("userid", user.getId());

            Set<Role> roles = user.getRoles();

            if(roles != null && !roles.isEmpty()) {
                List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

                claims.put("roles", roleNames);
            }
            
        }

        return createToken(claims, username);
        
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if(roles instanceof List)
                return (List<String>) roles;
            return new ArrayList<>();
        });
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        final String username = extractUserName(token);

        return (username != null) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
