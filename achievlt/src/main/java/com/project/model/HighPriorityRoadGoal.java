package com.project.model;

import java.time.LocalDate; // Import LocalDate
import java.time.LocalDateTime;

public class HighPriorityRoadGoal extends RoadGoal {
    private LocalDate dueDate; // Additional feature: dueDate

    // Constructor for creating new
    public HighPriorityRoadGoal(int tujuanId, String deskripsi, boolean isCompleted, LocalDate dueDate) {
        super(tujuanId, deskripsi, isCompleted, "HIGH"); // Set priority type to HIGH
        this.dueDate = dueDate;
    }

    // Constructor for loading from DB (needs all superclass fields + new field)
    public HighPriorityRoadGoal(int id, int tujuanId, String deskripsi, boolean isCompleted, LocalDateTime createdAt, LocalDate dueDate) {
        super(id, tujuanId, deskripsi, isCompleted, createdAt, "HIGH");
        this.dueDate = dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String getAdditionalInfo() {
        return (dueDate != null) ? "Deadline: " + dueDate : "No specific due date.";
    }
}