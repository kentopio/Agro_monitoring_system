package com.agro.dao;

import com.agro.db.DatabaseConnection;
import com.agro.models.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FieldDao {
    public void create(Field field) throws SQLException {
        String sql = "INSERT INTO fields (name, crop_type, area) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, field.getName());
            stmt.setString(2, field.getCropType());
            stmt.setDouble(3, field.getArea());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    field.setId(keys.getInt(1));
                }
            }
        }
    }
    public Field findById(int id) throws SQLException {
        String sql = "SELECT * FROM fields WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Field(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("crop_type"),
                            rs.getDouble("area")
                    );
                }
            }
        }
        return null;
    }
    public List<Field> findAll() throws SQLException {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fields.add(new Field(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("crop_type"),
                        rs.getDouble("area")
                ));
            }
        }
        return fields;
    }
    public boolean update(Field field) throws SQLException {
        String sql = "UPDATE fields SET name = ?, crop_type = ?, area = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, field.getName());
            stmt.setString(2, field.getCropType());
            stmt.setDouble(3, field.getArea());
            stmt.setInt(4, field.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM fields WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}