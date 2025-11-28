package com.example.bicyclerentalsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/example/bicyclerentalsystem/main_view.fxml"));

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass()
                .getResource("/com/example/bicyclerentalsystem/css/style.css")
                .toExternalForm());

        stage.setTitle("Bicycle Rental System");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
