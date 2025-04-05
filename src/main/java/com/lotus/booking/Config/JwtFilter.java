package com.lotus.booking.Config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lotus.booking.Repository.UserRepository;
import com.lotus.booking.Entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final List<String> tokenBlacklist;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository, List<String> tokenBlacklist) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;

        // Extract JWT from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                // System.out.println("Encrypted Token from Cookie: " + token);

                // Decrypt the token
                // String decryptToken = CookieEncryptionUtil.decrypt(token);
                // System.out.println("Decrypted JWT: " + decryptToken);

                // Validate the decrypted token
                if (token == null || !token.contains(".") || token.split("\\.").length != 3) {
                    throw new IllegalArgumentException("Invalid JWT format after decryption");
                }

                if (jwtUtil.validateAccessToken(token) && !tokenBlacklist.contains(token)) {
                    String subject = jwtUtil.getSubject(token);
                    System.out.println("Subject: " + subject);

                    String[] parts = subject.split(",");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid subject format in token");
                    }
                    String email = parts[1];
                    System.out.println("Extracted Email: " + email);

                    List<SimpleGrantedAuthority> authorities = jwtUtil.getRoles(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}