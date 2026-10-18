package com.agro;

import com.agro.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Перевірка підключення до бази даних MySQL...");
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("УСПІХ! З'єднання з базою agro_monitoring_db успішно встановлено!");
            }
        } catch (SQLException e) {
            System.err.println("ПОМИЛКА підключення до бази даних: " + e.getMessage());
        }
    }
}