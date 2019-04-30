package com.al.botgether.mapper;

import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
@SuppressWarnings("squid:S1214") // Suppress Sonar warning
public interface EntityMapper {

    EntityMapper instance = Mappers.getMapper(EntityMapper.class);

    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto user);
}
