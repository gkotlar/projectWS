package com.feit.projectWS.Controllers.REST;

import com.feit.projectWS.Models.Event;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Models.enums.EventStatus;
import com.feit.projectWS.Services.EventService;
import com.feit.projectWS.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*") // Configure properly for production
public class EventController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;

    // GET /api/events - Get all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.findAllEvents();
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/{id} - Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @PathVariable int id) {
        try {
            Event event = eventService.findEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/createdbyuser - Get events created by user
    @GetMapping("/owned")
    public ResponseEntity<List<Event>> getEventsByCreator(
            @RequestAttribute(name = "userId") int userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Event> events = eventService.findEventsByCreatedByUser(user);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(events);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/applied - Get events where user is a participant
    @GetMapping("/applied")
    public ResponseEntity<List<Event>> getEventsByParticipant(
            @RequestAttribute(name = "userId")  int userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Event> events = eventService.findEventsByParticipatedByUser(user);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?name={name} - Search events by name
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEventsByName(
            @RequestParam(name = "name") String name) {
        try {
            List<Event> events = eventService.findEventsByName(name);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?date={date}&type={type} - Get events for date
    @GetMapping("/search")
    public ResponseEntity<List<Event>> getEventsByDate(
            @RequestParam(name = "type", defaultValue = "equal") String type,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            
            List<Event> events = new ArrayList<Event>();
            switch (type) {
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

            eventService.findEventsByDateAnchor(date, true);
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?status={status} - filter events by status
    @GetMapping("/search")
    public ResponseEntity<List<Event>> getEventsByStatus(
            @RequestParam(name = "status") String tmpStatus) {
        try {       
            if(tmpStatus.isEmpty() || tmpStatus.isBlank()){
                return ResponseEntity.badRequest().build();
            }
            EventStatus status = EventStatus.ACTIVE;
            try {
                 status = Enum.valueOf(EventStatus.class, tmpStatus);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }            
            
            List<Event> events = eventService.findEventsByStatus(status);             
            
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?participants={number}&type={type} - Filter by participant count
    @GetMapping("/search")
    public ResponseEntity<List<Event>> getEventsByParticipantCount(
            @RequestParam(name = "minparticipants") int number,
            @RequestParam(name = "type", defaultValue = "equal") String type) {
        try {
            List<Event> events = new ArrayList<Event>();
            switch (type) {
                case "equal":
                    events = eventService.findEventsByParticapantNumber(number);
                    break;
                case "min":
                    events = eventService.findEventsByParticapantMinNumber(number);
                    break;
                case "max":
                    events = eventService.findEventsByParticapantMaxNumber(number);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?length={length}&type={type} - Filter by length
    @GetMapping("/search")
    public ResponseEntity<List<Event>> getEventsByLength(
            @RequestParam(name = "length") int length,
            @RequestParam(name = "type", defaultValue = "max") String type) {
        try {
            List<Event> events = new ArrayList<Event>();
            switch (type) {
                case "min":
                    events = eventService.findEventsByMaxLength(length);
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
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/events/search?elevation={elevation}&type={type} - Filter by elevation gain
    @GetMapping("/search")
    public ResponseEntity<List<Event>> getEventsByElevation(
            @RequestParam(name = "length") int elevation,
            @RequestParam(name = "type", defaultValue = "min") String type) {
        try {
            List<Event> events = new ArrayList<Event>();
            switch (type) {
                case "min":
                    events = eventService.findEventsByMaxElevationGain(elevation);
                    break;
                case "max":
                    events = eventService.findEventsByMinElevationGain(elevation);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            if (events.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/events - Create new event
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Validated @RequestBody Event event) {
        try {
            Event savedEvent = eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/events/{id} - Update existing event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable int id, 
            @Validated @RequestBody Event event) {
        try {
            Event existingEvent = eventService.findEventById(id);
            if (existingEvent == null) {
                return ResponseEntity.notFound().build();
            }
            
            event.setId(id); // Ensure the ID is set
            Event updatedEvent = eventService.editEvent(event);
            
            if (updatedEvent == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/events/{id} - Delete event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable int id) {
        try {
            Event existingEvent = eventService.findEventById(id);
            if (existingEvent == null) {
                return ResponseEntity.notFound().build();
            }
            
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/events/{id}/join/{userId} - Join event as participant
    @PostMapping("/{id}/join")
    public ResponseEntity<Event> joinEvent(
            @PathVariable int id, 
            @RequestAttribute("userId") int userId) {
        try {
            Event event = eventService.findEventById(id);
            User user = userService.findUserById(userId);
            
            if (event == null || user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Check if user is already a participant
            if (event.getParticipants().contains(user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Already joined
            }
            
            event.getParticipants().add(user);
            Event updatedEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/events/{id}/leave/{userId} - Leave event as participant
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Event> leaveEvent(
            @PathVariable int id, 
            @RequestAttribute("userId") int userId) {
        try {
            Event event = eventService.findEventById(id);
            User user = userService.findUserById(userId);
            
            if (event == null || user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Remove user from participants
            boolean removed = event.getParticipants().remove(user);
            if (!removed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // User wasn't a participant
            }
            
            Event updatedEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}