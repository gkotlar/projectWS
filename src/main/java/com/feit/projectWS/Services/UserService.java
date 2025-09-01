package com.feit.projectWS.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.feit.projectWS.Models.User;
import com.feit.projectWS.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get a List<User> of all of the users
     * @param username The username of the user
     * @return The requested user or null
     */
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    /**
     * find the user by the uid
     * @param userId The users id
     * @return The requested user or null
     */
    public User findUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);    
    }

    /**
     * find the user by his the username
     * @param username The username of the user
     * @return The requested user or null
     */
    public User findUserByName(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    /**
     * Create a new user
     * @param user The we want to create as a following User.java Model
     * @return The saved information if succesfull or null
     */
    public User createUser (User user) {
        user.setAccountActive(true);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }        
        return userRepository.save(user);
    }

    /**
     * Update the user
     * @param userId The userId
     * @param tmpUser The new user information we want to update
     * @return The saved User information or null
     */
    public User updateUser (int userId, User tmpUser) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Update fields
            if (tmpUser.getUsername() != null) {
                existingUser.setUsername(tmpUser.getUsername());
            }
            
            // Hash password only if it's being updated
            if (tmpUser.getPassword() != null && !tmpUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(tmpUser.getPassword()));
            }
            
            if (tmpUser.getDateOfBirth() != null) {
                existingUser.setDateOfBirth(tmpUser.getDateOfBirth());
            }
            
            existingUser.setAccountActive(tmpUser.isAccountActive());
            
            return userRepository.save(existingUser);
        }
        return null;
    }
        
    
    /**
     * Clear user data and set the account as inactive
     * @param userId The userId
     * @return void
     */
    public void deleteUser (int userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null){
            return;
        }

        user.setAccountActive(false);
        user.setDateOfBirth(null);
    }

     /**
     * Verify a plain text password against a hashed password
     * @param plainPassword The plain text password
     * @param hashedPassword The hashed password from database
     * @return true if passwords match
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
    
    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The plain text password
     * @return User if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        User user = findUserByName(username);
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
