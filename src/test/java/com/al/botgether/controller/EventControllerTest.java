package com.al.botgether.controller;


import com.al.botgether.dto.EventDto;
import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.entity.Event;
import com.al.botgether.mapper.EntityMapper;
import com.al.botgether.repository.AvailabilityRepository;
import com.al.botgether.repository.EventRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static groovy.json.JsonOutput.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql(
        statements = {
                "insert into User (id, username, discriminator) values ('0123456789', 'JDoe', '9182')",
                "insert into Event (id, title, description, creator, event_date) values (9876543210, 'Test Event Before', 'A Before Event', '0123456789', null)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        statements = {
                "delete from Event where id = 9876543210",
                "delete from User where id = '0123456789'"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@SuppressWarnings("squid:S2699") // Remove Sonar Warning for "no assertion"
public class EventControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void should_return_not_found() {
        when()
            .get("/event/{id}", 129193478)
        .then()
            .statusCode(404);
    }

    private EventDto createEventDto() {
        UserDto creator = new UserDto();
        creator.setId("0123456789");
        creator.setDiscriminator("9182");
        creator.setUsername("JDoe");

        EventDto dto = new EventDto();
        dto.setTitle("Another Test Event");
        dto.setDescription("Let's dance");
        dto.setCreatorDto(creator);
        dto.setEventDate(DateUtils.truncate(new Date(), Calendar.SECOND));

        return dto;
    }

    @Test
    public void should_create_event() {
        EventDto dto = createEventDto();

        EventDto createdEvent =
                given()
                    .contentType(ContentType.JSON)
                    .body(toJson(dto))
                .when()
                    .post("/events")
                .then()
                    .statusCode(201)
                    .header("Location", containsString("/events/"))
                    .extract().body().as(EventDto.class);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getTitle()).isEqualTo("Another Test Event");
        assertThat(createdEvent.getDescription()).isEqualTo("Let's dance");
        assertThat(createdEvent.getEventDate()).isEqualTo(dto.getEventDate());

        eventRepository.deleteById(createdEvent.getId());
    }

    @Test
    public void should_find_event_by_id() {
        EventDto result =
            when()
                .get("/events/9876543210")
            .then()
                .statusCode(200)
                .extract().body().as(EventDto.class);

        assertThat(result.getId()).isEqualTo(9876543210L);
        assertThat(result.getTitle()).isEqualTo("Test Event Before");
        assertThat(result.getDescription()).isEqualTo("A Before Event");
        assertThat(result.getCreatorDto().getId()).isEqualTo("0123456789");
        assertThat(result.getEventDate()).isNull();
    }

    @Test
    public void should_update_event_title() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"title\": \"Test Rename\"}")
        .when()
            .put("/events/9876543210")
        .then()
            .statusCode(204);

        Optional<Event> event = eventRepository.findById(9876543210L);
        assertThat(event.isPresent()).isTrue();
        event.ifPresent(value -> assertThat(value.getTitle()).isEqualTo("Test Rename"));
    }

    @Test
    public void should_update_event_description() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"description\": \"Another description\"}")
        .when()
            .put("/events/9876543210")
        .then()
            .statusCode(204);

        Optional<Event> event = eventRepository.findById(9876543210L);
        assertThat(event.isPresent()).isTrue();
        event.ifPresent(value -> assertThat(value.getDescription()).isEqualTo("Another description"));
    }

    @Test
    public void should_delete_event() {
        when()
            .delete("/events/9876543210")
        .then()
            .statusCode(204);
    }

    @Test
    public void should_return_agenda() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, 1);

        EventDto dto = createEventDto();
        dto.setEventDate(calendar.getTime());
        Event savedEvent = eventRepository.save(EntityMapper.instance.eventDtoToEvent(dto));

        AvailabilityKey key = new AvailabilityKey("0123456789", savedEvent.getId(), calendar.getTime());
        Availability availability = new Availability();
        availability.setId(key);

        availabilityRepository.save(availability);

        List<EventDto> dtos = Arrays.asList(
            when()
                .get("/events/agenda/0123456789")
            .then()
                .statusCode(200)
                .extract().body().as(EventDto[].class));

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(1);
        EventDto eventDto = dtos.get(0);
        assertThat(eventDto.getId()).isEqualTo(savedEvent.getId());
        assertThat(eventDto.getTitle()).isEqualTo("Another Test Event");
        assertThat(eventDto.getDescription()).isEqualTo("Let's dance");
        assertThat(eventDto.getEventDate().getTime()).isEqualTo(calendar.getTime().getTime());

        availabilityRepository.deleteById(availability.getId());
        eventRepository.deleteById(savedEvent.getId());
    }
}
