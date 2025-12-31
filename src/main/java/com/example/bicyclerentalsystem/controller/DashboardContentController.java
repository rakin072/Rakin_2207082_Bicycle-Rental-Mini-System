package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DashboardContentController {

    @FXML private Label totalBicyclesLabel;
    @FXML private Label availableBicyclesLabel;
    @FXML private Label rentedBicyclesLabel;
    @FXML private Label totalRentalsLabel;

    @FXML private TableView<BicycleModelInfo> bicycleModelsTable;
    @FXML private TableColumn<BicycleModelInfo, String> colModel;
    @FXML private TableColumn<BicycleModelInfo, String> colType;
    @FXML private TableColumn<BicycleModelInfo, Integer> colCount;

    @FXML private TableView<RecentRental> recentRentalsTable;
    @FXML private TableColumn<RecentRental, String> colRentalBike;
    @FXML private TableColumn<RecentRental, String> colRentDate;
    @FXML private TableColumn<RecentRental, String> colStatus;

    private final ObservableList<BicycleModelInfo> modelList = FXCollections.observableArrayList();
    private final ObservableList<RecentRental> rentalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();
    }

    public void refresh() {
        loadDashboardData();
    }

    private void setupTableColumns() {
        colModel.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getModel()));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        colCount.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCount()));

        colRentalBike.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        colRentDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRentDate()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        bicycleModelsTable.setItems(modelList);
        recentRentalsTable.setItems(rentalList);
    }

    private void loadDashboardData() {
        int userId = UserSession.getUserId();

        // Load statistics
        loadStatistics(userId);
        
        // Load bicycle models
        loadBicycleModels(userId);
        
        // Load recent rentals
        loadRecentRentals(userId);
    }

    private void loadStatistics(int userId) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            
            // Total bicycles - count all bicycles from all users
            String totalSql = "SELECT COUNT(*) as total FROM bicycles";
            try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalBicyclesLabel.setText(String.valueOf(rs.getInt("total")));
                }
            }

            // Available bicycles - count OTHER users' bicycles available for this user to rent
            String availableSql = "SELECT COUNT(*) as available FROM bicycles WHERE owner_id != ? AND isAvailable = 1";
            try (PreparedStatement ps = conn.prepareStatement(availableSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    availableBicyclesLabel.setText(String.valueOf(rs.getInt("available")));
                }
            }

            // Currently Rented - count active rentals made BY this user (not bicycles owned by them)
            String rentedSql = """
                SELECT COUNT(*) as rented 
                FROM rentals
                WHERE user_id = ? AND return_date IS NULL AND status = 'active'
                """;
            try (PreparedStatement ps = conn.prepareStatement(rentedSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    rentedBicyclesLabel.setText(String.valueOf(rs.getInt("rented")));
                }
            }

            // Total rentals
            String rentalsSql = "SELECT COUNT(*) as total FROM rentals WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(rentalsSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalRentalsLabel.setText(String.valueOf(rs.getInt("total")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBicycleModels(int userId) {
        modelList.clear();

        String sql = "SELECT model, type, COUNT(*) as count FROM bicycles WHERE owner_id = ? GROUP BY model, type";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelList.add(new BicycleModelInfo(
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getInt("count")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentRentals(int userId) {
        rentalList.clear();

        String sql = """
            SELECT b.model, r.rent_date, r.return_date, r.status
            FROM rentals r
            JOIN bicycles b ON r.bicycle_id = b.id
            WHERE r.user_id = ? AND r.status = 'active'
            ORDER BY r.rent_date DESC
            LIMIT 5
            """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String status = rs.getString("return_date") == null ? "Active" : "Returned";
                rentalList.add(new RecentRental(
                    rs.getString("model"),
                    rs.getString("rent_date"),
                    status
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class for bicycle model info
    public static class BicycleModelInfo {
        private final String model;
        private final String type;
        private final int count;

        public BicycleModelInfo(String model, String type, int count) {
            this.model = model;
            this.type = type;
            this.count = count;
        }

        public String getModel() { return model; }
        public String getType() { return type; }
        public int getCount() { return count; }
    }

    // Inner class for recent rental info
    public static class RecentRental {
        private final String bicycleModel;
        private final String rentDate;
        private final String status;

        public RecentRental(String bicycleModel, String rentDate, String status) {
            this.bicycleModel = bicycleModel;
            this.rentDate = rentDate;
            this.status = status;
        }

        public String getBicycleModel() { return bicycleModel; }
        public String getRentDate() { return rentDate; }
        public String getStatus() { return status; }
    }
}
