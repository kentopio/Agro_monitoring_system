package com.agro.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.Proxy;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/agro_monitoring_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8&autoReconnect=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection sharedConnection;
    public static synchronized Connection getConnection() throws SQLException {
        if (sharedConnection == null || sharedConnection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                sharedConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver не знайдено!", e);
            }
        }
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        return null;
                    }
                    return method.invoke(sharedConnection, args);
                }
        );
    }
}