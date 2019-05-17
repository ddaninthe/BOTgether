package com.al.botgether.repository;

import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, AvailabilityKey> {
    List<Availability> getAvailabilitiesByUserId(String userId);
    List<Availability> getAvailabilitiesByEventId(long eventId);

    @SuppressWarnings( {"SqlResolve", "SpringDataRepositoryMethodReturnTypeInspection"} )
    @Query(value = "select a.availability_date from Availability a where a.event_id = :event_id " +
            " group by a.availability_date " +
            " order by count(a.user_id) desc " +
            " limit 1",
            nativeQuery = true)
    Date getBestAvailabilityByEventId(@Param("event_id") long eventId);
}
