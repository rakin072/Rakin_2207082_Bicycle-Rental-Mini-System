package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.Overdue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OverdueController {

    @FXML
    private TableView<Overdue> overdueTable;

    @FXML
    private TableColumn<Overdue, Integer> colRentalId;

    @FXML
    private TableColumn<Overdue, String> colCustomer;

    @FXML
    private TableColumn<Overdue, String> colBike;

    @FXML
    private TableColumn<Overdue, String> colDueDate;

    @FXML
    private TableColumn<Overdue, Integer> colDaysOver;

    @FXML
    private TableColumn<Overdue, Double> colFee;

    @FXML
    public void initialize() {

        // Mapping table columns to model fields
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colBike.setCellValueFactory(new PropertyValueFactory<>("bicycle"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colDaysOver.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("overdueFee"));

        // Temporary sample data for UI preview
        overdueTable.setItems(FXCollections.observableArrayList(
                new Overdue(1, "Hasan", "Mountain Bike #2", "2025-02-01", 3, 150.0),
                new Overdue(2, "Rakin", "Road Bike #5", "2025-01-30", 6, 300.0)
        ));
    }
}
