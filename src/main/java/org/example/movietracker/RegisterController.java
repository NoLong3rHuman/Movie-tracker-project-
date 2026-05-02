package org.example.movietracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Password must be at least 6 characters.");
            return;
        }

        MovieDatabase db = new MovieDatabase();

        if (!db.testConnection()) {
            showAlert(Alert.AlertType.ERROR, "Cannot connect to the database. Please check your connection settings.");
            return;
        }

        int result = db.registerUser(email, password);

        if (result == 1) {
            showAlert(Alert.AlertType.INFORMATION, "Account created! Please sign in.");
            MovieTrackerApp.showLoginScreen();
        } else if (result == 0) {
            showAlert(Alert.AlertType.WARNING, "That email is already registered. Please sign in.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration failed due to a database error. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        MovieTrackerApp.showLoginScreen();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Register");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
