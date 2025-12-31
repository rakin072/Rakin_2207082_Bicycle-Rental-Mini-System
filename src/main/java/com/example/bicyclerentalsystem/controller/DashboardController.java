package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

    @FXML private BorderPane contentPane;  // matches dashboard_view.fxml
    @FXML private Label messageNotificationBadge;
    private DashboardContentController currentDashboardController;

    @FXML
    public void initialize() {
        loadView("dashboard_content.fxml");  // Default screen - show dashboard
        updateMessageNotification();
    }
    
    private void updateMessageNotification() {
        String sql = """
            SELECT COUNT(*) as count 
            FROM messages 
            WHERE receiver_id = ? AND response IS NULL
            """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    messageNotificationBadge.setText(String.valueOf(count));
                    messageNotificationBadge.setVisible(true);
                } else {
                    messageNotificationBadge.setVisible(false);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        // Don't reload the view if we're already on the dashboard - just refresh the data
        if (currentDashboardController != null) {
            currentDashboardController.refresh();
        } else {
            loadView("dashboard_content.fxml");
        }
        updateMessageNotification();
    }

    @FXML
    private void showBicycles() {
        currentDashboardController = null;  // Clear dashboard reference when navigating away
        loadView("bicycles_view.fxml");
        updateMessageNotification();
    }

    @FXML
    private void showRentals() {
        currentDashboardController = null;  // Clear dashboard reference when navigating away
        loadView("rentals_view.fxml");
        updateMessageNotification();
    }

    @FXML
    private void showOverdue() {
        currentDashboardController = null;  // Clear dashboard reference when navigating away
        loadView("overdue_view.fxml");
        updateMessageNotification();
    }

    @FXML
    private void showMessages() {
        currentDashboardController = null;  // Clear dashboard reference when navigating away
        loadView("messages_view.fxml");
        updateMessageNotification();
    }
    
    @FXML
    private void showProfile() {
        currentDashboardController = null;  // Clear dashboard reference when navigating away
        loadView("profile_view.fxml");
        updateMessageNotification();
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
            
            // Store reference to dashboard controller if loading dashboard content
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
