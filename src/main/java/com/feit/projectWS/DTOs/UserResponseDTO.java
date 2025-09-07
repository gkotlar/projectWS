package com.feit.projectWS.DTOs;

import lombok.Data;

import java.util.Date;

// DTO for returning user data (excludes password)
@Data
public class UserResponseDTO {
    private int id;
    private String username;
    private Date dateOfBirth;

    
    // Constructor to convert from User entity
    public UserResponseDTO(com.feit.projectWS.Models.User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.dateOfBirth = user.getDateOfBirth();
    }
}
