package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tujuan {
    private int id;
    private int userId;
    private String namaTujuan;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private List<RoadGoal> roadGoals; // Untuk menampung sub-tujuan

    // Konstruktor
    public Tujuan(int id, int userId, String namaTujuan, LocalDate deadline, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.namaTujuan = namaTujuan;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.roadGoals = new ArrayList<>();
    }

    public Tujuan(int userId, String namaTujuan, LocalDate deadline) {
        this.userId = userId;
        this.namaTujuan = namaTujuan;
        this.deadline = deadline;
        this.roadGoals = new ArrayList<>();
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNamaTujuan() {
        return namaTujuan;
    }

    public void setNamaTujuan(String namaTujuan) {
        this.namaTujuan = namaTujuan;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoadGoal> getRoadGoals() {
        return roadGoals;
    }

    public void setRoadGoals(List<RoadGoal> roadGoals) {
        this.roadGoals = roadGoals;
    }

    public void addRoadGoal(RoadGoal roadGoal) {
        this.roadGoals.add(roadGoal);
    }

    // Metode untuk menghitung persentase progres
    public double getProgressPercentage() {
        if (roadGoals.isEmpty()) {
            return 0.0;
        }
        long completedGoals = roadGoals.stream().filter(RoadGoal::isCompleted).count();
        return (double) completedGoals / roadGoals.size();
    }
}