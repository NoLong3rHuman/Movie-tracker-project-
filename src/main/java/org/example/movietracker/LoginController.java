package org.example.movietracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter your email and password.");
            return;
        }

        MovieDatabase db = new MovieDatabase();

        if (!db.testConnection()) {
            showAlert(Alert.AlertType.ERROR, "Cannot connect to the database. Please check your connection settings.");
            return;
        }

        User user = db.authenticateUser(email, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid email or password.");
        } else {
            MovieTrackerApp.showMainScreen(user);
        }
    }

    @FXML
    private void handleRegister() {
        MovieTrackerApp.showRegisterScreen();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Login");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
