package com.project.repository;

import com.project.config.DBUtil;
import com.project.model.*; // Import semua model
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoadGoalRepository {

    public RoadGoal save(RoadGoal roadGoal) {
        String sql = "INSERT INTO road_goals (tujuan_id, deskripsi, is_completed, priority_type, due_date, estimated_days, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, roadGoal.getTujuanId());
            stmt.setString(2, roadGoal.getDeskripsi());
            stmt.setBoolean(3, roadGoal.isCompleted());
            stmt.setString(4, roadGoal.getPriorityType());

            // Set specific fields based on actual type (Polymorphism in action)
            if (roadGoal instanceof HighPriorityRoadGoal) {
                HighPriorityRoadGoal hpGoal = (HighPriorityRoadGoal) roadGoal;
                stmt.setDate(5, hpGoal.getDueDate() != null ? Date.valueOf(hpGoal.getDueDate()) : null);
                stmt.setNull(6, Types.INTEGER); // estimated_days
                stmt.setNull(7, Types.LONGVARCHAR); // notes
            } else if (roadGoal instanceof MediumPriorityRoadGoal) {
                MediumPriorityRoadGoal mpGoal = (MediumPriorityRoadGoal) roadGoal;
                stmt.setNull(5, Types.DATE); // due_date
                stmt.setObject(6, mpGoal.getEstimatedDays(), Types.INTEGER); // Use setObject for Integer
                stmt.setNull(7, Types.LONGVARCHAR); // notes
            } else if (roadGoal instanceof LowPriorityRoadGoal) {
                LowPriorityRoadGoal lpGoal = (LowPriorityRoadGoal) roadGoal;
                stmt.setNull(5, Types.DATE); // due_date
                stmt.setNull(6, Types.INTEGER); // estimated_days
                stmt.setString(7, lpGoal.getNotes());
            } else { // Default or base RoadGoal
                stmt.setNull(5, Types.DATE);
                stmt.setNull(6, Types.INTEGER);
                stmt.setNull(7, Types.LONGVARCHAR);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        roadGoal.setId(generatedKeys.getInt(1));
                        roadGoal.setCreatedAt(LocalDateTime.now());
                    }
                }
                return roadGoal;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RoadGoal> findByTujuanId(int tujuanId) {
        List<RoadGoal> roadGoals = new ArrayList<>();
        String sql = "SELECT id, tujuan_id, deskripsi, is_completed, created_at, priority_type, due_date, estimated_days, notes FROM road_goals WHERE tujuan_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tujuanId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String deskripsi = rs.getString("deskripsi");
                boolean isCompleted = rs.getBoolean("is_completed");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String priorityType = rs.getString("priority_type");

                RoadGoal roadGoal;
                switch (priorityType) {
                    case "HIGH":
                        LocalDate dueDate = rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null;
                        roadGoal = new HighPriorityRoadGoal(id, tujuanId, deskripsi, isCompleted, createdAt, dueDate);
                        break;
                    case "MEDIUM":
                        Integer estimatedDays = (Integer) rs.getObject("estimated_days"); // Use getObject for Integer
                        roadGoal = new MediumPriorityRoadGoal(id, tujuanId, deskripsi, isCompleted, createdAt, estimatedDays);
                        break;
                    case "LOW":
                        String notes = rs.getString("notes");
                        roadGoal = new LowPriorityRoadGoal(id, tujuanId, deskripsi, isCompleted, createdAt, notes);
                        break;
                    default:
                        roadGoal = new RoadGoal(id, tujuanId, deskripsi, isCompleted, createdAt, priorityType);
                        break;
                }
                roadGoals.add(roadGoal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roadGoals;
    }

    // Metode update dan delete perlu diperbarui juga jika mereka memanipulasi kolom tambahan
    public void update(RoadGoal roadGoal) {
        String sql = "UPDATE road_goals SET deskripsi = ?, is_completed = ?, priority_type = ?, due_date = ?, estimated_days = ?, notes = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roadGoal.getDeskripsi());
            stmt.setBoolean(2, roadGoal.isCompleted());
            stmt.setString(3, roadGoal.getPriorityType());

            // Set specific fields based on actual type
            if (roadGoal instanceof HighPriorityRoadGoal) {
                HighPriorityRoadGoal hpGoal = (HighPriorityRoadGoal) roadGoal;
                stmt.setDate(4, hpGoal.getDueDate() != null ? Date.valueOf(hpGoal.getDueDate()) : null);
                stmt.setNull(5, Types.INTEGER);
                stmt.setNull(6, Types.LONGVARCHAR);
            } else if (roadGoal instanceof MediumPriorityRoadGoal) {
                MediumPriorityRoadGoal mpGoal = (MediumPriorityRoadGoal) roadGoal;
                stmt.setNull(4, Types.DATE);
                stmt.setObject(5, mpGoal.getEstimatedDays(), Types.INTEGER);
                stmt.setNull(6, Types.LONGVARCHAR);
            } else if (roadGoal instanceof LowPriorityRoadGoal) {
                LowPriorityRoadGoal lpGoal = (LowPriorityRoadGoal) roadGoal;
                stmt.setNull(4, Types.DATE);
                stmt.setNull(5, Types.INTEGER);
                stmt.setString(6, lpGoal.getNotes());
            } else { // Default or base RoadGoal
                stmt.setNull(4, Types.DATE);
                stmt.setNull(5, Types.INTEGER);
                stmt.setNull(6, Types.LONGVARCHAR);
            }
            stmt.setInt(7, roadGoal.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int roadGoalId) {
        String sql = "DELETE FROM road_goals WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roadGoalId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}