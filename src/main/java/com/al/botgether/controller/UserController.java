package com.al.botgether.controller;

import com.al.botgether.dto.UserDto;
import com.al.botgether.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable("id") String userId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if (userId.matches("^\\d+$")) {
            UserDto userDto = userService.getById(userId);
            if (userDto == null) { // Not found
                return ResponseEntity.notFound().headers(headers).build();
            } else {
                return ResponseEntity.ok().headers(headers).body(userDto);
            }
        } else {
            List<UserDto> usersDto = userService.getUserByUsername(userId);
            if (usersDto.isEmpty()) { // Not found
                return ResponseEntity.notFound().headers(headers).build();
            } else {
                return ResponseEntity.ok().headers(headers).body(usersDto.get(0));
            }
        }
    }
}
