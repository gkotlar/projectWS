package com.feit.projectWS.Services;

import java.util.List;

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

    @Autowired PasswordEncoder passwordEncoder;

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public User findUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findUserByName(String name) {
        return userRepository.findByUsername(name).orElse(null);
    }

    public User createUser (User user) {
        user.setAccountActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser (int userId, User tmpUser) {
        User user = userRepository.findById(userId).orElse(null);

        String pw = passwordEncoder.encode(tmpUser.getPassword());
        if (user == null) {
            return null;
        }

        user.setUsername(tmpUser.getUsername());
        user.setDateOfBirth(tmpUser.getDateOfBirth());
        if (pw != user.getPassword()){
            user.setPassword(pw);
        }
        user.setAccountActive(tmpUser.isAccountActive());

        return userRepository.save(user);
    }

    public void deleteUser (int userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null){
            return;
        }

        user.setAccountActive(false);
        user.setDateOfBirth(null);
    }
}
