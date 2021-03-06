package com.al.botgether.controller;

import com.al.botgether.dto.EventDto;
import com.al.botgether.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final HttpHeaders headers;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable("id") long eventId) {
        EventDto eventDto = eventService.getById(eventId);
        if (eventDto == null) {
            return ResponseEntity.notFound().headers(headers).build();
        } else {
            return ResponseEntity.ok().headers(headers).body(eventDto);
        }
    }

    @GetMapping("/agenda/{user_id}")
    public ResponseEntity getWeekAgenda(@PathVariable("user_id") String userId) {
        List<EventDto> eventDtos = eventService.getWeekAgenda(userId);
        if (eventDtos.isEmpty()) {
            return ResponseEntity.notFound().headers(headers).build();
        } else {
            return ResponseEntity.ok().headers(headers).body(eventDtos);
        }
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
        EventDto newEvent = eventService.saveEvent(eventDto);
        return ResponseEntity.created(URI.create("/events/" + newEvent.getId()))
                .headers(headers)
                .body(newEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable("id") long eventId, @RequestBody EventDto eventDto) {
        eventDto.setId(eventId);
        eventService.updateEvent(eventDto);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

    @PutMapping("/{id}/best")
    public ResponseEntity setEventDate(@PathVariable("id") long eventId) {
        Date date = eventService.setEventDate(eventId);
        if (date != null) {
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(date);
        } else {
            return ResponseEntity.notFound()
                    .headers(headers)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }
}
