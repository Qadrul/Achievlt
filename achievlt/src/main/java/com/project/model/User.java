package com.project.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String password; // Sebaiknya di-hash di aplikasi nyata
    private LocalDateTime createdAt;

    // Konstruktor
    public User(int id, String username, String password, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}