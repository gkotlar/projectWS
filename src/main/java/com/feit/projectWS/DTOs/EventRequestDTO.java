package com.feit.projectWS.DTOs;

import com.feit.projectWS.Models.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.sql.Date;

@Data
public class EventRequestDTO {
    @NotBlank(message = "Event name is required")
    @Size(max = 255, message = "Event name cannot exceed 255 characters")
    private String name;

    @Min(value = 0, message = "Length must be non-negative")
    private int length;

    @Min(value = 0, message = "Elevation gain must be non-negative")
    private int elevationGain;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Event status is required")
    private EventStatus eventStatus;

    @Future(message = "Event date must be in the future")
    private Date eventDate;

    @NotBlank(message = "Start location is required")
    @Size(max = 255, message = "Start location cannot exceed 255 characters")
    private String startLocation;

    @NotBlank(message = "Finish location is required")
    @Size(max = 255, message = "Finish location cannot exceed 255 characters")
    private String finishLocation;
}