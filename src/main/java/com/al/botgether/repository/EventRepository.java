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

    @Modifying
    @Query("Update Event Set event_date = :new_date Where id = :id")
    void updateEventDate(@Param("id") long id, @Param("new_date") Date date);

    @SuppressWarnings("SqlResolve")
    @Query(value = "Select e.* From Event e INNER JOIN Availability a ON e.id = a.event_id " +
            "Where e.event_date is not null " +
            "And e.event_date between CURRENT_DATE And CURRENT_DATE + 7 " +
            "And a.user_id = :user_id",
            nativeQuery = true)
    List<Event> getAllByUserIdAndDateSet(@Param("user_id") String userId);
}
