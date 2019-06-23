package com.al.botgether.client;

import com.google.gson.JsonParser;
import lombok.Getter;
import org.springframework.web.client.RestClientResponseException;

@Getter
public class HttpStatus {
    private final int value;
    private String errorMessage;

    HttpStatus(int value) {
        this.value = value;
    }

    HttpStatus(int value, RestClientResponseException exception) {
        this.value = value;
        try {
            errorMessage = new JsonParser()
                    .parse(exception.getResponseBodyAsString())
                    .getAsJsonObject()
                    .get("message").getAsString();
        } catch (IllegalStateException e) {
            errorMessage = exception.getMessage();
        }
    }

    public boolean is2xxSuccessful() {
        return value >= 200 && value < 300;
    }

    public boolean is5xxServerError() {
        return value >= 500;
    }

    public boolean is4xxClientError() {
        return value >= 400 && value < 500;
    }
}
