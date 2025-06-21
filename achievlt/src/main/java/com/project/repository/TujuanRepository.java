package com.project.repository;

import com.project.config.DBUtil;
import com.project.model.Tujuan;
import com.project.model.RoadGoal;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TujuanRepository {

    public Tujuan save(Tujuan tujuan) {
        String sql = "INSERT INTO tujuan (user_id, nama_tujuan, deadline) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tujuan.getUserId());
            stmt.setString(2, tujuan.getNamaTujuan());
            stmt.setDate(3, tujuan.getDeadline() != null ? Date.valueOf(tujuan.getDeadline()) : null);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tujuan.setId(generatedKeys.getInt(1));
                        tujuan.setCreatedAt(LocalDateTime.now()); // Set creation time
                    }
                }
                return tujuan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tujuan> findByUserId(int userId) {
        List<Tujuan> tujuanList = new ArrayList<>();
        String sql = "SELECT id, user_id, nama_tujuan, deadline, created_at FROM tujuan WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String namaTujuan = rs.getString("nama_tujuan");
                LocalDate deadline = rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null;
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Tujuan tujuan = new Tujuan(id, userId, namaTujuan, deadline, createdAt);
                tujuan.setRoadGoals(new RoadGoalRepository().findByTujuanId(id)); // Load road goals
                tujuanList.add(tujuan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tujuanList;
    }

    public Tujuan findById(int id) {
        String sql = "SELECT id, user_id, nama_tujuan, deadline, created_at FROM tujuan WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String namaTujuan = rs.getString("nama_tujuan");
                LocalDate deadline = rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null;
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Tujuan tujuan = new Tujuan(id, userId, namaTujuan, deadline, createdAt);
                tujuan.setRoadGoals(new RoadGoalRepository().findByTujuanId(id));
                return tujuan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Tujuan tujuan) {
        String sql = "UPDATE tujuan SET nama_tujuan = ?, deadline = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tujuan.getNamaTujuan());
            stmt.setDate(2, tujuan.getDeadline() != null ? Date.valueOf(tujuan.getDeadline()) : null);
            stmt.setInt(3, tujuan.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int tujuanId) {
        String sql = "DELETE FROM tujuan WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tujuanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}