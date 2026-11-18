package com.agro.dao;

import com.agro.db.DatabaseConnection;
import com.agro.models.Problem;
import com.agro.models.ProblemStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemDao {

    public void create(Problem problem) throws SQLException {
        String sql = "INSERT INTO problems (observation_id, description, status, f1_infected_area, f2_severity, f3_sensitivity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, problem.getObservationId());
            stmt.setString(2, problem.getDescription());
            stmt.setString(3, problem.getStatus().name());
            stmt.setInt(4, problem.getF1InfectedArea());
            stmt.setInt(5, problem.getF2Severity());
            stmt.setInt(6, problem.getF3Sensitivity());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    problem.setId(keys.getInt(1));
                }
            }
        }
    }

    public Problem findById(int id) throws SQLException {
        String sql = "SELECT * FROM problems WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Problem> findAll() throws SQLException {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT * FROM problems";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean update(Problem problem) throws SQLException {
        String sql = "UPDATE problems SET observation_id = ?, description = ?, status = ?, f1_infected_area = ?, f2_severity = ?, f3_sensitivity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, problem.getObservationId());
            stmt.setString(2, problem.getDescription());
            stmt.setString(3, problem.getStatus().name());
            stmt.setInt(4, problem.getF1InfectedArea());
            stmt.setInt(5, problem.getF2Severity());
            stmt.setInt(6, problem.getF3Sensitivity());
            stmt.setInt(7, problem.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM problems WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Problem mapRow(ResultSet rs) throws SQLException {
        return new Problem(
                rs.getInt("id"),
                rs.getInt("observation_id"),
                rs.getString("description"),
                ProblemStatus.valueOf(rs.getString("status")),
                rs.getInt("f1_infected_area"),
                rs.getInt("f2_severity"),
                rs.getInt("f3_sensitivity")
        );
    }
}