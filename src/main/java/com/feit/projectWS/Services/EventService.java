package com.feit.projectWS.Services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feit.projectWS.Models.Event;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Models.enums.EventStatus;
import com.feit.projectWS.Repository.EventRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    public Event findEventById(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        return event;
    }
    public List<Event> findEventsByName (String name){
        List<Event> events = eventRepository.findByNameContaining(name);
        return events;
    }

    public List<Event> findEventsByCreatedByUser(User user){
        List<Event> events = eventRepository.findByCreatedBy(user);
        return events;
    }

    public List<Event> findEventsByParticipatedByUser(User user) {
        /*Alt if it doesn't work
        List<Event> events = eventRepository.findAll();
        List<Event> tmpEvents = new ArrayList<Event>();
        events.forEach(c -> {
            if (c.getParticipants().contains(user)) {
                tmpEvents.add(c);
            }
        });
        return tmpEvents;
         */
        List<Event> events = eventRepository.findByParticipantsContaining(user);
        return events;
    }
    public List<Event> findEventsByDate(Date date){
        List<Event> events = eventRepository.findByEventDate(date);
        return events;
    }

    public List<Event> findEventsByDateAnchor(Date date, boolean isAfterBoolean){
        /*Alt if it doesn't work
        List<Event> events = eventRepository.findAll();
        List<Event> tmpEvents = new ArrayList<Event>();
        if(isAfterBoolean){
            events.forEach(c -> {
                if (c.getEventDate().after(date)) {
                    tmpEvents.add(c);
                }
            });
        }else{
            events.forEach(c -> {
                if (c.getEventDate().before(date) || c.getEventDate().equals(date)){
                    tmpEvents.add(c);
                }
            });
        }
         */
        List<Event> events = new ArrayList<Event>();
        if (isAfterBoolean){
            events = eventRepository.findByEventDateAfter(date);
        }else{
            events = eventRepository.findByEventDateBefore(date);
        }
        return events;
    }

    public List<Event> findEventsByStatus(EventStatus status){
        List<Event> events = eventRepository.findByEventStatus(status);
        return events;
    }

    public List<Event> findEventsByParticapantMaxNumber(int paxNumber){
        List<Event> events =  eventRepository.findByMaximumParticipants(paxNumber);
        return events;
    }
    public List<Event> findEventsByParticapantMinNumber(int paxNumber){
        List<Event> events = eventRepository.findByMinimumParticipants(paxNumber);
        return events;
    }
    public List<Event> findEventsByParticapantNumber(int paxNumber){
        List<Event> events = eventRepository.findByParticipantCount(paxNumber);
        return events;
    }

    public List<Event> findEventsByMinElevationGain(int elevationGain){
        List<Event> events =  eventRepository.findByElevationGainGreaterThanEqual(elevationGain);
        return events;
    }
    public List<Event> findEventsByMaxElevationGain(int elevationGain){
        List<Event> events = eventRepository.findByElevationGainLessThanEqual(elevationGain);
        return events;
    }


    public List<Event> findEventsByMinLength(int length){
        List<Event> events =  eventRepository.findByLengthGreaterThanEqual(length);
        return events;
    }
    public List<Event> findEventsByMaxLength(int length){
        List<Event> events = eventRepository.findByLengthLessThanEqual(length);
        return events;
    }

    public Event saveEvent (Event event){
        return eventRepository.save(event);
    }

    public void deleteEvent(int id) {
        eventRepository.deleteById(id);
    }

    public Event editEvent (Event tmpEvent) {
        Event event = eventRepository.findById(tmpEvent.getId()).orElse(null);

        if (event == null){
            return null;
        }

        event.setName(tmpEvent.getName());
        event.setLength(tmpEvent.getLength());
        event.setElevationGain(tmpEvent.getElevationGain());
        event.setDescription(tmpEvent.getDescription());
        event.setEventStatus(tmpEvent.getEventStatus());
        event.setEventDate(tmpEvent.getEventDate());
        event.setStartLocation(tmpEvent.getStartLocation());
        event.setFinishLocation(tmpEvent.getFinishLocation());
        event.setCreatedBy(tmpEvent.getCreatedBy());
        event.setParticipants(tmpEvent.getParticipants());

        return event;
    }
}