package com.lotus.booking.Service;

import com.lotus.booking.Entity.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public User findUser(Long id);

    public List<User> findAllUser();
}
