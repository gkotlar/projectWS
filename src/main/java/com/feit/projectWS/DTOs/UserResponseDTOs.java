package com.feit.projectWS.DTOs;

import java.util.ArrayList;

import lombok.Data;

@Data
public class UserResponseDTOs {
    
    public UserResponseDTOs(ArrayList<com.feit.projectWS.Models.User> users){
        ArrayList<UserResponseDTO> userDTOs = new ArrayList<UserResponseDTO>();
        for (com.feit.projectWS.Models.User user : users) {
            userDTOs.add(new UserResponseDTO(user));
        }
    }
}
