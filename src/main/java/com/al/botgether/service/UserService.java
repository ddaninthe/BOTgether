package com.al.botgether.service;

import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.al.botgether.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<UserDto> getUserByUsername(String username){
        List<User> users = userRepository.findByUsername(username);
        List<UserDto> usersDto = new ArrayList<>(users.size());

        EntityMapper mapper = EntityMapper.instance;
        for (User user : users) {
            usersDto.add(mapper.userToUserDto(user));
        }

        return usersDto;
    }

    @Transactional
    public UserDto getById(String id){
        return userRepository.findById(id)
                .map(EntityMapper.instance::userToUserDto)
                .orElse(null);
    }

    @Transactional
    public UserDto saveUser(UserDto userDto){
        User user = EntityMapper.instance.userDtoToUser(userDto);
        return EntityMapper.instance.userToUserDto(userRepository.save(user));
    }
}
