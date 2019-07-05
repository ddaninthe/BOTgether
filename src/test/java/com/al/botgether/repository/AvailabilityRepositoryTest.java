package com.al.botgether.repository;

import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        statements = {
                "insert into User (id, username, discriminator) values ('0123456789', 'JDoe', '9182')",
                "insert into User (id, username, discriminator) values ('01234', 'User', '5623')",
                "insert into Event (id, title, description, event_date, creator) values (123456789, 'Test Event', 'This a normal event', null, '0123456789')",
                "insert into Availability (availability_date, event_id, user_id) values ('2019-01-01 13:00:00', 123456789, '0123456789')",
                "insert into Availability (availability_date, event_id, user_id) values ('2019-01-01 14:00:00', 123456789, '0123456789')",
                "insert into Availability (availability_date, event_id, user_id) values ('2019-01-01 14:00:00', 123456789, '01234')"
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
    @Autowired
    private EventService eventService;

    private Date bestDate;

    @Before
    public void setup() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        sdf.setTimeZone(DateUtil.UTC);
        bestDate = sdf.parse("2019-01-01 14");
    }

    @Test
    public void should_return_3_availabilities() {
        List<Availability> availabilities = availabilityRepository.getAvailabilitiesByEventId(123456789L);
        assertThat(availabilities).hasSize(3);
        assertThat(availabilities.get(0).getEvent().getId()).isEqualTo(123456789L);
    }

    @Test
    public void should_return_best_date() {
        Date date = availabilityRepository.getBestAvailabilityByEventId(123456789L);

        assertThat(date.getTime()).isEqualTo(bestDate.getTime());
    }

    @Test
    public void should_add_availability() {
        Date date = new Date();
        AvailabilityKey key = new AvailabilityKey("0123456789", 123456789, date);
        Availability a = new Availability();
        a.setId(key);
        Availability added = availabilityRepository.save(a);
        assertThat(added).isNotNull();
        assertThat(added.getAvailabilityDate().getTime()).isEqualTo(date.getTime());
        assertThat(added.getId().getUserId()).isEqualTo("0123456789");
        assertThat(added.getId().getEventId()).isEqualTo(123456789);
    }

    @Test
    public void should_delete_all_by_event_where_date_mismatch() {
        availabilityRepository.deleteAllByEventWhenDateMismatch(123456789, bestDate);

        List<Availability> availabilities = availabilityRepository.getAvailabilitiesByEventId(123456789);
        assertThat(availabilities).hasSize(2);

        assertThat(availabilities.get(0).getAvailabilityDate().getTime()).isEqualTo(bestDate.getTime());
    }

    @Test
    public void should_delete_an_availability() {
        AvailabilityKey key = new AvailabilityKey("01234", 123456789, bestDate);
        Availability availability = new Availability();
        availability.setId(key);
        List<Availability> firstList = availabilityRepository.getAvailabilitiesByUserId("01234");
        assertThat(firstList).hasSize(1);

        availabilityRepository.delete(availability);

        List<Availability> list = availabilityRepository.getAvailabilitiesByUserId("01234");
        assertThat(list).hasSize(0);
    }

    @Test
    public void should_get_today_availabilities() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR, 3);

        AvailabilityKey key = new AvailabilityKey("0123456789", 123456789, c.getTime());
        Availability a = new Availability();
        a.setId(key);
        availabilityRepository.save(a);

        EventDto eventDto = new EventDto();
        eventDto.setId(123456789);
        eventDto.setEventDate(c.getTime());
        eventService.updateEvent(eventDto);

        List<Availability> todays = availabilityRepository.getAllForToday();

        assertThat(todays).hasSize(1);
        Availability today = todays.get(0);
        assertThat(today.getId()).isNotNull();
        AvailabilityKey todayKey = today.getId();
        assertThat(today.getAvailabilityDate()).isEqualTo(c.getTime());
        assertThat(todayKey.getUserId()).isEqualTo("0123456789");
        assertThat(todayKey.getEventId()).isEqualTo(123456789);
    }
}
