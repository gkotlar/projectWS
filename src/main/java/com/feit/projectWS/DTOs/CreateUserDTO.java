package com.feit.projectWS.DTOs;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.Date;

@Data
public class CreateUserDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
    
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;
    
    private boolean accountActive = true; // default to active
}