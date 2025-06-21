package com.project.service;

import com.project.model.User;
import com.project.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User registerUser(String username, String password) {
        // Dalam aplikasi nyata, hash password di sini
        if (userRepository.findByUsername(username) != null) {
            System.out.println("Username sudah ada!");
            return null;
        }
        User newUser = new User(username, password); // TODO: Hash password
        return userRepository.save(newUser);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) { // TODO: Compare hashed passwords
            return user;
        }
        return null;
    }
}