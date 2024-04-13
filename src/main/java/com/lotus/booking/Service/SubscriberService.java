package com.lotus.booking.Service;

import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {
    @Autowired
    private SubscriberRepository subscriberRepository;
    public Subscriber saveSubscriber( Subscriber subscriber){
        try{
            Subscriber newSubscriber = new Subscriber();
            newSubscriber.setPlatform(subscriber.getPlatform());
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
}
