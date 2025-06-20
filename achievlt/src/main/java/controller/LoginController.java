package controller.package;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DBUtil.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Login berhasil
                System.out.println("Login berhasil: " + username);
                // TODO: pindah ke halaman homepage
            } else {
                errorLabel.setText("Username atau password salah.");
                errorLabel.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Koneksi gagal.");
            errorLabel.setVisible(true);
        }
    }
}
