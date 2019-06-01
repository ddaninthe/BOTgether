package com.al.botgether.service;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.mapper.EntityMapper;
import com.al.botgether.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final EventService eventService;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository, EventService eventService) {
        this.availabilityRepository = availabilityRepository;
        this.eventService = eventService;
    }

    @Transactional
    public List<AvailabilityDto> getAllByUserId(String userId) {
        return EntityMapper.instance
                .availabilitiesToAvailabilityDtos(availabilityRepository.getAvailabilitiesByUserId(userId));
    }

    @Transactional
    public List<AvailabilityDto> getAllByEventId(long eventId) {
        return EntityMapper.instance
                .availabilitiesToAvailabilityDtos(availabilityRepository.getAvailabilitiesByEventId(eventId));
    }

    @Transactional
    public Date getBestAvailability(long eventId) {
        return availabilityRepository.getBestAvailabilityByEventId(eventId);
    }

    @Transactional
    public AvailabilityDto saveAvailability(AvailabilityDto availabilityDto) {
        EventDto e = eventService.getById(availabilityDto.getEventDto().getId());
        if (e != null) {
            Availability availability = EntityMapper.instance.availabilityDtoToAvailability(availabilityDto);
            return EntityMapper.instance.availabilityToAvailabilityDto(availabilityRepository.save(availability));
        }
        else {
            return null;
        }
    }

    @Transactional
    public void deleteAvailability(AvailabilityKey availabilityKey) {
        Availability availability = new Availability();
        availability.setId(availabilityKey);
        availabilityRepository.delete(availability);
    }
}
