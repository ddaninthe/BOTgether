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
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static groovy.json.JsonOutput.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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
        dto.setEmail("john@test.com");

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
            .body("email", is("john@test.com"))
            .extract().header("Location");

        assertThat(location).contains("/users/" + userTestId);
    }

    @Test
    public void should_return_user() {
        User user = new User(userTestId, "John", "0123", "john@test.com");
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
        assertThat(userGet.getEmail()).isEqualTo("john@test.com");
    }
}
