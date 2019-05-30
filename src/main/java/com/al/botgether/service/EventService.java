package com.al.botgether.service;

import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Event;
import com.al.botgether.mapper.EntityMapper;
import com.al.botgether.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }



    @Transactional
    public EventDto getById(long id) {
        return eventRepository.findById(id)
                .map(EntityMapper.instance::eventToEventDto)
                .orElse(null);
    }

    @Transactional
    public EventDto saveEvent(EventDto eventDto) {
        Event event = EntityMapper.instance.eventDtoToEvent(eventDto);
        return EntityMapper.instance.eventToEventDto(eventRepository.save(event));
    }

    @Transactional
    public void updateEvent(EventDto eventDto) {
        // TODO: delete useless Availabilities
        if (eventDto.getTitle() != null) eventRepository.updateEventTitle(eventDto.getId(), eventDto.getTitle());
        if (eventDto.getDescription() != null) eventRepository.updateEventDescription(eventDto.getId(), eventDto.getDescription());
    }

    @Transactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }
}
