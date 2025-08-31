package com.feit.projectWS.Controllers.REST;

import com.feit.projectWS.Models.User;
import com.feit.projectWS.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.feit.projectWS.DTOs.CreateUserDTO;
import com.feit.projectWS.DTOs.LoginDTO;
import com.feit.projectWS.DTOs.UpdateUserDTO;
import com.feit.projectWS.DTOs.UserResponseDTO;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Configure properly for production
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

    // POST /api/users - Create new user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody CreateUserDTO createUserDTO) {
        try {
            User existingUser = userService.findUserByName(createUserDTO.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
            } 
            
             // Convert DTO to User entity
            User user = new User();
            user.setUsername(createUserDTO.getUsername());
            user.setPassword(createUserDTO.getPassword()); // Will be hashed in service
            user.setDateOfBirth(createUserDTO.getDateOfBirth());
            user.setAccountActive(createUserDTO.isAccountActive());

            User savedUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/users/{id} - Update existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Validated @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            User existingUser = userService.findUserById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Convert DTO to User entity for update
            User user = new User();
            user.setUsername(updateUserDTO.getUsername());
            user.setPassword(updateUserDTO.getPassword()); // Will be hashed in service if provided
            user.setDateOfBirth(updateUserDTO.getDateOfBirth());
            if (updateUserDTO.getAccountActive() != null) {
                user.setAccountActive(updateUserDTO.getAccountActive());
            }
            
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/users/{id} - Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        try {
            User existingUser = userService.findUserById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            if (existingUser.isAccountActive() == false) {
                return ResponseEntity.status(HttpStatusCode.valueOf(206)).build();
            }
            
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     // POST /api/users/login - Authenticate user
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            User authenticatedUser = userService.authenticateUser(loginDTO.getUsername(), loginDTO.getPassword());
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
            }
            
            if (!authenticatedUser.isAccountActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden - account inactive
            }
            
            return ResponseEntity.ok(new UserResponseDTO(authenticatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}