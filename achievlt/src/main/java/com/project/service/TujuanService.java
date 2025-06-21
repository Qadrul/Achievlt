package com.project.service;

import com.project.model.*; // Import all models
import com.project.repository.TujuanRepository;
import com.project.repository.RoadGoalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // Untuk menerima data road goals dengan prioritas dan info tambahan

public class TujuanService {
    private TujuanRepository tujuanRepository;
    private RoadGoalRepository roadGoalRepository;

    public TujuanService() {
        this.tujuanRepository = new TujuanRepository();
        this.roadGoalRepository = new RoadGoalRepository();
    }

    // Ubah parameter roadGoalDescriptions menjadi Map atau objek custom
    // yang bisa menampung deskripsi, prioritas, dan info tambahan
    public Tujuan createTujuan(int userId, String namaTujuan, LocalDate deadline, List<Map<String, Object>> roadGoalDataList) {
        Tujuan newTujuan = new Tujuan(userId, namaTujuan, deadline);
        Tujuan savedTujuan = tujuanRepository.save(newTujuan);

        if (savedTujuan != null && roadGoalDataList != null) {
            for (Map<String, Object> roadGoalData : roadGoalDataList) {
                String description = (String) roadGoalData.get("description");
                String priority = (String) roadGoalData.get("priority");

                if (description != null && !description.trim().isEmpty()) {
                    RoadGoal newRoadGoal;
                    switch (priority) {
                        case "HIGH":
                            LocalDate dueDate = (LocalDate) roadGoalData.get("dueDate");
                            newRoadGoal = new HighPriorityRoadGoal(savedTujuan.getId(), description, false, dueDate);
                            break;
                        case "MEDIUM":
                            Integer estimatedDays = (Integer) roadGoalData.get("estimatedDays");
                            newRoadGoal = new MediumPriorityRoadGoal(savedTujuan.getId(), description, false, estimatedDays);
                            break;
                        case "LOW":
                            String notes = (String) roadGoalData.get("notes");
                            newRoadGoal = new LowPriorityRoadGoal(savedTujuan.getId(), description, false, notes);
                            break;
                        default: // Fallback
                            newRoadGoal = new RoadGoal(savedTujuan.getId(), description, false, "LOW");
                            break;
                    }
                    roadGoalRepository.save(newRoadGoal);
                    savedTujuan.addRoadGoal(newRoadGoal); // Add to model for immediate use
                }
            }
        }
        return savedTujuan;
    }

    public List<Tujuan> getTujuanByUserId(int userId) {
        return tujuanRepository.findByUserId(userId);
    }

    public Tujuan getTujuanById(int tujuanId) {
        return tujuanRepository.findById(tujuanId);
    }

    public void updateTujuan(Tujuan tujuan) {
        tujuanRepository.update(tujuan);
    }

    public void deleteTujuan(int tujuanId) {
        tujuanRepository.delete(tujuanId);
    }

    // Metode untuk membuat RoadGoal yang spesifik
    public RoadGoal createRoadGoal(int tujuanId, String deskripsi, String priorityType, LocalDate dueDate, Integer estimatedDays, String notes) {
        RoadGoal newRoadGoal;
        switch (priorityType) {
            case "HIGH":
                newRoadGoal = new HighPriorityRoadGoal(tujuanId, deskripsi, false, dueDate);
                break;
            case "MEDIUM":
                newRoadGoal = new MediumPriorityRoadGoal(tujuanId, deskripsi, false, estimatedDays);
                break;
            case "LOW":
                newRoadGoal = new LowPriorityRoadGoal(tujuanId, deskripsi, false, notes);
                break;
            default:
                newRoadGoal = new RoadGoal(tujuanId, deskripsi, false, "LOW");
                break;
        }
        return roadGoalRepository.save(newRoadGoal);
    }

    public void updateRoadGoal(RoadGoal roadGoal) {
        roadGoalRepository.update(roadGoal);
    }

    public void deleteRoadGoal(int roadGoalId) {
        roadGoalRepository.delete(roadGoalId);
    }
}