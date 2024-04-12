package com.lotus.booking.Service;

import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {
    @Autowired
    private SubscriberRepository subscriberRepository;
    public Subscriber saveSubscriber( Subscriber subscriber){
        Subscriber newSubscriber = new Subscriber();
        newSubscriber.setPlatform(subscriber.getPlatform());
        newSubscriber.setToken(subscriber.getToken());
        return subscriberRepository.save(newSubscriber);
    }
}