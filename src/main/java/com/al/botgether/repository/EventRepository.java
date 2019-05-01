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
    public void updateEventTitle(@Param("id") long id, @Param("new_title") String title);

    @Modifying
    @Query("Update Event Set description = :new_description Where id = :id")
    public void updateEventDescription(@Param("id") long id, @Param("new_description") String description);

    @Modifying
    @Query("Update Event Set eventDate = :new_date Where id = :id")
    public void updateEventDate(@Param("id") long id, @Param("new_date") Date date);

    @Query("Select e From Event e Where eventDate is not null And eventDate between :from_date and :to_date")
    public List<Event> getAllFromDateToDate(@Param("from_date") Date from, @Param("to_date") Date to);
}
