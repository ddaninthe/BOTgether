package com.al.botgether.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AvailabilityDto {
    UserDto userDto;
    EventDto eventDto;
    Date availabilityDate;
}
