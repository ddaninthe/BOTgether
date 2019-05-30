package com.al.botgether.repository;

import com.al.botgether.entity.Event;
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
                "insert into User (id, username, discriminator) values ('0123456789', 'JDoe', '9182')",
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
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    public void should_find_event_by_id() {
        assertThat(eventRepository.findById((long) 123456789)).isNotNull();
    }

    @Test
    public void should_rename_event_title() {
        eventRepository.updateEventTitle(123456789, "New Title");
        Optional<Event> eventOptional = eventRepository.findById((long) 123456789);
        assertThat(eventOptional.isPresent()).isTrue();
        eventOptional.ifPresent(event -> assertThat(event.getTitle()).isEqualTo("New Title"));
    }

    @Test
    public void should_modify_event_description() {
        eventRepository.updateEventDescription(123456789, "New Test description");
        Optional<Event> eventOptional = eventRepository.findById((long) 123456789);
        assertThat(eventOptional.isPresent()).isTrue();
        eventOptional.ifPresent(event -> assertThat(event.getDescription()).isEqualTo("New Test description"));
    }
}
