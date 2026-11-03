package com.agro.dao;

import com.agro.db.DatabaseConnection;
import com.agro.models.Treatment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDao {

    public void create(Treatment treatment) throws SQLException {
        String sql = "INSERT INTO treatments (problem_id, treatment_name, planned_date, is_completed) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, treatment.getProblemId());
            stmt.setString(2, treatment.getTreatmentName());
            stmt.setDate(3, Date.valueOf(treatment.getPlannedDate()));
            stmt.setBoolean(4, treatment.isCompleted());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    treatment.setId(keys.getInt(1));
                }
            }
        }
    }

    public Treatment findById(int id) throws SQLException {
        String sql = "SELECT * FROM treatments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Treatment(
                            rs.getInt("id"),
                            rs.getInt("problem_id"),
                            rs.getString("treatment_name"),
                            rs.getDate("planned_date").toLocalDate(),
                            rs.getBoolean("is_completed")
                    );
                }
            }
        }
        return null;
    }

    public List<Treatment> findAll() throws SQLException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Treatment(
                        rs.getInt("id"),
                        rs.getInt("problem_id"),
                        rs.getString("treatment_name"),
                        rs.getDate("planned_date").toLocalDate(),
                        rs.getBoolean("is_completed")
                ));
            }
        }
        return list;
    }

    public boolean update(Treatment treatment) throws SQLException {
        String sql = "UPDATE treatments SET problem_id = ?, treatment_name = ?, planned_date = ?, is_completed = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, treatment.getProblemId());
            stmt.setString(2, treatment.getTreatmentName());
            stmt.setDate(3, Date.valueOf(treatment.getPlannedDate()));
            stmt.setBoolean(4, treatment.isCompleted());
            stmt.setInt(5, treatment.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM treatments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}