package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.Rental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class RentalsController {

    @FXML
    private TableView<Rental> rentalsTable;
    @FXML
    private TableColumn<Rental, Integer> idColumn;
    @FXML
    private TableColumn<Rental, String> customerColumn;
    @FXML
    private TableColumn<Rental, String> bicycleColumn;
    @FXML
    private TableColumn<Rental, LocalDate> rentalDateColumn;
    @FXML
    private TableColumn<Rental, LocalDate> returnDateColumn;

    @FXML
    private TextField customerField;
    @FXML
    private TextField bicycleField;
    @FXML
    private DatePicker rentalDatePicker;
    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private final ObservableList<Rental> rentals = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        customerColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCustomerName()));
        bicycleColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBicycleModel()));
        rentalDateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRentalDate()));
        returnDateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getReturnDate()));

        rentalsTable.setItems(rentals);


        rentals.addAll(
                new Rental(1, "John Doe", "Roadster", LocalDate.now(), LocalDate.now().plusDays(3)),
                new Rental(2, "Emily Clark", "Mountain King", LocalDate.now(), LocalDate.now().plusDays(5))
        );

        addButton.setOnAction(e -> addRental());
        editButton.setOnAction(e -> editRental());
        deleteButton.setOnAction(e -> deleteRental());
    }

    private void addRental() {
        if (!customerField.getText().isEmpty() && !bicycleField.getText().isEmpty() &&
                rentalDatePicker.getValue() != null && returnDatePicker.getValue() != null) {

            Rental r = new Rental(
                    customerField.getText(),
                    bicycleField.getText(),
                    rentalDatePicker.getValue(),
                    returnDatePicker.getValue()
            );

            r.setId(rentals.size() + 1);
            rentals.add(r);

            clearInputs();
        }
    }

    private void editRental() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setCustomerName(customerField.getText());
            selected.setBicycleModel(bicycleField.getText());
            selected.setRentalDate(rentalDatePicker.getValue());
            selected.setReturnDate(returnDatePicker.getValue());

            rentalsTable.refresh();
            clearInputs();
        }
    }

    private void deleteRental() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            rentals.remove(selected);
        }
    }

    private void clearInputs() {
        customerField.clear();
        bicycleField.clear();
        rentalDatePicker.setValue(null);
        returnDatePicker.setValue(null);
    }
}
