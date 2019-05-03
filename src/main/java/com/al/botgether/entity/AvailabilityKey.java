package com.al.botgether.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AvailabilityKey implements Serializable {
    @Column(name = "user_id")
    String userId;

    @Column(name = "event_id")
    long eventId;

    @Column(name = "availability_date")
    Date availabilityDate;
}
