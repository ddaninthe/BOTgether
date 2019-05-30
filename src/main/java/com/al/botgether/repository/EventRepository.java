package com.al.botgether.repository;

import com.al.botgether.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("Update Event Set title = :new_title Where id = :id")
    void updateEventTitle(@Param("id") long id, @Param("new_title") String title);

    @Modifying
    @Query("Update Event Set description = :new_description Where id = :id")
    void updateEventDescription(@Param("id") long id, @Param("new_description") String description);

    @Query("Select e From Event e Where event_date is not null And event_date between :from_date and :to_date")
    List<Event> getAllFromDateToDate(@Param("from_date") Date from, @Param("to_date") Date to);
}
