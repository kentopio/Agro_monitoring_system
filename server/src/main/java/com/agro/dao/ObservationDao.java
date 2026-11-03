package com.agro.dao;

import com.agro.db.DatabaseConnection;
import com.agro.models.Observation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObservationDao {
    public void create(Observation obs) throws SQLException {
        String sql = "INSERT INTO observations (field_id, observation_date, inspector_name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, obs.getFieldId());
            stmt.setDate(2, Date.valueOf(obs.getObservationDate()));
            stmt.setString(3, obs.getInspectorName());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    obs.setId(keys.getInt(1));
                }
            }
        }
    }
    public Observation findById(int id) throws SQLException {
        String sql = "SELECT * FROM observations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Observation(
                            rs.getInt("id"),
                            rs.getInt("field_id"),
                            rs.getDate("observation_date").toLocalDate(),
                            rs.getString("inspector_name")
                    );
                }
            }
        }
        return null;
    }
    public List<Observation> findAll() throws SQLException {
        List<Observation> list = new ArrayList<>();
        String sql = "SELECT * FROM observations";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Observation(
                        rs.getInt("id"),
                        rs.getInt("field_id"),
                        rs.getDate("observation_date").toLocalDate(),
                        rs.getString("inspector_name")
                ));
            }
        }
        return list;
    }
    public boolean update(Observation obs) throws SQLException {
        String sql = "UPDATE observations SET field_id = ?, observation_date = ?, inspector_name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, obs.getFieldId());
            stmt.setDate(2, Date.valueOf(obs.getObservationDate()));
            stmt.setString(3, obs.getInspectorName());
            stmt.setInt(4, obs.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM observations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}