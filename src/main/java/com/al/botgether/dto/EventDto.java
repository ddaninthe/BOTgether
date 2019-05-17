package com.al.botgether.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {
    private long id;
    private String title;
    private String description;
    private Date eventDate;
    private List<AvailabilityDto> availabilities;
}
