package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.User;
import com.example.bicyclerentalsystem.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private TextField hintField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText().trim();
        String hint = hintField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate all fields
        if (username.isEmpty() || hint.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Check if password is strong enough
        if (newPassword.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters long!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Get user from database
        User user = DatabaseHelper.getUserByUsername(username);
        if (user == null) {
            messageLabel.setText("Username not found!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Verify hint (case-insensitive comparison)
        String storedHint = user.getHint();
        if (storedHint == null || !storedHint.trim().equalsIgnoreCase(hint)) {
            messageLabel.setText("Incorrect hint! Please try again.");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        // Update password
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        boolean success = DatabaseHelper.updatePassword(username, hashedPassword);

        if (success) {
            messageLabel.setText("Password reset successful!");
            messageLabel.setStyle("-fx-text-fill: #22c55e;");
            
            // Close dialog after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> handleCancel());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            messageLabel.setText("Failed to reset password. Please try again.");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
