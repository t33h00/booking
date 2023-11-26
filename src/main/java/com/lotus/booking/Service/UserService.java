package com.lotus.booking.Service;

import com.lotus.booking.Entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    public String saveUser(User user);
    public User findUser(Long id);

    public List<User> findAllUser();
    void sendVerificationEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException;
    boolean verifyVerificationCode(String code);
    Optional<User> findUserByEmail(User user);
}
