package com.feit.projectWS.DTOs;

import lombok.Data;
import java.sql.Date;

// DTO for updating users (password is optional)
@Data
public class UpdateUserDTO {
    private String username;
    private String password; // optional - only include if changing password
    private Date dateOfBirth;
    private Boolean accountActive; // Boolean wrapper to allow null
}
