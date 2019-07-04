package com.al.botgether.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
    @Id
    private String id;
    private String username;
    private String discriminator;

    @OneToMany(mappedBy = "creator")
    private List<Event> createdEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Availability> availabilities;
}
