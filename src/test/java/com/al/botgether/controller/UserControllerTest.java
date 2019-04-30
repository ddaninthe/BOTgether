package com.al.botgether.controller;

import com.al.botgether.dto.UserDto;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
            .body("id", is(dto.getId()))
            .body("username", is(dto.getUsername()))
            .body("discriminator", is(dto.getDiscriminator()))
            .body("email", is(dto.getEmail()))
            .extract().header("Location");

        assertThat(location).contains("/users/" + userTestId);
    }
}
