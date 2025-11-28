package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.Bicycle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BicyclesController {

    @FXML
    private TableView<Bicycle> bicyclesTable;

    @FXML
    private TableColumn<Bicycle, Integer> idColumn;

    @FXML
    private TableColumn<Bicycle, String> modelColumn;

    @FXML
    private TableColumn<Bicycle, String> typeColumn;

    @FXML
    private TextField modelField;

    @FXML
    private TextField typeField;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final ObservableList<Bicycle> bicycleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        modelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModel()));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        bicyclesTable.setItems(bicycleList);

        // Example data
        bicycleList.addAll(
                new Bicycle(1, "Roadster", "Road Bike"),
                new Bicycle(2, "Mountain King", "Mountain Bike")
        );

        // Button actions
        addButton.setOnAction(e -> addBicycle());
        editButton.setOnAction(e -> editBicycle());
        deleteButton.setOnAction(e -> deleteBicycle());
    }

    private void addBicycle() {
        if (!modelField.getText().isEmpty() && !typeField.getText().isEmpty()) {
            Bicycle b = new Bicycle(modelField.getText(), typeField.getText());
            b.setId(bicycleList.size() + 1); // Simple ID assignment
            bicycleList.add(b);
            modelField.clear();
            typeField.clear();
        }
    }

    private void editBicycle() {
        Bicycle selected = bicyclesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setModel(modelField.getText());
            selected.setType(typeField.getText());
            bicyclesTable.refresh();
            modelField.clear();
            typeField.clear();
        }
    }

    private void deleteBicycle() {
        Bicycle selected = bicyclesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            bicycleList.remove(selected);
        }
    }
}
