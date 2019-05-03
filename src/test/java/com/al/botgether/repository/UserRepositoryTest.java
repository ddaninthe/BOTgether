package com.al.botgether.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        statements = {
                "insert into User (id, username, discriminator, email) values ('0123456789', 'JDoe', '9182', null)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)

@Sql(
        statements = {
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
        assertThat(userRepository.findById("0123456789")).isNotNull();
    }

    @Test
    public void should_find_one_user_by_username() {
        assertThat(userRepository.findByUsername("JDoe")).hasSize(1);
    }
}
