package com.lotus.booking.Service;

import com.lotus.booking.DTO.UserResponse;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Override
    public String saveUser(User user) throws ValidationException {
            Long id = idGeneration();
            try{
                User newUser = new User();
                String verificationCode = RandomString.make(64);
                newUser.setVerificationCode(verificationCode);
                newUser.setId(id);
                newUser.setFirstName(user.getFirstName());
                newUser.setLastName(user.getLastName());
                newUser.setEmail(user.getEmail().toLowerCase());
                newUser.setRole("USER");
                newUser.setPassword(passwordEncoder.encode(user.getPassword()));
                newUser.setDate(LocalDateTime.now().plusMinutes(15));
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

    public boolean updatePassword(String password, String code){
        LocalDateTime now = LocalDateTime.now();
        Optional<User> user = Optional.ofNullable(userRepository.findByVerificationCode(code));
        if(user.isPresent() && user.get().getDate().isAfter(now)){
            String ePassword = passwordEncoder.encode(password);
            userRepository.updatePassword(ePassword,user.get().getEmail());
            System.out.println("password update successfully!");
            return true;
        } else if(user.isPresent() && user.get().getDate().isAfter(now)){
            System.out.println("Time expired! Try again.");
            return false;
        } else {
            System.out.println("Invalid code");
            return false;
        }
    }

    public User findUser(Long id){
        return userRepository.findById(id).orElseThrow(null);
    }

    public List<UserResponse> findAllUsers(){
        return userRepository.findAllUsers();
    }

    public void sendVerificationEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException {
        User findUser = userRepository.findByEmail(user.getEmail().toLowerCase()).orElseThrow();
        String verifyURL = siteURL+ "/auth/verify?code=" + findUser.getVerificationCode();
        System.out.println(verifyURL);
        String subject = "Please verify your registration";
        String senderName = "Lotus Nail and Spa";
        String mailContent = "<p>Hello, " + user.getFirstName() + ",</p>";
        mailContent += "<p> Please click the link to verify your registration</p>";
        mailContent +="<a href =\"" + verifyURL + "\">VERIFY</a>";
        mailContent += "<p>The link will expired in 15 minutes</p>";
        mailContent += "<p>Thank you!</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("4businessoffice@gmail.com", senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent,true);

        mailSender.send(message);
    }

    public void contactEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        Optional<User> user = userRepository.findByEmail(email);
        String senderName = "Lotus Nail and Spa";
        String mailContent = "<p>Hello " + user.get().getFirstName() + ",</p>";
        mailContent += content;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("4businessoffice@gmail.com", senderName);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(mailContent,true);

        mailSender.send(message);
    }

    public boolean sendVerificationCode(String email) throws MessagingException, UnsupportedEncodingException{
        Optional<User> user = userRepository.findByEmail(email.toLowerCase());
        LocalDateTime now = LocalDateTime.now();
        if(user.isPresent()){
            LocalDateTime time = LocalDateTime.now().plusMinutes(15);
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            String code = String.format("%06d",number);
            System.out.println("code: " + code);
            userRepository.updateCode(code,time,email);
            String subject = "Verification Code";
            String senderName = "Lotus Nail and Spa";
            String mailContent = "<p>Hello, this is the code: " + code + ". It will be expired after 15 minutes.</p>";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("4businessoffice@gmail.com", senderName);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(mailContent,true);
            mailSender.send(message);
            return true;
        }
        return false;
    }

    public Long idGeneration(){
        Random rnd = new Random();
        return rnd.nextLong(999999);
    }
    @Override
    public Optional<User> findUserByEmail(User user){
        return userRepository.findByEmail(user.getEmail().toLowerCase());
    }

}
