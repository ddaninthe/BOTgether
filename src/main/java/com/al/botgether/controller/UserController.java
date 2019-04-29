package com.al.botgether.controller;

import com.al.botgether.dto.UserDto;
import com.al.botgether.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity getUser(@PathVariable("id") String userId) {
        if (userId.matches("^\\d+$")) {
            UserDto userDto = userService.getById(userId);
            if (userDto == null) { // Not found
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(userDto);
            }
        } else {
            List<UserDto> usersDto = userService.getUserByUsername(userId);
            if (usersDto.isEmpty()) { // Not found
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(usersDto.get(0));
            }
        }
    }
}
