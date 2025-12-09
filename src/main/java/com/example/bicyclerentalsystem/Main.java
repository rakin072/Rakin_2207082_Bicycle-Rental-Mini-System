package com.example.bicyclerentalsystem;

import com.example.bicyclerentalsystem.model.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Initialize database
            DatabaseHelper.initializeDatabase();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/bicyclerentalsystem/view/login_view.fxml")
            );

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Bicycle Rental System");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load Login View!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
