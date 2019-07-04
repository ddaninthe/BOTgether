package com.al.botgether.controller;

import com.al.botgether.dto.UserDto;
import com.al.botgether.entity.User;
import com.al.botgether.repository.UserRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static groovy.json.JsonOutput.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Sql(
        statements = {
                "delete from User where id = '102938'"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@SuppressWarnings("squid:S2699") // Remove Sonar Warning for "no assertion"
public class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private String userTestId;

    @Before
    public void setup() {
        RestAssured.port = port;
        userTestId = "102938";
    }

    @Test
    public void should_return_not_found() {
        when()
            .get("/users/{id}", "02")
        .then()
            .statusCode(404);
    }

    @Test
    public void should_create_user() {
        UserDto dto = new UserDto();
        dto.setId(userTestId);
        dto.setUsername("John");
        dto.setDiscriminator("0123");

        String location =
        given()
            .contentType(ContentType.JSON)
            .body(toJson(dto))
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("id", is(userTestId))
            .body("username", is("John"))
            .body("discriminator", is("0123"))
            .extract().header("Location");

        assertThat(location).contains("/users/" + userTestId);
    }

    @Test
    public void should_return_user() {
        User user = new User(userTestId, "John", "0123", new ArrayList<>(), new ArrayList<>());
        userRepository.save(user);

        UserDto userGet =
        when()
            .get("/users/" + userTestId)
        .then()
            .statusCode(200)
            .extract().body().as(UserDto.class);

        assertThat(userGet.getId()).isEqualTo(userTestId);
        assertThat(userGet.getUsername()).isEqualTo("John");
        assertThat(userGet.getDiscriminator()).isEqualTo("0123");
    }

    @Test
    public void should_delete_user() {
        User user = new User(userTestId, "John", "0123", new ArrayList<>(), new ArrayList<>());
        userRepository.save(user);

        when()
            .delete("/users/" + userTestId)
        .then()
            .statusCode(204);
    }
}
