package com.al.botgether.client;

import com.google.gson.JsonParser;
import lombok.Getter;

@Getter
public class HttpStatus {
    private final int value;
    private String errorMessage;

    HttpStatus(int value) {
        this.value = value;
    }

    HttpStatus(int value, String responseBody) {
        this.value = value;
        errorMessage = new JsonParser()
                .parse(responseBody)
                .getAsJsonObject()
                .get("message").getAsString(); // TODO: change this field
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
