package com.feit.projectWS.DTOs;

import lombok.Data;
import java.sql.Date;

// DTO for creating users (includes password)
@Data
public class CreateUserDTO {
    private String username;
    private String password;
    private Date dateOfBirth;
    private boolean accountActive = true; // default to active
}