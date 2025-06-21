package com.project.model;

import java.time.LocalDateTime;

public class MediumPriorityRoadGoal extends RoadGoal {
    private Integer estimatedDays; // Additional feature: estimatedDays

    // Constructor for creating new
    public MediumPriorityRoadGoal(int tujuanId, String deskripsi, boolean isCompleted, Integer estimatedDays) {
        super(tujuanId, deskripsi, isCompleted, "MEDIUM"); // Set priority type to MEDIUM
        this.estimatedDays = estimatedDays;
    }

    // Constructor for loading from DB
    public MediumPriorityRoadGoal(int id, int tujuanId, String deskripsi, boolean isCompleted, LocalDateTime createdAt, Integer estimatedDays) {
        super(id, tujuanId, deskripsi, isCompleted, createdAt, "MEDIUM");
        this.estimatedDays = estimatedDays;
    }

    public Integer getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(Integer estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    @Override
    public String getAdditionalInfo() {
        return (estimatedDays != null) ? "Estimasi: " + estimatedDays + " hari" : "Tidak ada estimasi hari.";
    }
}