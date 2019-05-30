package com.al.botgether.repository;

import com.al.botgether.entity.Availability;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        statements = {
                "insert into User (id, username, discriminator, email) values ('0123456789', 'JDoe', '9182', null)",
                "insert into User (id, username, discriminator, email) values ('01234', 'User', '5623', null)",
                "insert into Event (id, title, description, event_date, creator) values (123456789, 'Test Event', 'This a normal event', null, '0123456789')"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)

@Sql(
        statements = {
                "delete from User where id = '0123456789'",
                "delete from User where id = '01234'",
                "delete from Event where id = 123456789",
                "delete from Availability where event_id = 123456789"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AvailabilityRepositoryTest {
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    public void should_return_best_date() {
        User user = new User("0123456789", "JDoe", "9182", null, new ArrayList<>(), new ArrayList<>());
        Event event = new Event(123456789, "Test Event", "This a normal event", null, user, new ArrayList<>());

        final Date date = DateUtils.truncate(new Date(), Calendar.SECOND);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Availability availability = new Availability(user, event, date);
        availabilityRepository.save(availability);

        user.setId("01234");
        Availability availability2 = new Availability(user, event, date);
        availabilityRepository.save(availability2);

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Availability availability3 = new Availability(user, event, calendar.getTime());
        availabilityRepository.save(availability3);

        Date bestDate = availabilityRepository.getBestAvailabilityByEventId(123456789);

        assertThat(bestDate.getTime()).isEqualTo(date.getTime());
    }
}
