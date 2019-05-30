package com.al.botgether.controller;

import com.al.botgether.dto.UserDto;
import com.al.botgether.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final HttpHeaders headers;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable("id") String userId) {
        UserDto userDto = userService.getById(userId);
        if (userDto == null) { // Not found
            return ResponseEntity.notFound().headers(headers).build();
        } else {
            return ResponseEntity.ok().headers(headers).body(userDto);
        }
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDto userDto) {
        UserDto createdUserDto = userService.saveUser(userDto);
        return ResponseEntity.created(URI.create("/users/" + userDto.getId()))
                .headers(headers)
                .body(createdUserDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }
}
