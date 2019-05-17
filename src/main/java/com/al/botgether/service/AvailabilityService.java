package com.al.botgether.service;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.entity.Availability;
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

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
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
        Availability availability = EntityMapper.instance.availabilityDtoToAvailability(availabilityDto);
        return EntityMapper.instance.availabilityToAvailabilityDto(availabilityRepository.save(availability));
    }

    @Transactional
    public void deleteAvailability(AvailabilityDto availabilityDto) {
        Availability availability = EntityMapper.instance.availabilityDtoToAvailability(availabilityDto);
        availabilityRepository.delete(availability);
    }
}
