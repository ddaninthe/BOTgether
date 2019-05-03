package com.al.botgether.controller;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@RestController
@RequestMapping(value = "/availabilities")
public class AvailabilityController {
    private final AvailabilityService availabilityService;
    private final HttpHeaders headers;

    @Autowired
    AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @PostMapping
    public ResponseEntity createAvailability(@RequestBody AvailabilityDto availabilityDto) {
        try {
            AvailabilityDto dto = availabilityService.saveAvailability(availabilityDto);
            return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>("error: " + e.getMessage(), headers, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/best/{eventId}")
    public ResponseEntity getBestAvailability(@PathVariable("eventId") long id) {
        Date bestDate = availabilityService.getBestAvailability(id);
        return ResponseEntity.ok()
                .headers(headers)
                .body(bestDate);
    }

    @DeleteMapping
    public ResponseEntity deleteAvailability(@RequestBody AvailabilityDto availabilityDto) {
        availabilityService.deleteAvailability(availabilityDto);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }
}
