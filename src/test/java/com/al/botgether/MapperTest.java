package com.al.botgether;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.dto.EventDto;
import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTest {
    private static UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId("0123456789");
        userDto.setUsername("John");
        userDto.setDiscriminator("2703");
        return userDto;
    }

    @Test
    public void should_map_user_to_dto() {
        User user = new User("0123456789", "John", "2703", new ArrayList<>(), new ArrayList<>());

        UserDto userDto = EntityMapper.instance.userToUserDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo("0123456789");
        assertThat(userDto.getUsername()).isEqualTo("John");
        assertThat(userDto.getDiscriminator()).isEqualTo("2703");
    }

    @Test
    public void should_map_dto_to_user() {
        UserDto userDto = createUserDto();

        User user = EntityMapper.instance.userDtoToUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("0123456789");
        assertThat(user.getUsername()).isEqualTo("John");
        assertThat(user.getDiscriminator()).isEqualTo("2703");
        assertThat(user.getAvailabilities()).isNull();
    }

    @Test
    public void should_map_event_to_dto() {
        Date date = new Date();
        User user = new User("0123456789", "John", "2703", new ArrayList<>(), new ArrayList<>());
        Event event = new Event(123456789, "First Event", "This is a random event", date, user, new ArrayList<>());

        EventDto eventDto = EntityMapper.instance.eventToEventDto(event);

        assertThat(eventDto).isNotNull();
        assertThat(eventDto.getId()).isEqualTo(123456789);
        assertThat(eventDto.getTitle()).isEqualTo("First Event");
        assertThat(eventDto.getDescription()).isEqualTo("This is a random event");
        assertThat(eventDto.getEventDate()).isEqualTo(date);
        assertThat(eventDto.getCreatorDto().getId()).isEqualTo("0123456789");
    }

    @Test
    public void should_map_dto_to_event() {
        EventDto eventDto = new EventDto();
        eventDto.setId(123456789);
        eventDto.setTitle("Some Event");
        eventDto.setDescription("This is a random event");
        eventDto.setCreatorDto(createUserDto());

        Event event = EntityMapper.instance.eventDtoToEvent(eventDto);

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(123456789);
        assertThat(event.getTitle()).isEqualTo("Some Event");
        assertThat(event.getDescription()).isEqualTo("This is a random event");
        assertThat(event.getEventDate()).isNull();
        assertThat(event.getAvailabilities()).isNull();
    }

    @Test
    public void should_map_availability_to_dto() {
        User user = new User("123456789", "Avail", "0123", new ArrayList<>(), new ArrayList<>());
        Event event = new Event(124313, "Title", "Some nice description", null, user, new ArrayList<>());
        Date date = new Date();
        Availability availability = new Availability(user, event, date);

        AvailabilityDto dto = EntityMapper.instance.availabilityToAvailabilityDto(availability);

        assertThat(dto).isNotNull();
        assertThat(dto.getAvailabilityDate()).isEqualTo(new SimpleDateFormat(EntityMapper.DATE_FORMAT).format(date));
        assertThat(dto.getEventDto().getId()).isEqualTo(124313);
        assertThat(dto.getUserDto().getId()).isEqualTo("123456789");
    }

    @Test
    public void should_map_availability_to_dto_when_only_key() {
        Date date = new Date();
        AvailabilityKey key = new AvailabilityKey("0123456789", 123456789, date);
        Availability a = new Availability();
        a.setId(key);

        AvailabilityDto dto = EntityMapper.instance.availabilityToAvailabilityDto(a);
        assertThat(dto).isNotNull();
        assertThat(dto.getAvailabilityDate()).isEqualTo(new SimpleDateFormat(EntityMapper.DATE_FORMAT).format(date));
        assertThat(dto.getUserDto().getId()).isEqualTo("0123456789");
        assertThat(dto.getEventDto().getId()).isEqualTo(123456789);
    }

    @Test
    public void should_map_dto_to_availability() {
        UserDto userDto = createUserDto();

        EventDto eventDto = new EventDto();
        eventDto.setId(123456789);
        eventDto.setTitle("Some Event");
        eventDto.setDescription("This is a random event");

        final Date date = new Date();
        AvailabilityDto dto = new AvailabilityDto();
        dto.setEventDto(eventDto);
        dto.setUserDto(userDto);
        dto.setAvailabilityDate(new SimpleDateFormat(EntityMapper.DATE_FORMAT).format(date));

        Availability availability = EntityMapper.instance.availabilityDtoToAvailability(dto);
        assertThat(availability).isNotNull();
        assertThat(availability.getId().getEventId()).isEqualTo(123456789);
        assertThat(availability.getId().getUserId()).isEqualTo("0123456789");

        Event event = availability.getEvent();
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(123456789);
        assertThat(event.getTitle()).isEqualTo("Some Event");
        assertThat(event.getDescription()).isEqualTo("This is a random event");
        assertThat(event.getEventDate()).isNull();

        User user = availability.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("0123456789");
        assertThat(user.getUsername()).isEqualTo("John");
        assertThat(user.getDiscriminator()).isEqualTo("2703");
    }

    @Test
    public void should_map_availabilities_to_dto_list() {
        List<Availability> availabilities = new ArrayList<>();

        User user = new User("123456789", "Avail", "0123", new ArrayList<>(), new ArrayList<>());
        Event event = new Event(124313, "Title", "Some nice description", null, user, new ArrayList<>());
        Date date = new Date();

        for (int i = 0; i < 5; i++) {
            availabilities.add(new Availability(user, event, date));
        }

        List<AvailabilityDto> dtos = EntityMapper.instance.availabilitiesToAvailabilityDtos(availabilities);

        assertThat(dtos).hasSize(5);
    }
}
