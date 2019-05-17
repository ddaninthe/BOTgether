package com.al.botgether.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String discriminator;
    private String email;
    private List<AvailabilityDto> availabilities;
}
