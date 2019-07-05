package com.al.botgether.repository;

import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("delete from Availability where event_id = :event_id and availability_date <> :date")
    void deleteAllByEventWhenDateMismatch(@Param("event_id") long eventId, @Param("date") Date date);

    @Query(value = "select a.* from Availability a INNER JOIN Event e on e.id = a.event_id " +
            "where e.event_date is not null " +
            "and e.event_date BETWEEN CURRENT_TIME and CURRENT_TIME + INTERVAL '1' DAY",
            nativeQuery = true)
    List<Availability> getAllForToday();
}
