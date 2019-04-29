package com.al.botgether.service;

import com.al.botgether.entity.User;
import com.al.botgether.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User getById(String id){
        return userRepository.getOne(id);
    }
}
