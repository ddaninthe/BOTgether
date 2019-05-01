package com.al.botgether.repository;

import com.al.botgether.dto.EventDto;
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
                "insert into Event (id, title, description, eventDate) values (123456789, 'Test Event', 'This a normal event', null)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)

@Sql(
        statements = {
                "delete from Event"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    public void should_find_event_by_id() {
        assertThat(eventRepository.findById((long) 123456789)).isNotNull();
    }
}
