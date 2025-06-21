package com.project.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // DETAIL KONEKSI AIVEN ANDA
    private static final String JDBC_URL = "jdbc:mysql://mysql-1aa03021-qadrulzaidan-287a.b.aivencloud.com:25071/achievlt" +
            "?useSSL=true" +
            "&requireSSL=true" +
            "&verifyServerCertificate=true" +
            "&trustCertificateKeyStoreUrl=file:D:\\keystore.jks" + // Pastikan jalur ini benar
            "&trustCertificateKeyStorePassword=qwerty"; // Pastikan password ini benar

    private static final String USER = "avnadmin"; // Username Aiven
    private static final String PASSWORD = "AVNS_VWAomUy1jtSIhmebvZc"; // Password Aiven

    public static Connection getConnection() throws SQLException {
        try {
            // Memuat driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan.");
            throw new SQLException("MySQL JDBC Driver tidak ditemukan.", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database: " + e.getMessage());
            }
        }
    }
}