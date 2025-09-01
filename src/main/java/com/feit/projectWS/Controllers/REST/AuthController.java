package com.feit.projectWS.Controllers.REST;

import com.feit.projectWS.DTOs.CreateUserDTO;
import com.feit.projectWS.DTOs.LoginDTO;
import com.feit.projectWS.DTOs.UserResponseDTO;
import com.feit.projectWS.Models.User;
import com.feit.projectWS.Security.JwtService;
import com.feit.projectWS.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.findUserByName(loginDTO.getUsername());
            if (user == null || !user.isAccountActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String token = authenticate(loginDTO.getUsername(), loginDTO.getPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is disabled"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed"));
        }
    }

    private String authenticate(String username, String password) throws Exception {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(token);
            String jwt = jwtService.generateToken(auth);
            return jwt;
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}