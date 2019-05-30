package com.al.botgether.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {
    private long id;
    private String title;
    private String description;
    @Column(name = "event_date")
    private Date eventDate;
    private UserDto creatorDto;
}
