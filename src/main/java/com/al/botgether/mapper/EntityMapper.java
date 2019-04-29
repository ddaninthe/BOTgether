package com.al.botgether.mapper;

import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EntityMapper {

    EntityMapper instance = Mappers.getMapper(EntityMapper.class);

    UserDto userToUserDto(User user);
}
