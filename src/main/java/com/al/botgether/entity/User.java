package com.al.botgether.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User implements Serializable {

    @Id
    private String id;
    private String username;
    private String discriminator;
    private String email;

    public User(String id, String username, String discriminator, @Nullable String email) {
        this.id = id;
        this.username = username;
        this.discriminator = discriminator;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User: " + id + ", " + username + "#" + discriminator + ", " + email;
    }
}
