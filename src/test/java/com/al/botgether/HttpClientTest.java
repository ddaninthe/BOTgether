package com.al.botgether;


import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class HttpClientTest {
    @LocalServerPort
    private int port;
    private HttpClient client;

    @Before
    public void setup() {
        HttpClient.port = port;
        client = new HttpClient();
    }

    @Test
    public void should_have_two_headers() {
        assertThat(client.getHeaders()).hasSize(2);
        assertThat(client.getRest()).isNotNull();
    }

    @Test
    public void should_return_404() {
        client.get("/events/314354676");
        HttpStatus status = client.getStatus();
        assertThat(status.getValue()).isEqualTo(404);
    }
}
