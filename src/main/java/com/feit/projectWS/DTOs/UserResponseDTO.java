package com.feit.projectWS.DTOs;

import lombok.Data;
import java.util.Date;

// DTO for returning user data (excludes password)
@Data
public class UserResponseDTO {
    private int id;
    private String username;
    private Date dateOfBirth;
    private boolean accountActive;
    private Date created_at;
    private Date updated_at;
    
    // Constructor to convert from User entity
    public UserResponseDTO(com.feit.projectWS.Models.User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.dateOfBirth = user.getDateOfBirth();
        this.accountActive = user.isAccountActive();
        this.created_at = user.getCreated_at();
        this.updated_at = user.getUpdated_at();
    }
}
