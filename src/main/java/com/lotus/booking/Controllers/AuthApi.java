package com.lotus.booking.Controllers;

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
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {
        "https://lotus-ui.web.app",
        "https://lotuscheckin.web.app",
        "https://lotusnails-67281.web.app",
        "https://lotus-nailsspa.web.app",
        "https://lotuswages.com",
        "https://admin.lotuswages.com",
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

            //Determine the domain and cookie name based on the request
            // String domain = jwtUtil.getDomain(request);
            // String cookieName = jwtUtil.getCookieName(request);
            // System.out.println("Client Name: " + domain);
            // System.out.println("Cookie Name: " + cookieName);
            ResponseCookie cookie = ResponseCookie.from("JWT", accessToken)
                    .httpOnly(true)
                    .secure(true)  // Required for Safari
                    .path("/")
                    .sameSite("None") // Required for cross-site cookies
                    // .domain(domain)
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
            // Extract the token from the cookies
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("JWT".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            System.out.println("Extracted Token: " + token);

            // Validate the token
            if (token != null) {
                try {
                    if (jwtUtil.validateAccessToken(token)) {
                        // Extract user details from the token
                        String email = jwtUtil.getEmailFromToken(token);
                        User user = userRepository.findByEmail(email).orElse(null);
                        if (user != null) {
                            // Create a response object with validation status and user details
                            Map<String, Object> response = new HashMap<>();
                            response.put("valid", true);
                            response.put("user", new AuthenticationResponse(
                                    user.getId(),
                                    user.getEmail(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getAuthorities().toString()
                            ));
                            return ResponseEntity.ok(response);
                        }
                    }
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    // Handle expired token
                    System.out.println("Token expired: " + e.getMessage());
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", false);
                    response.put("error", "Token expired");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            // If the token is invalid or not found
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Invalid or missing token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
                if ("JWT".equals(cookie.getName())) {
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
        // String domain = jwtUtil.getDomain(request);
        // String cookieName = jwtUtil.getCookieName(request);
        // System.out.println("Server Name: " + domain);
        // System.out.println("Cookie Name: " + cookieName);

        ResponseCookie cookie = ResponseCookie.from("JWT", null)
                .httpOnly(true)
                .secure(true)  // Required for Safari
                .path("/")
                .sameSite("None")
                // .domain(domain)
                .maxAge(0)// Required for cross-site cookies
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        System.out.println("Logged out!");

        return ResponseEntity.ok("Logged out successfully");
    }
}