package com.feit.projectWS.Controllers.REST;

import com.feit.projectWS.Models.User;
import com.feit.projectWS.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.feit.projectWS.DTOs.UpdateUserDTO;
import com.feit.projectWS.DTOs.UserResponseDTO;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users - Get all users with pagination
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<UserResponseDTO> userDTOs = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //GET api/users/profile/{userId} - get your profile
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile(
            @PathVariable int userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new UserResponseDTO(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/users/{id} - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable(value = "id") int userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(new UserResponseDTO(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/users/username/{username} - Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable(value = "username") String username) {
        try {
            User user = userService.findUserByName(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new UserResponseDTO(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/users/{id} - Update existing user
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
            @PathVariable("userId") int userId,
            @Validated @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            User existingUser = userService.findUserById(userId);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }
 
            User user = new User();
            user.setUsername(updateUserDTO.getUsername());
            user.setPassword(updateUserDTO.getPassword());
            user.setDateOfBirth(updateUserDTO.getDateOfBirth());
            if (updateUserDTO.getAccountActive() != null) {
                user.setAccountActive(updateUserDTO.getAccountActive());
            }

            User updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.ok(new UserResponseDTO(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/users/{id} - Delete user
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<Void> deleteCurrentUser(@PathVariable int userId) {
        try {
            User existingUser = userService.findUserById(userId);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            if (!existingUser.isAccountActive()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error-Reason", "User is not active anymore")
                    .build();
            }
            
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}