package com.lotus.booking.Controllers;

import com.lotus.booking.Config.CookieEncryptionUtil;
import com.lotus.booking.Config.JwtUtil;
import com.lotus.booking.Config.TokenBlacklist;
import com.lotus.booking.DTO.AuthenticationRequest;
import com.lotus.booking.DTO.AuthenticationResponse;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.UserRepository;
import com.lotus.booking.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@CrossOrigin(origins = {
        "https://lotus-ui.web.app",
        "https://lotuscheckin.web.app",
        "https://lotusnails-67281.web.app",
        "https://lotus-nailsspa.web.app",
        "https://lotuswages.com",
        "http://localhost:3000",
        "http://localhost:3001"
}, allowCredentials = "true", maxAge = 3600)
@RequestMapping("/auth")
public class AuthApi {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail().toLowerCase(), authenticationRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            // Determine the domain and cookie name based on the request
            String domain = request.getServerName().contains("admin") ? "admin.lotuswages.com" : "lotuswages.com";
            String cookieName = request.getServerName().contains("admin") ? "JWTa" : "JWT";

            ResponseCookie cookie = ResponseCookie.from(cookieName, accessToken)
                    .httpOnly(true)
                    .secure(true)  // Required for Safari
                    .path("/")
                    .sameSite("None")
                    .domain(domain)
                    .maxAge(24 * 60 * 60)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                    user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getAuthorities().toString());
            return ResponseEntity.ok(authenticationResponse);
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/token")
    public ResponseEntity<?> tokenValidation(HttpServletRequest request) {
        try {
            // Extract JWT from cookies
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("JWT".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // Validate the token
            if (token != null && jwtUtil.validateAccessToken(token)) {
                System.out.println("Validated JWT: " + token); // Log the validated JWT
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code){
        boolean verified = userService.verifyVerificationCode(code);
        if( verified){
            return "Success";
        } else {
            return "failed";
        }
    }

    @GetMapping("/reset")
    public ResponseEntity<?> sendCode(@Param("email") String email) throws MessagingException, UnsupportedEncodingException {
        if(userService.sendVerificationCode(email.toLowerCase())){
            return new ResponseEntity<>("Found", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<?> resetPassword(@RequestBody AuthenticationRequest authenticationRequest){
        String code = authenticationRequest.getCode();
        User user = userRepository.findByVerificationCode(code);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeExpire = user.getDate();
        String password =passwordEncoder.encode(authenticationRequest.getPassword());
        if(timeExpire.isAfter(now)){
            userRepository.updatePassword(password,user.getEmail());
            System.out.println("Successfully reset the password");
            return new ResponseEntity<>("Password reset",HttpStatus.ACCEPTED);
        } else if (timeExpire.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("Time expired!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Invalid code",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) throws Exception {
        // Extract the token from the cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT".equals(cookie.getName()) || "JTWa".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Add the token to the blacklist
        if (token != null) {
            tokenBlacklist.add(token);
        }

        // Clear the cookie
        String domain = request.getServerName().contains("admin")? "admin.lotuswages.com" : "lotuswages.com";
        String cookieName = request.getServerName().contains("admin") ? "JWTa" : "JWT";

        ResponseCookie cookie = ResponseCookie.from(cookieName, null)
                .httpOnly(true)
                .secure(true)  // Required for Safari
                .path("/")
                .sameSite("None")
                .domain(domain)
                .maxAge(0)// Required for cross-site cookies
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        System.out.println("Logged out!");

        return ResponseEntity.ok("Logged out successfully");
    }
}