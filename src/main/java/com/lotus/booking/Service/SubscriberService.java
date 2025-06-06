package com.lotus.booking.Service;

import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscriberService {
    @Autowired
    private SubscriberRepository subscriberRepository;
    public Subscriber saveSubscriber( Subscriber subscriber){
        try{
            Subscriber newSubscriber = new Subscriber();
            newSubscriber.setUser_id(subscriber.getUser_id());
            newSubscriber.setToken(subscriber.getToken());
            return subscriberRepository.save(newSubscriber);
        } catch (Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    public List<Subscriber> getAllSubscriber(){
        return subscriberRepository.findAll();
    }

    public List<Subscriber> findAllSubscriberById(Long user_id){
        return subscriberRepository.findAllByUserId(user_id);
    }

    public String deleteSubscriber(String token){
        Subscriber subscriber = subscriberRepository.findSubscriberByToken(token);
        Long id = subscriber.getId();
        subscriberRepository.deleteById(id);
        return "Success!";
    }
}
