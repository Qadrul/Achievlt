package com.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.project.config.DBUtil; // Import DBUtil

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Aplikasi Pengelola Tujuan");
        primaryStage.setResizable(false); // Mengatur agar tidak bisa di-resize

        // Memuat FXML login sebagai halaman awal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1024, 768); // Ukuran jendela 1024x768
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Optional: Test database connection on startup
        try (Connection connection = DBUtil.getConnection()) {
            if (connection != null) {
                System.out.println("Koneksi database berhasil!");
            }
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Metode untuk mengganti scene
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + fxml + ".fxml"));
        Parent root = loader.load();
        primaryStage.getScene().setRoot(root);
    }

    // Metode untuk mengganti scene dengan com.project.controller yang sudah di-set
    public static void setRoot(String fxml, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + fxml + ".fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.getScene().setRoot(root);
    }
// Di Main.java, setelah membuat scene:


    public static void main(String[] args) {
        launch();
    }
}