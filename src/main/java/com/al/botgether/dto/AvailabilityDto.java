package com.al.botgether.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvailabilityDto {
    UserDto userDto;
    EventDto eventDto;
    String availabilityDate;
}
