package com.lotus.booking.Controllers;

import com.lotus.booking.Config.JwtUtil;
import com.lotus.booking.DTO.AuthenicationRequest;
import com.lotus.booking.DTO.AuthenticationResponse;
import com.lotus.booking.Entity.Role;
import com.lotus.booking.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@CrossOrigin(exposedHeaders = {"Authorization"},origins = "*")
public class AuthApi {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenicationRequest authenicationRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenicationRequest.getEmail(),authenicationRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getRoles());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,accessToken).body(authenticationResponse);
        } catch (BadCredentialsException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}