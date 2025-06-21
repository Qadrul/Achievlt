package com.project.model;

import java.time.LocalDateTime;

public class LowPriorityRoadGoal extends RoadGoal {
    private String notes; // Additional feature: notes

    // Constructor for creating new
    public LowPriorityRoadGoal(int tujuanId, String deskripsi, boolean isCompleted, String notes) {
        super(tujuanId, deskripsi, isCompleted, "LOW"); // Set priority type to LOW
        this.notes = notes;
    }

    // Constructor for loading from DB
    public LowPriorityRoadGoal(int id, int tujuanId, String deskripsi, boolean isCompleted, LocalDateTime createdAt, String notes) {
        super(id, tujuanId, deskripsi, isCompleted, createdAt, "LOW");
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String getAdditionalInfo() {
        return (notes != null && !notes.isEmpty()) ? "Catatan: " + notes : "Tidak ada catatan tambahan.";
    }
}