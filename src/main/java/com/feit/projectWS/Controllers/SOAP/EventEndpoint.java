package com.feit.projectWS.Controllers.SOAP;

import com.feit.projectWS.Models.Event;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Models.enums.EventStatus;
import com.feit.projectWS.Services.EventService;
import com.feit.projectWS.Services.UserService;
import com.feit.projectWS.Exceptions.*;

// Import generated JAXB classes (these will be generated from XSD)
import com.feit.projectWS.events.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.util.*;
import java.sql.Date;
import java.util.List;

@Endpoint
public class EventEndpoint {
    
    private static final String NAMESPACE_URI = "http://projectWS.feit.com/events";
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;
    
    // Get all events
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllEventsRequest")
    @ResponsePayload
    public GetAllEventsResponse getAllEvents(@RequestPayload GetAllEventsRequest request) {
        GetAllEventsResponse response = new GetAllEventsResponse();
        
        try {
            List<Event> events = eventService.findAllEvents();
            EventList eventList = new EventList();
            
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (Exception e) {
            // Log error and return empty list
            response.setEvents(new EventList());
        }
        
        return response;
    }
    
    // Get event by ID
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventByIdRequest")
    @ResponsePayload
    public GetEventByIdResponse getEventById(@RequestPayload GetEventByIdRequest request) {
        GetEventByIdResponse response = new GetEventByIdResponse();
        
        if (request.getId() <= 0) {
            throw new IllegalArgumentException("Event ID must be greater than 0");
        }
        
        try {
            Event event = eventService.findEventById(request.getId());
            if (event == null) {
                throw new EventNotFoundException("Event with ID " + request.getId() + " not found");
            }
            response.setEvent(convertToSoapEvent(event));
        } catch (EventNotFoundException e) {
            throw e; // Re-throw to be handled by exception handler
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving event: " + e.getMessage(), e);
        }
        
        return response;
    }
    
    // Search events by name
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchEventsByNameRequest")
    @ResponsePayload
    public SearchEventsByNameResponse searchEventsByName(@RequestPayload SearchEventsByNameRequest request) {
        SearchEventsByNameResponse response = new SearchEventsByNameResponse();
        
        try {
            List<Event> events = eventService.findEventsByName(request.getName());
            EventList eventList = new EventList();
            
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (Exception e) {
            response.setEvents(new EventList());
        }
        
        return response;
    }
    
    // Get events by status
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventsByStatusRequest")
    @ResponsePayload
    public GetEventsByStatusResponse getEventsByStatus(@RequestPayload GetEventsByStatusRequest request) {
        GetEventsByStatusResponse response = new GetEventsByStatusResponse();
        
        try {
            EventStatus status = EventStatus.valueOf(request.getStatus().value());
            List<Event> events = eventService.findEventsByStatus(status);
            EventList eventList = new EventList();
            
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (Exception e) {
            response.setEvents(new EventList());
        }
        
        return response;
    }
    
    // Get events by date
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventsByDateRequest")
    @ResponsePayload
    public GetEventsByDateResponse getEventsByDate(@RequestPayload GetEventsByDateRequest request) {
        GetEventsByDateResponse response = new GetEventsByDateResponse();
        
        try {
            // Convert XMLGregorianCalendar to SQL Date
            Date date = new Date(request.getDate().toGregorianCalendar().getTimeInMillis());
            String type = request.getType() != null ? request.getType() : "equal";
            
            List<Event> events;
            switch (type.toLowerCase()) {
                case "equal":
                    events = eventService.findEventsByDate(date);
                    break;
                case "after":
                    events = eventService.findEventsByDateAnchor(date, true);
                    break;
                case "before":
                    events = eventService.findEventsByDateAnchor(date, false);
                    break;
                default:
                    events = eventService.findEventsByDate(date);
                    break;
            }
            
            EventList eventList = new EventList();
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (Exception e) {
            response.setEvents(new EventList());
        }
        
        return response;
    }
    
    // Get events by length
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventsByLengthRequest")
    @ResponsePayload
    public GetEventsByLengthResponse getEventsByLength(@RequestPayload GetEventsByLengthRequest request) {
        GetEventsByLengthResponse response = new GetEventsByLengthResponse();
        
        try {
            int length = request.getLength();
            String type = request.getType() != null ? request.getType() : "min";
            
            if (length < 0) {
                throw new IllegalArgumentException("Length must be non-negative");
            }
            
            List<Event> events;
            switch (type.toLowerCase()) {
                case "min":
                    events = eventService.findEventsByMinLength(length);
                    break;
                case "max":
                    events = eventService.findEventsByMaxLength(length);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type. Use 'min' or 'max'");
            }
            
            EventList eventList = new EventList();
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw to be handled by exception handler
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving events by length: " + e.getMessage(), e);
        }
        
        return response;
    }
    
    // Get events by elevation gain
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEventsByElevationGainRequest")
    @ResponsePayload
    public GetEventsByElevationGainResponse getEventsByElevationGain(@RequestPayload GetEventsByElevationGainRequest request) {
        GetEventsByElevationGainResponse response = new GetEventsByElevationGainResponse();
        
        try {
            int elevationGain = request.getElevationGains();
            String type = request.getType() != null ? request.getType() : "min";
            
            if (elevationGain < 0) {
                throw new IllegalArgumentException("Elevation gain must be non-negative");
            }
            
            List<Event> events;
            switch (type.toLowerCase()) {
                case "min":
                    events = eventService.findEventsByMinElevationGain(elevationGain);
                    break;
                case "max":
                    events = eventService.findEventsByMaxElevationGain(elevationGain);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type. Use 'min' or 'max'");
            }
            
            EventList eventList = new EventList();
            for (Event event : events) {
                eventList.getEvent().add(convertToSoapEvent(event));
            }
            
            response.setEvents(eventList);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw to be handled by exception handler
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving events by elevation gain: " + e.getMessage(), e);
        }
        
        return response;
    }
    
    // Helper methods to convert between JPA entities and SOAP types
    private com.feit.projectWS.events.Event convertToSoapEvent(Event jpaEvent) {
        com.feit.projectWS.events.Event soapEvent = new com.feit.projectWS.events.Event();
        
        soapEvent.setId(jpaEvent.getId());
        soapEvent.setName(jpaEvent.getName() != null ? jpaEvent.getName() : "");
        soapEvent.setLength(jpaEvent.getLength());
        soapEvent.setElevationGain(jpaEvent.getElevationGain());
        soapEvent.setDescription(jpaEvent.getDescription() != null ? jpaEvent.getDescription() : "");
        
        // Convert enum
        soapEvent.setEventStatus(com.feit.projectWS.events.EventStatus.fromValue(jpaEvent.getEventStatus().name()));
        
        // Convert dates - these are now required in XSD
        if (jpaEvent.getEventDate() != null) {
            soapEvent.setEventDate(DatatypeConverter.parseDate(jpaEvent.getEventDate().toString()));
        } else {
            // Provide default date if null
            soapEvent.setEventDate(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        soapEvent.setStartLocation(jpaEvent.getStartLocation() != null ? jpaEvent.getStartLocation() : "");
        soapEvent.setFinishLocation(jpaEvent.getFinishLocation() != null ? jpaEvent.getFinishLocation() : "");
        
        // Convert created by user - now required in XSD
        if (jpaEvent.getCreatedBy() != null) {
            soapEvent.setCreatedBy(convertToSoapUser(jpaEvent.getCreatedBy()));
        } else {
            // Create a default user if null
            com.feit.projectWS.events.User defaultUser = new com.feit.projectWS.events.User();
            defaultUser.setId(0);
            defaultUser.setUsername("Unknown");
            defaultUser.setAccountActive(false);
            defaultUser.setDateOfBirth(DatatypeConverter.parseDate("1970-01-01"));
            defaultUser.setCreatedAt(DatatypeConverter.parseDate("1970-01-01"));
            defaultUser.setUpdatedAt(DatatypeConverter.parseDate("1970-01-01"));
            soapEvent.setCreatedBy(defaultUser);
        }
        
        // Convert participants - now required in XSD
        UserList userList = new UserList();
        if (jpaEvent.getParticipants() != null && !jpaEvent.getParticipants().isEmpty()) {
            for (User participant : jpaEvent.getParticipants()) {
                userList.getUser().add(convertToSoapUser(participant));
            }
        }
        soapEvent.setParticipants(userList);
        
        // Convert timestamps - now required in XSD
        if (jpaEvent.getCreated_at() != null) {
            soapEvent.setCreatedAt(DatatypeConverter.parseDate(jpaEvent.getCreated_at().toString()));
        } else {
            soapEvent.setCreatedAt(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        if (jpaEvent.getUpdated_at() != null) {
            soapEvent.setUpdatedAt(DatatypeConverter.parseDate(jpaEvent.getUpdated_at().toString()));
        } else {
            soapEvent.setUpdatedAt(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        return soapEvent;
    }
    
    private com.feit.projectWS.events.User convertToSoapUser(User jpaUser) {
        com.feit.projectWS.events.User soapUser = new com.feit.projectWS.events.User();
        
        soapUser.setId(jpaUser.getId());
        soapUser.setUsername(jpaUser.getUsername() != null ? jpaUser.getUsername() : "");
        soapUser.setAccountActive(jpaUser.isAccountActive());
        
        // These are now required in XSD
        if (jpaUser.getDateOfBirth() != null) {
            soapUser.setDateOfBirth(DatatypeConverter.parseDate(jpaUser.getDateOfBirth().toString()));
        } else {
            soapUser.setDateOfBirth(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        if (jpaUser.getCreated_at() != null) {
            soapUser.setCreatedAt(DatatypeConverter.parseDate(jpaUser.getCreated_at().toString()));
        } else {
            soapUser.setCreatedAt(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        if (jpaUser.getUpdated_at() != null) {
            soapUser.setUpdatedAt(DatatypeConverter.parseDate(jpaUser.getUpdated_at().toString()));
        } else {
            soapUser.setUpdatedAt(DatatypeConverter.parseDate("1970-01-01"));
        }
        
        return soapUser;
    }
}