package com.project.controller;

import com.project.Main;
import com.project.model.User;
import com.project.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorMessageLabel;

    private UserService userService;

    public RegisterController() {
        userService = new UserService();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessageLabel.setText("Semua field harus diisi.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessageLabel.setText("Password dan konfirmasi password tidak cocok.");
            return;
        }

        User newUser = userService.registerUser(username, password);
        if (newUser != null) {
            System.out.println("Registrasi Berhasil untuk user: " + newUser.getUsername());
            errorMessageLabel.setText("Registrasi berhasil! Silakan masuk.");
            // Optionally clear fields or go back to login
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } else {
            errorMessageLabel.setText("Registrasi gagal. Username mungkin sudah ada.");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Main.setRoot("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}