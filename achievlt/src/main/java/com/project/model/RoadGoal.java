package com.project.model;

import java.time.LocalDateTime;

public class RoadGoal {
    private int id;
    private int tujuanId;
    private String deskripsi;
    private boolean isCompleted;
    private LocalDateTime createdAt;
    private String priorityType; // New field for priority

    // Constructor for loading from DB
    public RoadGoal(int id, int tujuanId, String deskripsi, boolean isCompleted, LocalDateTime createdAt, String priorityType) {
        this.id = id;
        this.tujuanId = tujuanId;
        this.deskripsi = deskripsi;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.priorityType = priorityType;
    }

    // Constructor for creating new RoadGoal (defaulting to LOW priority for now)
    public RoadGoal(int tujuanId, String deskripsi, boolean isCompleted) {
        this(0, tujuanId, deskripsi, isCompleted, null, "LOW"); // Default to LOW
    }

    // Constructor for creating new RoadGoal with specified priority
    public RoadGoal(int tujuanId, String deskripsi, boolean isCompleted, String priorityType) {
        this(0, tujuanId, deskripsi, isCompleted, null, priorityType);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTujuanId() {
        return tujuanId;
    }

    public void setTujuanId(int tujuanId) {
        this.tujuanId = tujuanId;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(String priorityType) {
        this.priorityType = priorityType;
    }

    // New method to get color based on priority (Polymorphism applied via subclasses will override this)
    public String getPriorityColor() {
        switch (priorityType) {
            case "HIGH": return "red";
            case "MEDIUM": return "orange"; // Changed from yellow to orange for better visibility
            case "LOW": return "green";
            default: return "gray";
        }
    }

    // Placeholder method for unique features (will be overridden)
    public String getAdditionalInfo() {
        return "";
    }
}