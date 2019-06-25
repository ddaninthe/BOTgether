package com.al.botgether.controller;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.mapper.EntityMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql(
        statements = {
                "insert into User (id, username, discriminator) values ('0123456789', 'JDoe', '9182')",
                "insert into User (id, username, discriminator) values ('01234', 'User', '5623')",
                "insert into Event (id, title, description, event_date, creator) values (123456789, 'Test Event', 'This a normal event', null, '0123456789')",
                "insert into Event (id, title, description, event_date, creator) values (987654321, 'Another Event', 'This a test event', null, '0123456789')",
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
                "delete from Event where id = 987654321",
                "delete from Availability where event_id = 123456789"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@SuppressWarnings("squid:S2699") // Remove Sonar Warning for "no assertion"
public class AvailabilityControllerTest {
    @LocalServerPort
    private int port;

    private Date bestDate;

    @Before
    public void setup() throws ParseException {
        RestAssured.port = port;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        sdf.setTimeZone(DateUtil.UTC);
        bestDate = sdf.parse("2019-01-01 14");
    }

    @Test
    public void should_return_best_date() {
        Date response =
        when()
            .get("/availabilities/best/123456789")
        .then()
            .statusCode(200)
            .extract().body().as(Date.class);

        assertThat(response.getTime()).isEqualTo(bestDate.getTime());
    }

    @Test
    public void should_return_best_not_found() {
        when()
            .get("/availabilities/best/987654321")
        .then()
            .statusCode(404);
    }

    @Test
    public void should_add_availability() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        AvailabilityKey key = new AvailabilityKey("0123456789", 123456789, calendar.getTime());
        Availability avail = new Availability();
        avail.setId(key);

        AvailabilityDto dto =
            given()
                .contentType(ContentType.JSON)
                .body(EntityMapper.instance.availabilityToAvailabilityDto(avail))
            .when()
                .post("/availabilities")
            .then()
                .statusCode(201)
                .extract().body().as(AvailabilityDto.class);

        SimpleDateFormat sdf = new SimpleDateFormat(EntityMapper.DATE_FORMAT);
        Date date = sdf.parse(dto.getAvailabilityDate());
        assertThat(dto).isNotNull();
        assertThat(date).isEqualTo(calendar.getTime());
        assertThat(dto.getEventDto().getId()).isEqualTo(123456789);
        assertThat(dto.getUserDto().getId()).isEqualTo("0123456789");
    }

    @Test
    public void should_not_add_availability_when_event_incorrect() {
        Date anotherDate = new Date();
        AvailabilityKey key = new AvailabilityKey("0123456789", 1234567890L, anotherDate);
        Availability avail = new Availability();
        avail.setId(key);

        given()
            .contentType(ContentType.JSON)
            .body(EntityMapper.instance.availabilityToAvailabilityDto(avail))
        .when()
            .post("/availabilities")
        .then()
            .statusCode(400);
    }

    @Test
    public void should_delete_availability() {
        AvailabilityKey key = new AvailabilityKey("0123456789", 123456789, bestDate);
        Availability availability = new Availability();
        availability.setId(key);

        given()
            .contentType(ContentType.JSON)
            .body(EntityMapper.instance.availabilityToAvailabilityDto(availability))
        .when()
            .delete("/availabilities")
        .then()
            .statusCode(204);
    }
}
