package com.al.botgether.controller;

import com.al.botgether.entity.User;
import com.al.botgether.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String userId) {
        if (userId.matches("^\\d+$")) {
            return userService.getById(userId);
        } else {
            List<User> users = userService.getUserByUsername(userId);
            if (users.size() == 0) {
                // Not found
                return null;
            } else {
                return users.get(0);
            }
        }
    }
}
