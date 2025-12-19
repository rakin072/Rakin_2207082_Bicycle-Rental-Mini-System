package com.example.bicyclerentalsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private BorderPane contentPane;
    private DashboardContentController currentDashboardController;

    @FXML
    public void initialize() {
        loadView("dashboard_content.fxml");
    }

    @FXML
    private void showDashboard() {
        if (currentDashboardController != null) {
            currentDashboardController.refresh();
        } else {
            loadView("dashboard_content.fxml");
        }
    }

    @FXML
    private void showBicycles() {
        currentDashboardController = null;
        loadView("bicycles_view.fxml");
    }

    @FXML
    private void showRentals() {
        currentDashboardController = null;
        loadView("rentals_view.fxml");
    }

    @FXML
    private void showOverdue() {
        currentDashboardController = null;
        loadView("overdue_view.fxml");
    }

    @FXML
    private void showMessages() {
        currentDashboardController = null;
        loadView("messages_view.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/login_view.fxml"))
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        handleLogout();
    }

    private void loadView(String file) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bicyclerentalsystem/view/" + file));
            javafx.scene.Parent view = loader.load();
            
            if (file.equals("dashboard_content.fxml")) {
                currentDashboardController = loader.getController();
            }
            
            contentPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
            contentPane.setCenter(new Label("Failed to load: " + file));
        }
    }
}
