package com.al.botgether.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Availability implements Serializable {
    @EmbeddedId
    private AvailabilityKey id;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @MapsId("event_id")
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    public Availability(User user, Event event, Date date) {
        this.user = user;
        this.event = event;
        this.id = new AvailabilityKey(user.getId(), event.getId(), date);
    }

    public Date getAvailabilityDate() {
        return this.id.getAvailabilityDate();
    }
}
