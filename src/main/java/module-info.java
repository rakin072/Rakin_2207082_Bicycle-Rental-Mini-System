module com.bicyclerentalsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.bicyclerentalsystem to javafx.fxml;
    opens com.example.bicyclerentalsystem.controller to javafx.fxml;
    opens com.example.bicyclerentalsystem.model to javafx.base;

    exports com.example.bicyclerentalsystem;
    exports com.example.bicyclerentalsystem.controller;
}
