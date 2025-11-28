package com.example.bicyclerentalsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;


    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bicyclerentalsystem/" + fxmlFile));
            Node view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to load: " + fxmlFile);
        }
    }

    @FXML
    private void showBicycles() {
        loadView("bicycles_view.fxml");
    }

    @FXML
    private void showRentals() {
        loadView("rentals_view.fxml");
    }

    @FXML
    private void showOverdue() {
        loadView("overdue_view.fxml");
    }
}
