package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.Rental;
import com.example.bicyclerentalsystem.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class RentalsController {

    @FXML private ComboBox<String> bicycleDropdown;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private TableView<Rental> rentalsTable;

    @FXML private TableColumn<Rental, String> colBike;
    @FXML private TableColumn<Rental, LocalDate> colRentDate;
    @FXML private TableColumn<Rental, Integer> colDuration;
    @FXML private TableColumn<Rental, LocalDate> colDueDate;
    @FXML private TableColumn<Rental, String> colStatus;

    private final ObservableList<Rental> rentalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize duration spinner (1-7 days)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 7, 1);
        durationSpinner.setValueFactory(valueFactory);
        durationSpinner.setEditable(true);
        
        loadAvailableBicycles();
        loadRentals();

        colBike.setCellValueFactory(c -> {
            // Fetch bicycle model name from database
            String model = getBicycleModel(c.getValue().getBicycleId());
            return new javafx.beans.property.SimpleStringProperty(model);
        });
        colRentDate.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRentDate()));
        colDuration.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRentalDays()));
        colDueDate.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getDueDate()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getReturnDate() != null ? "Returned" : "Active"));
    }

    private void loadAvailableBicycles() {
        bicycleDropdown.getItems().clear();

        // Exclude user's own bicycles from rental list
        String sql = "SELECT id, model FROM bicycles WHERE isAvailable = 1 AND owner_id != ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, UserSession.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bicycleDropdown.getItems().add(rs.getInt("id") + " - " + rs.getString("model"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRentals() {
        rentalList.clear();

        String sql = """
                SELECT r.id, r.user_id, r.bicycle_id, r.rent_date, r.rental_days, r.due_date, r.return_date
                FROM rentals r
                WHERE r.user_id = ? AND r.status = 'active'
                ORDER BY r.rent_date DESC
                """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, UserSession.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rental r = new Rental();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setBicycleId(rs.getInt("bicycle_id"));
                    r.setRentDate(LocalDate.parse(rs.getString("rent_date")));
                    
                    int rentalDays = rs.getInt("rental_days");
                    r.setRentalDays(rentalDays);
                    
                    String dueDateStr = rs.getString("due_date");
                    if (dueDateStr != null && !dueDateStr.isEmpty()) {
                        r.setDueDate(LocalDate.parse(dueDateStr));
                    }
                    
                    String returnDateStr = rs.getString("return_date");
                    if (returnDateStr != null && !returnDateStr.isEmpty()) {
                        r.setReturnDate(LocalDate.parse(returnDateStr));
                    }
                    
                    rentalList.add(r);
                }
            }

            rentalsTable.setItems(rentalList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void rentBicycle() {

        if (bicycleDropdown.getSelectionModel().isEmpty()) {
            showAlert("No Selection", "Please select a bicycle to rent.", Alert.AlertType.WARNING);
            return;
        }

        // Check if user has unpaid overdue charges
        double overdueCharges = getOverdueCharges();
        if (overdueCharges > 0) {
            showAlert("Unpaid Overdue Charges", 
                     "You have unpaid overdue charges of " + String.format("%.2f", overdueCharges) + " Taka.\n\n" +
                     "Please clear your overdue charges before renting a new bicycle.\n" +
                     "You can pay through the Messages section by sending a payment confirmation with transaction ID.", 
                     Alert.AlertType.ERROR);
            return;
        }

        // Check if user already has an active rental
        if (hasActiveRental()) {
            showAlert("Active Rental Exists", 
                     "You already have an active bicycle rental. Please return it before renting another bicycle.", 
                     Alert.AlertType.WARNING);
            return;
        }

        int bikeId = Integer.parseInt(bicycleDropdown.getValue().split(" - ")[0]);
        int rentalDays = durationSpinner.getValue();
        LocalDate rentDate = LocalDate.now();
        LocalDate dueDate = rentDate.plusDays(rentalDays);

        // Create a pending rental (status will be 'pending' until owner approves)
        String rentSql = "INSERT INTO rentals(user_id, bicycle_id, rent_date, rental_days, due_date, status) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement rentPs = conn.prepareStatement(rentSql)) {

            rentPs.setInt(1, UserSession.getUserId());
            rentPs.setInt(2, bikeId);
            rentPs.setString(3, rentDate.toString());
            rentPs.setInt(4, rentalDays);
            rentPs.setString(5, dueDate.toString());
            rentPs.setString(6, "pending");
            rentPs.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rental Request Created");
            alert.setHeaderText(null);
            alert.setContentText("Your rental request has been created!\n\nRental Period: " + rentalDays + " day(s)\nDue Date: " + dueDate + "\n\nYou will now be taken to the Messages page to send payment details to the bicycle owner.");
            alert.showAndWait();
            
            // Navigate to messages page after alert is closed
            navigateToMessages();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create rental request. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void navigateToMessages() {
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.scene.Node view = FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/messages_view.fxml"));
                
                // Get the scene from the rentalsTable
                if (rentalsTable.getScene() == null) {
                    System.err.println("Scene is null!");
                    return;
                }
                
                BorderPane root = (BorderPane) rentalsTable.getScene().getRoot();
                
                // Get the contentPane which is the center of the root
                Object center = root.getCenter();
                if (center instanceof BorderPane) {
                    // contentPane is still a BorderPane
                    ((BorderPane) center).setCenter(view);
                } else {
                    // contentPane was replaced, so set view directly to root's center
                    root.setCenter(view);
                }
            } catch (Exception e) {
                System.err.println("Navigation error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private boolean hasActiveRental() {
        String sql = "SELECT COUNT(*) as count FROM rentals WHERE user_id = ? AND return_date IS NULL AND status = 'active'";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private double getOverdueCharges() {
        String sql = "SELECT COALESCE(overdue_charges, 0) as charges FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, UserSession.getUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("charges");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getBicycleModel(int bicycleId) {
        String sql = "SELECT model FROM bicycles WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bicycleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("model");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Bike #" + bicycleId;
    }

    @FXML
    public void returnBicycle() {
        Rental selectedRental = rentalsTable.getSelectionModel().getSelectedItem();
        
        if (selectedRental == null) {
            showAlert("No Selection", "Please select a rental from the table to return.", Alert.AlertType.WARNING);
            return;
        }
        
        if (selectedRental.getReturnDate() != null) {
            showAlert("Already Returned", "This bicycle has already been returned.", Alert.AlertType.INFORMATION);
            return;
        }
        
        LocalDate returnDate = LocalDate.now();
        LocalDate dueDate = selectedRental.getDueDate();
        
        // Calculate overdue amount if return date is past due date
        long overdueDays = 0;
        double overdueAmount = 0.0;
        final double OVERDUE_RATE_PER_DAY = 50.0; // 50 Taka per day
        
        if (dueDate != null && returnDate.isAfter(dueDate)) {
            overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            overdueAmount = overdueDays * OVERDUE_RATE_PER_DAY;
        }
        
        // Confirm return with overdue information
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Return Bicycle #" + selectedRental.getBicycleId());
        
        String contentText = "Are you sure you want to return this bicycle?\n\n" +
                           "Rent Date: " + selectedRental.getRentDate() + "\n" +
                           "Due Date: " + selectedRental.getDueDate() + "\n" +
                           "Return Date: " + returnDate;
        
        if (overdueDays > 0) {
            contentText += "\n\n⚠️ OVERDUE CHARGES:\n" +
                          "Overdue Days: " + overdueDays + " day(s)\n" +
                          "Overdue Amount: " + overdueAmount + " Taka\n" +
                          "(Rate: " + OVERDUE_RATE_PER_DAY + " Taka per day)\n\n" +
                          "This amount will be added to your profile.";
        }
        
        confirmAlert.setContentText(contentText);
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        
        String updateRentalSql = "UPDATE rentals SET return_date = ? WHERE id = ?";
        String updateBicycleSql = "UPDATE bicycles SET isAvailable = 1 WHERE id = ?";
        String updateUserOverdueSql = "UPDATE users SET overdue_charges = COALESCE(overdue_charges, 0) + ? WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement rentPs = conn.prepareStatement(updateRentalSql);
             PreparedStatement bikePs = conn.prepareStatement(updateBicycleSql);
             PreparedStatement userPs = conn.prepareStatement(updateUserOverdueSql)) {
            
            // Update rental with return date
            rentPs.setString(1, returnDate.toString());
            rentPs.setInt(2, selectedRental.getId());
            rentPs.executeUpdate();
            
            // Mark bicycle as available
            bikePs.setInt(1, selectedRental.getBicycleId());
            bikePs.executeUpdate();
            
            // Update user's overdue charges if applicable
            if (overdueAmount > 0) {
                userPs.setDouble(1, overdueAmount);
                userPs.setInt(2, UserSession.getUserId());
                userPs.executeUpdate();
            }
            
            String successMessage = "Bicycle returned successfully!\n\n" +
                                  "The bicycle is now available for others to rent.";
            
            if (overdueDays > 0) {
                successMessage += "\n\n⚠️ Overdue charges of " + overdueAmount + 
                                " Taka have been added to your profile.\n" +
                                "Please settle this amount.";
            }
            
            showAlert("Success! ✅", successMessage, Alert.AlertType.INFORMATION);
            
            loadRentals();
            loadAvailableBicycles();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return bicycle. Please try again.\n\nError: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void backToDashboard() {
        try {
            // Load dashboard content
            Pane view = FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/dashboard_content.fxml"));
            
            // Get the root BorderPane from the scene (dashboard_view.fxml)
            BorderPane root = (BorderPane) rentalsTable.getScene().getRoot();
            
            // Get the contentPane which is the center of the root
            Object center = root.getCenter();
            if (center instanceof BorderPane) {
                // contentPane is still a BorderPane
                ((BorderPane) center).setCenter(view);
            } else {
                // contentPane was replaced, so set view directly to root's center
                root.setCenter(view);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
