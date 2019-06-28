package com.al.botgether.controller;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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

    @GetMapping
    public ResponseEntity getAllForToday() {
        List<AvailabilityDto> dtos = availabilityService.getAllForToday();
        return ResponseEntity.ok()
                .headers(headers)
                .body(dtos);
    }

    @GetMapping("/best/{event_id}")
    public ResponseEntity getBestAvailability(@PathVariable("event_id") long id) {
        Date bestDate = availabilityService.getBestAvailability(id);
        if (bestDate != null) {
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bestDate);
        } else {
            return ResponseEntity.notFound()
                    .headers(headers).build();
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity getAvailabilitiesByUser(@PathVariable("user_id") String userId) {
        List<AvailabilityDto> availabilities = availabilityService.getAllByUserId(userId);

        return ResponseEntity.ok()
                .headers(headers)
                .body(availabilities);
    }

    @PostMapping
    public ResponseEntity createAvailability(@RequestBody AvailabilityDto availabilityDto) {
        AvailabilityDto dto = availabilityService.saveAvailability(availabilityDto);
        if (dto != null) {
            return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest()
                    .headers(headers)
                    .build();
        }
    }

    @DeleteMapping
    public ResponseEntity deleteAvailability(@RequestBody AvailabilityDto availabilityDto) {
        availabilityService.deleteAvailability(availabilityDto);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }
}
