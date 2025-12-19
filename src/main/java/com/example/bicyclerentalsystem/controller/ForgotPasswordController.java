package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.User;
import com.example.bicyclerentalsystem.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML private VBox hintSection;
    @FXML private VBox resetSection;
    @FXML private TextField hintInputField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private User currentUser;

    @FXML
    private void handleVerifyHint() {
        String enteredHint = hintInputField.getText().trim();

        if (enteredHint.isEmpty()) {
            messageLabel.setText("Please enter your hint!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        try {
            // Find user by hint
            User user = DatabaseHelper.getUserByHint(enteredHint);

            if (user == null) {
                messageLabel.setText("Hint not found! Please check and try again.");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            // Store user for password reset
            currentUser = user;

            // Hint verified, show password reset section
            hintSection.setVisible(false);
            hintSection.setManaged(false);
            resetSection.setVisible(true);
            resetSection.setManaged(true);
            messageLabel.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An error occurred!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill all password fields!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        if (newPassword.length() < 4) {
            messageLabel.setText("Password must be at least 4 characters!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        try {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            boolean success = DatabaseHelper.updatePassword(currentUser.getUsername(), hashedPassword);

            if (success) {
                messageLabel.setText("✅ Password reset successfully!");
                messageLabel.setStyle("-fx-text-fill: #10b981;");
                
                // Close dialog after 1.5 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::handleCancel);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Failed to reset password!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An error occurred while resetting password!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) hintInputField.getScene().getWindow();
        stage.close();
    }
}
