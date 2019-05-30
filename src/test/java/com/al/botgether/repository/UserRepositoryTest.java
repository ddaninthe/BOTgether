package com.al.botgether.repository;

import com.al.botgether.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        statements = {
                "insert into User (id, username, discriminator, email) values ('0123456789', 'JDoe', '9182', null)",
                "insert into Event (id, title, description, event_date, creator) values (123456789, 'Test Event', 'This a normal event', null, '0123456789')"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)

@Sql(
        statements = {
                "delete from Event where id = 123456789",
                "delete from User where id = '0123456789'"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void should_find_user_by_id() {
        Optional<User> optionalUser  = userRepository.findById("0123456789");
        assertThat(optionalUser.isPresent()).isTrue();
        User user = optionalUser.get();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("0123456789");
        assertThat(user.getUsername()).isEqualTo("JDoe");
        assertThat(user.getCreatedEvents()).hasSize(1);
    }

    @Test
    public void should_find_one_user_by_username() {
        assertThat(userRepository.findByUsername("JDoe")).hasSize(1);
    }
}
