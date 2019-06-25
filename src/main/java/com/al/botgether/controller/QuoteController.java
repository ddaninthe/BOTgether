package com.al.botgether.controller;

import com.al.botgether.dto.QuoteDto;
import com.google.gson.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This controller use Quotes REST API.
 * https://quotes.rest/#!/qod/get_qod
 *
 * It returns the quote of the day.
 */
@RestController
@RequestMapping("/quotes")
public class QuoteController {

    private static final String QUOTE_API = "http://quotes.rest/qod";

    private HttpHeaders headers;
    private final URL url;

    public QuoteController() throws MalformedURLException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        url = new URL(QUOTE_API);
    }

    @GetMapping
    public ResponseEntity getQuote() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.toString());
            connection.setConnectTimeout(10000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            Gson gson = new Gson();
            QuoteDto quote = gson.fromJson(gson.fromJson(content.toString(), JsonObject.class)
                    .getAsJsonObject("contents")
                    .getAsJsonArray("quotes")
                    .get(0), QuoteDto.class);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(quote);

        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .headers(headers)
                    .body(e.getMessage());
        }
    }
}
