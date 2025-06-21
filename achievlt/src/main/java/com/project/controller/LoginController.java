package com.project.controller;

import com.project.Main;
import com.project.model.User;
import com.project.service.UserService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    private UserService userService;

    public LoginController() {
        userService = new UserService();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userService.loginUser(username, password);
        if (user != null) {
            System.out.println("Login Berhasil!");
            errorMessageLabel.setText("");
            // Simulate storing logged in user (e.g., in a static context or session manager)
            // For simplicity, we'll pass the user ID to the Home Controller
            try {
                // Initialize HomeController with the logged-in user's ID
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Homepage.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setLoggedInUserId(user.getId()); // Pass user ID
                homeController.initialize(); // Manually call initialize if needed for data loading

                // Apply Fade + Slide Up transition
                root.setOpacity(0);
                root.setTranslateY(Main.getPrimaryStage().getHeight());

                FadeTransition fade = new FadeTransition(Duration.millis(500), root);
                fade.setFromValue(0);
                fade.setToValue(1);

                TranslateTransition slide = new TranslateTransition(Duration.millis(500), root);
                slide.setFromY(Main.getPrimaryStage().getHeight());
                slide.setToY(0);

                fade.play();
                slide.play();

                Main.getPrimaryStage().getScene().setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
                errorMessageLabel.setText("Gagal memuat halaman utama.");
            }
        } else {
            errorMessageLabel.setText("Username atau password salah.");
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        try {
            Main.setRoot("Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        // Ini adalah tombol panah di kanan atas untuk pindah ke homepage tanpa login
        // Hanya untuk tujuan demonstrasi/navigasi cepat

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Homepage.fxml"));
            Parent root = loader.load();
            HomeController homeController = loader.getController();
            // Set a dummy user ID for demonstration if not logged in
            homeController.setLoggedInUserId(1); // Assuming user ID 1 exists for testing
            homeController.initialize(); // Manually call initialize if needed for data loading

            // Apply Fade + Slide Up transition
            root.setOpacity(0);
            root.setTranslateY(Main.getPrimaryStage().getHeight());

            FadeTransition fade = new FadeTransition(Duration.millis(500), root);
            fade.setFromValue(0);
            fade.setToValue(1);

            TranslateTransition slide = new TranslateTransition(Duration.millis(500), root);
            slide.setFromY(Main.getPrimaryStage().getHeight());
            slide.setToY(0);

            fade.play();
            slide.play();

            Main.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}