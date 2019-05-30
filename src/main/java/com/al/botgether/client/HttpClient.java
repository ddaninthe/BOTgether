package com.al.botgether.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

class HttpClient {
    private final static String BASE_URL = "http://localhost:8080";
    private RestTemplate rest;
    private HttpHeaders headers;
    private HttpStatus status;

    HttpClient() {
        this.rest = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    String get(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + uri, HttpMethod.GET, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    void post(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + uri, HttpMethod.POST, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    String put(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> responseEntity = rest.exchange(BASE_URL + uri, HttpMethod.PUT, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    void delete(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<Void> responseEntity = rest.exchange(BASE_URL + uri, HttpMethod.DELETE, requestEntity, Void.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    HttpStatus getStatus() {
        return status;
    }

    private void setStatus(HttpStatus status) {
        this.status = status;
    }
}
