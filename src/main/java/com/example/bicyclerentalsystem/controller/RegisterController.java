package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!password.equals(confirmPass)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        try {
            if (DatabaseHelper.getUserByUsername(username) != null) {
                messageLabel.setText("Username already taken!");
                return;
            }

            String hashed = PasswordUtil.hashPassword(password);
            DatabaseHelper.insertUser(username, hashed);

            messageLabel.setText("Account created successfully!");

            // Go back to login
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/login_view.fxml"))));

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Registration failed!");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/login_view.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
