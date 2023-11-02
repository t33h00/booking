package com.lotus.booking.Service;

import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Override
    public String saveUser(User user) throws ValidationException {
//        Set<ConstraintViolation<User>> violations = validator.validate(user);
//        System.out.println("Violation: " + violations);
        try{
            User newUser = new User();
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);
            return "user added.";
        } catch (ValidationException e){
            System.out.println();
            return (e.getMessage());
        }
    }

    public User findUser(Long id){
        return userRepository.findById(id).orElseThrow(null);
    }

    public List<User> findAllUser(){
        return userRepository.findAll();
    }

}
