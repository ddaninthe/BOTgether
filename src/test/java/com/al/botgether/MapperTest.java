package com.al.botgether;

import com.al.botgether.dto.EventDto;
import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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

    @Test
    public void should_map_event_to_dto() {
        Date date = new Date();

        Event event = new Event(123456789, "First Event", "This is a random event", date);

        EventDto eventDto = EntityMapper.instance.eventToEventDto(event);

        assertThat(eventDto).isNotNull();
        assertThat(eventDto.getId()).isEqualTo(123456789);
        assertThat(eventDto.getTitle()).isEqualTo("First Event");
        assertThat(eventDto.getDescription()).isEqualTo("This is a random event");
        assertThat(eventDto.getEventDate()).isEqualTo(date);
    }

    @Test
    public void should_map_dto_to_event() {
        EventDto eventDto = new EventDto();
        eventDto.setId(123456789);
        eventDto.setTitle("Some Event");
        eventDto.setDescription("This is a random event");

        Event event = EntityMapper.instance.eventDtoToEvent(eventDto);

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(123456789);
        assertThat(event.getTitle()).isEqualTo("Some Event");
        assertThat(event.getDescription()).isEqualTo("This is a random event");
        assertThat(event.getEventDate()).isNull();
    }
}
