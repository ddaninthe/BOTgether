package com.al.botgether.client;

import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Getter
public class HttpClient {
    private static final String BASE_URL = "http://localhost:";
    public final int port;
    
    private final HttpHeaders headers;
    private final RestTemplate rest;
    private HttpStatus status;

    public HttpClient() {
        this.rest = new RestTemplate();
        this.port = 8080;
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public HttpClient(int port) {
        this.rest = new RestTemplate();
        this.port = port;
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public String get(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + port + uri, HttpMethod.GET, requestEntity, String.class);
            status = new HttpStatus(responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            status = new HttpStatus(e.getRawStatusCode(), e);
            return null;
        }
    }

    public String post(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        try {
            ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + port + uri, HttpMethod.POST, requestEntity, String.class);
            status = new HttpStatus(responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            status = new HttpStatus(e.getRawStatusCode(), e);
            return null;
        }
    }

    public String put(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        try {
            ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + port + uri, HttpMethod.PUT, requestEntity, String.class);
            status = new HttpStatus(responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            status = new HttpStatus(e.getRawStatusCode(), e);
            return null;
        }
    }

    public void delete(String uri, @Nullable String json) {
        HttpEntity<String> requestEntity = json == null ? new HttpEntity<>(headers) : new HttpEntity<>(json, headers);
        try {
            ResponseEntity<Void> responseEntity = rest.exchange(BASE_URL + port + uri, HttpMethod.DELETE, requestEntity, Void.class);
            status = new HttpStatus(responseEntity.getStatusCodeValue());
        } catch (RestClientResponseException e) {
            status = new HttpStatus(e.getRawStatusCode(), e);
        }
    }
}
