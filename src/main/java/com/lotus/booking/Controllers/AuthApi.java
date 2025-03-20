package com.lotus.booking.Controllers;

import com.lotus.booking.Config.JwtUtil;
import com.lotus.booking.DTO.AuthenticationRequest;
import com.lotus.booking.DTO.AuthenticationResponse;
import com.lotus.booking.DTO.TokenValidationRequest;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.UserRepository;
import com.lotus.booking.Service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(maxAge = 3600)
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenticationRequest authenticationRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail().toLowerCase(), authenticationRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getAuthorities().toString());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,accessToken).body(authenticationResponse);
        } catch (BadCredentialsException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/token")
    public ResponseEntity<?> tokenValidation(@AuthenticationPrincipal User user, TokenValidationRequest tokenValidationRequest){
        try{
            Boolean isValid = (jwtUtil.validateAccessToken(tokenValidationRequest.getToken()));
            return ResponseEntity.ok(isValid);
        } catch (Exception e){
            return ResponseEntity.ok(false);
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

}

