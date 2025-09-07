package com.feit.projectWS.Controllers.REST;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feit.projectWS.DTOs.EventRequestDTO;
import com.feit.projectWS.DTOs.EventResponseDTO;
import com.feit.projectWS.Models.Event;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Models.enums.EventStatus;
import com.feit.projectWS.Services.EventService;
import com.feit.projectWS.Services.UserService;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;

    // GET /api/events - Get all events
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        try {
            List<Event> events = eventService.findAllEvents();
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/{id} - Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(
            @PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Event event = eventService.findEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new EventResponseDTO(event));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/owned/{userId} - Get events created by user
    @GetMapping("/owned/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByCreator(
            @PathVariable int userId) {
        try {
            if (userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Event> events = eventService.findEventsByCreatedByUser(user);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/applied - Get events where user is a participant
    @GetMapping("/applied/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByParticipant(
            @PathVariable int userId) {
        try {
            if (userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Event> events = eventService.findEventsByParticipatedByUser(user);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventsDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventsDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search/name?name={name} - Search events by name
    @GetMapping("/search/name")
    public ResponseEntity<List<EventResponseDTO>> searchEventsByName(
            @RequestParam(name = "name") String name) {
        try {
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            List<Event> events = eventService.findEventsByName(name);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search/date?date={date}&type={type} - Get events for date
    @GetMapping("/search/date")
    public ResponseEntity<List<EventResponseDTO>> getEventsByDate(
            @RequestParam(name = "type", defaultValue = "equal") String type,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {   
            if (date == null || type.isEmpty() || type.isBlank()) {
                return ResponseEntity.badRequest().build();
            }      
            List<Event> events = new ArrayList<Event>();
            switch (type.toLowerCase().trim()) {
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
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search/status?status={status} - filter events by status
    @GetMapping("/search/status")
    public ResponseEntity<List<EventResponseDTO>> getEventsByStatus(
            @RequestParam(name = "status") String tmpStatus) {
        try {       
            if(tmpStatus.isEmpty() || tmpStatus.isBlank()){
                return ResponseEntity.badRequest().build();
            }
            EventStatus status = EventStatus.ACTIVE;
            try {
                 status = Enum.valueOf(EventStatus.class, tmpStatus.toUpperCase().trim());
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }            
            
            List<Event> events = eventService.findEventsByStatus(status);             
            
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search/participants?participants={number}&type={type} - Filter by participant count
    @GetMapping("/search/participants")
    public ResponseEntity<List<EventResponseDTO>> getEventsByParticipantCount(
            @RequestParam(name = "minparticipants") int count,
            @RequestParam(name = "type", defaultValue = "equal") String type) {
        try {
            if (count < 0 || type.isEmpty() || type.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            List<Event> events = new ArrayList<Event>();
            switch (type.toLowerCase().trim()) {
                case "equal":
                    events = eventService.findEventsByParticapantNumber(count);
                    break;
                case "min":
                    events = eventService.findEventsByParticapantMinNumber(count);
                    break;
                case "max":
                    events = eventService.findEventsByParticapantMaxNumber(count);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?length={length}&type={type} - Filter by length
    @GetMapping("/search/length")
    public ResponseEntity<List<EventResponseDTO>> getEventsByLength(
            @RequestParam(name = "length") int length,
            @RequestParam(name = "type", defaultValue = "min") String type) {
        try {
            if (length < 0 || type.isEmpty() || type.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            List<Event> events = new ArrayList<Event>();
            switch (type.toLowerCase().trim()) {
                case "min":
                    events = eventService.findEventsByMinLength(length);
                    break;
                case "max":
                    events = eventService.findEventsByMaxLength(length);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?elevation={elevation}&type={type} - Filter by elevation gain
    @GetMapping("/search/elevation")
    public ResponseEntity<List<EventResponseDTO>> getEventsByElevation(
            @RequestParam(name = "elevationGain") int elevation,
            @RequestParam(name = "type") String type) {
        try {
            if (elevation < 0 || type.isEmpty() || type.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            List<Event> events = new ArrayList<Event>();
            switch (type.toLowerCase().trim()) {
                case "min":
                    events = eventService.findEventsByMinElevationGain(elevation);
                    break;
                case "max":
                    events = eventService.findEventsByMaxElevationGain(elevation);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<EventResponseDTO> eventDTOs = events.stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(eventDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/events - Create new event
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @Validated @RequestBody EventRequestDTO eventDTO) {
        try {
            User creator = userService.findUserById(eventDTO.getUserId());
            if (creator == null) {
                return ResponseEntity.notFound().build();
            }

            Event event = new Event();
            event.setName(eventDTO.getName());
            event.setLength(eventDTO.getLength());
            event.setElevationGain(eventDTO.getElevationGain());
            event.setDescription(eventDTO.getDescription());
            event.setEventStatus(eventDTO.getEventStatus());
            event.setEventDate(eventDTO.getEventDate());
            event.setStartLocation(eventDTO.getStartLocation());
            event.setFinishLocation(eventDTO.getFinishLocation());
            event.setCreatedBy(creator);

            Event savedEvent = eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponseDTO(savedEvent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/events/{id} - Update existing event
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable int id, 
            @Validated @RequestBody EventRequestDTO eventDTO) {
        try {
            Event existingEvent = eventService.findEventById(id);
            if (existingEvent == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is the creator
            if (existingEvent.getCreatedBy().getId() != eventDTO.getUserId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            existingEvent.setName(eventDTO.getName());
            existingEvent.setLength(eventDTO.getLength());
            existingEvent.setElevationGain(eventDTO.getElevationGain());
            existingEvent.setDescription(eventDTO.getDescription());
            existingEvent.setEventStatus(eventDTO.getEventStatus());
            existingEvent.setEventDate(eventDTO.getEventDate());
            existingEvent.setStartLocation(eventDTO.getStartLocation());
            existingEvent.setFinishLocation(eventDTO.getFinishLocation());

            Event updatedEvent = eventService.saveEvent(existingEvent);
            return ResponseEntity.ok(new EventResponseDTO(updatedEvent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/events/{id} - Delete event
    @DeleteMapping("/{id}/{userId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable int id,
            @PathVariable int userId) {
        try {
            Event existingEvent = eventService.findEventById(id);
            if (existingEvent == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is the creator
            if (existingEvent.getCreatedBy().getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/events/{id}/join/{userId} - Join event as participant
    @PostMapping("/{id}/join/{userId}")
    public ResponseEntity<EventResponseDTO> joinEvent(
            @PathVariable int id, 
            @PathVariable int userId) {
        try {
            Event event = eventService.findEventById(id);
            User user = userService.findUserById(userId);
            
            if (event == null || user == null) {
                return ResponseEntity.notFound().build();
            }
            // Check if event is active
            if (event.getEventStatus() != EventStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error-Reason", "Event is not active")
                    .build();
            }
            
            // Check if user is already a participant
            if (event.getParticipants().contains(user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Already joined
            }
            
            event.getParticipants().add(user);
            Event updatedEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(new EventResponseDTO(updatedEvent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/events/{id}/leave/{userId} - Leave event as participant
    @PostMapping("/{id}/leave/{userId}")
    public ResponseEntity<EventResponseDTO> leaveEvent(
            @PathVariable int id, 
            @PathVariable int userId) {
        try {
            Event event = eventService.findEventById(id);
            User user = userService.findUserById(userId);
            
            if (event == null || user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Remove user from participants
            boolean removed = event.getParticipants().removeIf(p -> p.getId() == userId);
            if (!removed) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error-Reason", "User is not a participant")
                    .build();
            }
            
            Event updatedEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(new EventResponseDTO(updatedEvent));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}