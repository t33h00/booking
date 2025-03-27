package com.lotus.booking.Service;

import com.lotus.booking.DTO.VerifyRequest;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Entity.Verify;
import com.lotus.booking.Repository.VerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VerifyServiceImpl {
    @Autowired
    private VerifyRepository verifyRepository;

    @Autowired
    private UserServiceImpl user;

    public void saveVerify(VerifyRequest verify) {
        Verify verify1 = verifyRepository.findByUserIdAndDate(verify.getUser_id(), verify.getDate());
        User newUser = user.findUser(verify.getUser_id());
        if(verify1 == null) {
            Verify newVerify = new Verify();
            newVerify.setDate(verify.getDate());
            newVerify.setVerified(verify.isVerified());
            newVerify.setUser(newUser);
            verifyRepository.save(newVerify);
            System.out.println("Verify added.");
        }
        System.out.println("There was Verify saved for this date.");
    }

    public void updateVerify(Long user_id, Verify verify) {
        Verify newVerify = verifyRepository.findById(verify.getId()).get();
        newVerify.setDate(verify.getDate());
        newVerify.setVerified(verify.isVerified());
        newVerify.setUser(user.findUser(user_id));
        verifyRepository.save(newVerify);

        System.out.println("Verify updated.");
    }

    public Verify getVerify(Long id, String date) {
        Verify newVerify = verifyRepository.findByUserIdAndDate(id,date);
        System.out.println("Verify retrieved.");
        return newVerify;
    }

    public List<Verify> getVerifyByMonth(Long user_id, String date){
        return verifyRepository.getVerifyByMonth(user_id, date);
    }
}
