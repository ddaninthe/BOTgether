package com.al.botgether.controller;

import com.al.botgether.dto.QuoteDto;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuoteControllerTest {
    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void should_get_quote_of_day() {
        QuoteDto quote =
            when()
                .get("/quotes")
            .then()
                .statusCode(200)
                .extract()
                .body().as(QuoteDto.class);

        assertThat(quote).isNotNull();
        assertThat(quote.getAuthor()).isNotEmpty();
        assertThat(quote.getQuote()).isNotEmpty();
    }
}
