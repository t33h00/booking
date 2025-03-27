package com.lotus.booking.Controllers;

import com.lotus.booking.Config.SecurityConfig;
import com.lotus.booking.DTO.AuthenticationResponse;
import com.lotus.booking.DTO.UserResponse;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;

    @PostMapping("save")
    public ResponseEntity<?> saveUser(@RequestBody User user, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        Optional<User> findUser = userService.findUserByEmail(user);
        if (findUser.isPresent()){
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } else{
            String siteURL = SecurityConfig.getSiteURL(request);
            userService.saveUser(user);
            userService.sendVerificationEmail(user, siteURL);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> userProfile(@AuthenticationPrincipal User user, @PathVariable("id") Long id){
        if(user.getId().equals(id)){
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            User newUser = userService.findUser(id);
            authenticationResponse.setId(newUser.getId());
            authenticationResponse.setEmail(newUser.getEmail());
            authenticationResponse.setFirstName(newUser.getFirstName());
            authenticationResponse.setLastName(newUser.getLastName());
            authenticationResponse.setAuthorities(newUser.getAuthorities().toString());
            return ResponseEntity.ok(authenticationResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/admin/users")
    public List<UserResponse> getAllUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/admin/sendemail")
    public void sendEmail(@AuthenticationPrincipal User user, String email, String subject, String content) throws UnsupportedEncodingException, MessagingException{
        userService.contactEmail(email, subject, content);
    }

}
