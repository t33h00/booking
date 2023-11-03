package com.lotus.booking.Service;

import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Override
    public String saveUser(User user) throws ValidationException {
        try{
            User newUser = new User();
            String verificationCode = RandomString.make(64);
            newUser.setVerificationCode(verificationCode);
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setEnable(false);
            userRepository.save(newUser);
            return "user added.";

        } catch (ValidationException e){
            System.out.println();
            return (e.getMessage());
        }
    }

    public boolean verifyVerificationCode(String code){
        User user = userRepository.findByVerificationCode(code);
        if(user == null || user.isEnabled()){
            return false;
        } else{
            userRepository.enable(user.getId());
            return true;
        }
    }

    public User findUser(Long id){
        return userRepository.findById(id).orElseThrow(null);
    }

    public List<User> findAllUser(){
        return userRepository.findAll();
    }

    public void sendVerificationEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException {
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        String verifyURL = siteURL+ "/auth/verify?code=" + findUser.getVerificationCode();
        System.out.println(verifyURL);
        String subject = "Please verify your registration";
        String senderName = "Lotus Nail and Spa";
        String mailContent = "<p>Hello, " + user.getFirstName() + ",</p>";
        mailContent += "<p> Please click the link to verify your registration</p>";
        mailContent +="<a href =\"" + verifyURL + "\">VERIFY</a>";
        mailContent += "<p>Thank you!</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("4businessoffice@gmail.com", senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent,true);

        mailSender.send(message);
    }

}
