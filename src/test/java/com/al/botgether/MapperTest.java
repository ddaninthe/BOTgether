package com.al.botgether;

import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {
    @Test
    public void should_map_user_to_dto() {
        User user = new User("0123456789", "John", "2703", "john@spring.com");

        UserDto userDto = EntityMapper.instance.userToUserDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo("0123456789");
        assertThat(userDto.getUsername()).isEqualTo("John");
        assertThat(userDto.getDiscriminator()).isEqualTo("2703");
        assertThat(userDto.getEmail()).isEqualTo("john@spring.com");
    }

    @Test
    public void should_map_dto_to_user() {
        UserDto userDto = new UserDto();
        userDto.setId("0123456789");
        userDto.setUsername("John");
        userDto.setDiscriminator("2703");
        userDto.setEmail("jonh@spring.com");

        User user = EntityMapper.instance.userDtoToUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("0123456789");
        assertThat(user.getUsername()).isEqualTo("John");
        assertThat(user.getDiscriminator()).isEqualTo("2703");
        assertThat(user.getEmail()).isEqualTo("jonh@spring.com");
    }
}
