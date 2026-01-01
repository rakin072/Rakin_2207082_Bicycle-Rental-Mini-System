package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.UserSession;
import com.example.bicyclerentalsystem.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProfileController {

    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField hintField;
    
    @FXML private Label myBicyclesLabel;
    @FXML private Label totalRentalsLabel;
    @FXML private Label activeRentalsLabel;
    @FXML private Label pendingMessagesLabel;
    @FXML private Label overdueChargesLabel;
    
    @FXML private TableView<RentalHistory> rentalHistoryTable;
    @FXML private TableColumn<RentalHistory, String> colHistoryBike;
    @FXML private TableColumn<RentalHistory, String> colHistoryRentDate;
    @FXML private TableColumn<RentalHistory, String> colHistoryReturnDate;
    @FXML private TableColumn<RentalHistory, Integer> colHistoryDuration;
    @FXML private TableColumn<RentalHistory, String> colHistoryStatus;
    
    private final ObservableList<RentalHistory> historyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUserProfile();
        loadStatistics();
        loadRentalHistory();
    }
    
    private void setupTableColumns() {
        colHistoryBike.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        colHistoryRentDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRentDate()));
        colHistoryReturnDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getReturnDate()));
        colHistoryDuration.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDuration()));
        colHistoryStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        
        rentalHistoryTable.setItems(historyList);
    }
    
    private void loadUserProfile() {
        String sql = "SELECT username, hint FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                String hint = rs.getString("hint");
                hintField.setText(hint != null ? hint : "");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadStatistics() {
        int userId = UserSession.getUserId();
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            
            // My Bicycles
            String bicyclesSql = "SELECT COUNT(*) as count FROM bicycles WHERE owner_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(bicyclesSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    myBicyclesLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Total Rentals
            String rentalsSql = "SELECT COUNT(*) as count FROM rentals WHERE user_id = ? AND status = 'active'";
            try (PreparedStatement ps = conn.prepareStatement(rentalsSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalRentalsLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Active Rentals
            String activeSql = "SELECT COUNT(*) as count FROM rentals WHERE user_id = ? AND status = 'active' AND return_date IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(activeSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    activeRentalsLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Pending Messages
            String messagesSql = "SELECT COUNT(*) as count FROM messages WHERE receiver_id = ? AND response IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(messagesSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    pendingMessagesLabel.setText(String.valueOf(rs.getInt("count")));
                }
            }
            
            // Overdue Charges
            String overdueChargesSql = "SELECT COALESCE(overdue_charges, 0) as charges FROM users WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(overdueChargesSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double charges = rs.getDouble("charges");
                    overdueChargesLabel.setText(String.format("%.2f Taka", charges));
                    
                    // Change color if there are charges
                    if (charges > 0) {
                        overdueChargesLabel.setStyle("-fx-text-fill: #ff4444;");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadRentalHistory() {
        historyList.clear();
        
        String sql = """
            SELECT b.model, r.rent_date, r.return_date, r.rental_days, r.status
            FROM rentals r
            JOIN bicycles b ON r.bicycle_id = b.id
            WHERE r.user_id = ? AND r.status = 'active'
            ORDER BY r.rent_date DESC
            LIMIT 10
            """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String model = rs.getString("model");
                String rentDate = rs.getString("rent_date");
                String returnDate = rs.getString("return_date");
                int rentalDays = rs.getInt("rental_days");
                
                String status = returnDate == null ? "Active" : "Returned";
                String returnDateStr = returnDate != null ? returnDate : "N/A";
                
                historyList.add(new RentalHistory(model, rentDate, returnDateStr, rentalDays, status));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void updateProfile() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String hint = hintField.getText().trim();
        
        // Validate passwords if user wants to change password
        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 4) {
                showAlert("Invalid Password", "Password must be at least 4 characters long.", Alert.AlertType.WARNING);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showAlert("Password Mismatch", "New password and confirm password do not match.", Alert.AlertType.WARNING);
                return;
            }
            
            // Update password
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            String updatePasswordSql = "UPDATE users SET password = ? WHERE id = ?";
            
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updatePasswordSql)) {
                
                ps.setString(1, hashedPassword);
                ps.setInt(2, UserSession.getUserId());
                ps.executeUpdate();
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update password: " + e.getMessage(), Alert.AlertType.ERROR);
                return;
            }
        }
        
        // Update hint
        String updateHintSql = "UPDATE users SET hint = ? WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateHintSql)) {
            
            ps.setString(1, hint.isEmpty() ? null : hint);
            ps.setInt(2, UserSession.getUserId());
            ps.executeUpdate();
            
            showAlert("Success! ✅", "Your profile has been updated successfully.", Alert.AlertType.INFORMATION);
            resetForm();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update profile: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void resetForm() {
        newPasswordField.clear();
        confirmPasswordField.clear();
        loadUserProfile();
    }
    
    @FXML
    public void backToDashboard() {
        try {
            Pane view = FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/dashboard_content.fxml"));
            BorderPane root = (BorderPane) usernameField.getScene().getRoot();
            Object center = root.getCenter();
            if (center instanceof BorderPane) {
                ((BorderPane) center).setCenter(view);
            } else {
                root.setCenter(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Inner class for rental history
    public static class RentalHistory {
        private final String bicycleModel;
        private final String rentDate;
        private final String returnDate;
        private final int duration;
        private final String status;
        
        public RentalHistory(String bicycleModel, String rentDate, String returnDate, int duration, String status) {
            this.bicycleModel = bicycleModel;
            this.rentDate = rentDate;
            this.returnDate = returnDate;
            this.duration = duration;
            this.status = status;
        }
        
        public String getBicycleModel() { return bicycleModel; }
        public String getRentDate() { return rentDate; }
        public String getReturnDate() { return returnDate; }
        public int getDuration() { return duration; }
        public String getStatus() { return status; }
    }
}
