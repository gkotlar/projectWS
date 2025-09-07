package com.feit.projectWS.DTOs;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.feit.projectWS.Models.enums.EventStatus;


// DTO for returning user data (excludes password)
@Data
public class EventResponseDTO {
 
    private int id;
    private String name;
    private int length;
    private int elevationGain;
    private String description;
    private EventStatus eventStatus;
    private Date eventDate;
    private String startLocation;
    private String finishLocation;
    private UserResponseDTO createdBy;
    private List<UserResponseDTO> participants = new ArrayList<UserResponseDTO>();
    
    // Constructor to convert from User entity
    public EventResponseDTO(com.feit.projectWS.Models.Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.length = event.getLength();
        this.elevationGain = event.getElevationGain();
        this.description = event.getDescription();
        this.eventStatus = event.getEventStatus();
        this.eventDate = event.getEventDate();
        this.startLocation = event.getStartLocation();
        this.finishLocation = event.getFinishLocation();
        this.createdBy = new UserResponseDTO(event.getCreatedBy());
        for (com.feit.projectWS.Models.User user : event.getParticipants()) {
            this.participants.add(new UserResponseDTO(user));
        }
    }
}

