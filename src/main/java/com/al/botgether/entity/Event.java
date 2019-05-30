package com.al.botgether.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;
    @Column(name = "event_date")
    private Date eventDate;

    @ManyToOne
    @JoinColumn(name="creator", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "event")
    List<Availability> availabilities;
}
