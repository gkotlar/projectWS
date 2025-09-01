package com.feit.projectWS.Controllers.REST;

import com.feit.projectWS.DTOs.CreateUserDTO;
import com.feit.projectWS.DTOs.LoginDTO;
import com.feit.projectWS.DTOs.UserResponseDTO;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Security.JwtUtil;
import com.feit.projectWS.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            authenticate(loginDTO.getUsername(), loginDTO.getPassword());
            
            User user = userService.findUserByName(loginDTO.getUsername());
            if (user == null || !user.isAccountActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            final String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", new UserResponseDTO(user));

            return ResponseEntity.ok(response);

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is disabled"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody CreateUserDTO createUserDTO) {
        try {
            User existingUser = userService.findUserByName(createUserDTO.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Username already exists"));
            }

            User user = new User();
            user.setUsername(createUserDTO.getUsername());
            user.setPassword(createUserDTO.getPassword());
            user.setDateOfBirth(createUserDTO.getDateOfBirth());
            user.setAccountActive(true);

            User savedUser = userService.createUser(user);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UserResponseDTO(savedUser));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed"));
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}