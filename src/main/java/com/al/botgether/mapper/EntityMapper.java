package com.al.botgether.mapper;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.dto.EventDto;
import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
@SuppressWarnings("squid:S1214") // Suppress Sonar warning
public interface EntityMapper {
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    EntityMapper instance = Mappers.getMapper(EntityMapper.class);

    UserDto userToUserDto(User user);
    @Mapping(target = "createdEvents", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    User userDtoToUser(UserDto user);

    @Mapping(source ="creator", target = "creatorDto")
    EventDto eventToEventDto(Event event);
    @Mapping(source ="creatorDto", target = "creator")
    @Mapping(target = "availabilities", ignore = true)
    Event eventDtoToEvent(EventDto event);

    default AvailabilityDto availabilityToAvailabilityDto(Availability availability) {
        AvailabilityDto dto = new AvailabilityDto();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        dto.setAvailabilityDate(sdf.format(availability.getAvailabilityDate()));

        if (availability.getUser() != null) {
            dto.setUserDto(userToUserDto(availability.getUser()));
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(availability.getId().getUserId());
            dto.setUserDto(userDto);
        }

        if (availability.getEvent() != null) {
            dto.setEventDto(eventToEventDto(availability.getEvent()));
        } else {
            EventDto eventDto = new EventDto();
            eventDto.setId(availability.getId().getEventId());
            dto.setEventDto(eventDto);
        }

        return dto;
    }
    List<AvailabilityDto> availabilitiesToAvailabilityDtos(List<Availability> availabilities);

    default Availability availabilityDtoToAvailability(AvailabilityDto availabilityDto) {
        Availability availability = new Availability();
        Event event = eventDtoToEvent(availabilityDto.getEventDto());
        User user = userDtoToUser(availabilityDto.getUserDto());

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date;
        try {
            date = sdf.parse(availabilityDto.getAvailabilityDate());
        } catch (ParseException e) {
            date = null;
            LoggerFactory.getLogger(EntityMapper.class).error("Error parsing date", e);
        }

        availability.setId(new AvailabilityKey(user.getId(), event.getId(), date));
        availability.setEvent(event);
        availability.setUser(user);

        return availability;
    }
}
