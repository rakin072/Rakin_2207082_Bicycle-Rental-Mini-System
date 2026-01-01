package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.Overdue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class OverdueController {

    @FXML private TableView<Overdue> overdueTable;

    @FXML private TableColumn<Overdue, String> colUser;
    @FXML private TableColumn<Overdue, String> colBike;
    @FXML private TableColumn<Overdue, LocalDate> colRentDate;
    @FXML private TableColumn<Overdue, Long> colDays;
    @FXML private TableColumn<Overdue, Double> colAmount;
    @FXML private Label overdueInfoLabel;

    private final ObservableList<Overdue> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colUser.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        colBike.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        colRentDate.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRentDate()));
        colDays.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getOverdueDays()));
        colAmount.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getOverdueAmount()));

        if (overdueInfoLabel != null) {
            overdueInfoLabel.setText("⚠️ Overdue Rate: " + Overdue.getOverdueRatePerDay() + " Taka per day");
        }

        loadOverdue();
    }

    private void loadOverdue() {

        String sql = """
                SELECT rentals.id AS rentalId, users.username, bicycles.model,
                       rentals.rent_date, rentals.due_date
                FROM rentals
                JOIN users ON rentals.user_id = users.id
                JOIN bicycles ON rentals.bicycle_id = bicycles.id
                WHERE rentals.return_date IS NULL AND rentals.due_date IS NOT NULL
                """;

        list.clear();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                LocalDate rentDate = LocalDate.parse(rs.getString("rent_date"));
                LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
                LocalDate today = LocalDate.now();
                
                // Calculate overdue days only if past due date
                long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);

                if (overdueDays > 0) {
                    list.add(new Overdue(
                            rs.getInt("rentalId"),
                            rs.getString("model"),
                            rs.getString("username"),
                            rentDate,
                            overdueDays
                    ));
                }
            }

            overdueTable.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToDashboard() {
        try {
            // Load dashboard content
            Pane view = FXMLLoader.load(getClass().getResource("/com/example/bicyclerentalsystem/view/dashboard_content.fxml"));
            
            // Get the root BorderPane from the scene (dashboard_view.fxml)
            BorderPane root = (BorderPane) overdueTable.getScene().getRoot();
            
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
