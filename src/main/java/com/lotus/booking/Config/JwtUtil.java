package com.lotus.booking.Config;

import com.lotus.booking.Entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured properly!");
        }
        // Initialize the key using the secret
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        LOGGER.info("JWT key initialized successfully");
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId() + "," + user.getEmail())
                .setIssuer("Lotus")
                .claim("roles", Arrays.asList(user.getRole())) // Ensure user.getRole() returns a List<String>
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token);
        Object roles = claims.get("roles");

        if (roles instanceof String) {
            // Convert the roles string to a List<String>
            return Arrays.asList(((String) roles).split(","));
        } else if (roles instanceof List) {
            return (List<String>) roles;
        } else {
            throw new IllegalArgumentException("Invalid roles format in token");
        }
    }

    public Boolean validateAccessToken(String token) throws Exception {
        // String decryptToken = CookieEncryptionUtil.decrypt(token);

        if (token == null || token.isEmpty()) {
            LOGGER.error("Token is null or empty");
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex);
        } catch (IllegalArgumentException exception) {
            LOGGER.error("Token is null or empty", exception);
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            LOGGER.error("Signature validation failed", ex);
        }
        return false;
    }

    public String getSubject(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        System.out.println("Subject: " + subject);
        return subject;
    }

    public Claims parseClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        LOGGER.info("Parsed claims: {}", claims);
        return claims;
    }

    public String getUsernameFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        String email = subject.split(",")[1];
        System.out.println("Subject: " + subject);
        System.out.println("Extracted Email: " + email);
        return subject;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getDomain(HttpServletRequest request) {
        return request.getServerName().toLowerCase().contains("admin") ? "admin.lotuswages.com" : "lotuswages.com";
    }
    
    public String getCookieName(HttpServletRequest request) {
        return request.getServerName().toLowerCase().contains("admin") ? "JWTa" : "JWT";
    }
}