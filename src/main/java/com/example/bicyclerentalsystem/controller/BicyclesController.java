package com.example.bicyclerentalsystem.controller;

import com.example.bicyclerentalsystem.model.Bicycle;
import com.example.bicyclerentalsystem.model.DatabaseHelper;
import com.example.bicyclerentalsystem.model.UserSession;
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

public class BicyclesController {

    @FXML private TextField modelField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TableView<Bicycle> bicyclesTable;

    @FXML private TableColumn<Bicycle, String> colModel;
    @FXML private TableColumn<Bicycle, String> colType;
    @FXML private TableColumn<Bicycle, Boolean> colAvailable;

    private final ObservableList<Bicycle> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize type dropdown with options
        typeComboBox.getItems().addAll("electric", "physical");
        
        colModel.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getModel()));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        colAvailable.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isAvailable()));

        bicyclesTable.setItems(list);
        loadUserBicycles();
    }

    private void loadUserBicycles() {
        list.clear();

        int uid = UserSession.getUserId();

        String sql = "SELECT * FROM bicycles WHERE owner_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, uid);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Bicycle b = new Bicycle();
                    b.setId(rs.getInt("id"));
                    b.setModel(rs.getString("model"));
                    b.setType(rs.getString("type"));
                    b.setAvailable(rs.getInt("isAvailable") == 1);
                    b.setOwnerId(uid);

                    list.add(b);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addBicycle() {

        String model = modelField.getText().trim();
        String type = typeComboBox.getValue();

        if (model.isEmpty() || type == null || type.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a model name and select a type.");
            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO bicycles(model, type, isAvailable, owner_id) VALUES (?, ?, 1, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, model);
            ps.setString(2, type);
            ps.setInt(3, UserSession.getUserId());

            ps.executeUpdate();
            modelField.clear();
            typeComboBox.getSelectionModel().clearSelection();
            loadUserBicycles();

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
            BorderPane root = (BorderPane) bicyclesTable.getScene().getRoot();
            
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
