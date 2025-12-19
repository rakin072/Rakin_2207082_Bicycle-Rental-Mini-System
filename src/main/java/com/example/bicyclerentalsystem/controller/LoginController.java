package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.User;
import com.example.bicyclerentalsystem.model.UserSession;
import com.example.bicyclerentalsystem.utils.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/bicyclerentalsystem/view/forgot_password_view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to open forgot password dialog!");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields!");
            return;
        }

        try {
            User user = DatabaseHelper.getUserByUsername(username);

            if (user == null) {
                messageLabel.setText("User not found!");
                return;
            }

            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                messageLabel.setText("Incorrect password!");
                return;
            }

            // Store logged-in user
            UserSession.setLoggedUser(user);

            // Load dashboard
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/dashboard_view.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Login failed!");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/register_view.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
