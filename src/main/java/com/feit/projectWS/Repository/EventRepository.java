package com.feit.projectWS.Repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.feit.projectWS.Models.Event;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Models.enums.EventStatus;

@Repository
public interface EventRepository extends JpaRepository <Event, Integer> {
    List<Event> findByNameContaining (String name);
    List<Event> findByCreatedBy (User user);
    List<Event> findByParticipantsContaining(User user);

    List<Event> findByEventStatus (EventStatus status);

    List<Event> findByEventDate (Date date);
    List<Event> findByEventDateAfter (Date date);
    List<Event> findByEventDateBefore (Date date);
    List<Event> findByEventDateGreaterThanEqual (Date date);
    List<Event> findByEventDateLessThanEqual (Date date);

    List<Event> findByLengthLessThan (int length);
    List<Event> findByLengthLessThanEqual (int length);
    List<Event> findByLengthGreaterThan (int length);
    List<Event> findByLengthGreaterThanEqual (int length);

    List<Event> findByElevationGainLessThan (int elevationGain);
    List<Event> findByElevationGainLessThanEqual (int elevationGain);
    List<Event> findByElevationGainGreaterThan (int elevationGain);
    List<Event> findByElevationGainGreaterThanEqual (int elevationGain);

    @Query("SELECT e FROM Event e WHERE SIZE(e.participants) = :participantCount")
    List<Event> findByParticipantCount(@Param("participantCount") int participantCount);

    @Query("SELECT e FROM Event e WHERE SIZE(e.participants) >= :minParticipants")
    List<Event> findByMinimumParticipants(@Param("minParticipants") int minParticipants);

    @Query("SELECT e FROM Event e WHERE SIZE(e.participants) <= :maxParticipants")
    List<Event> findByMaximumParticipants(@Param("maxParticipants") int maxParticipants);

    int countByEventStatus(EventStatus status);
    
}