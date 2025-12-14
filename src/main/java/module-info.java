module com.bicyclerentalsystem {

    /* JavaFX */
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    /* Database */
    requires java.sql;

    /* SQLite JDBC + logging */
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;

    /* JavaFX reflection access */
    opens com.example.bicyclerentalsystem to javafx.fxml;
    opens com.example.bicyclerentalsystem.controller to javafx.fxml;
    opens com.example.bicyclerentalsystem.model to javafx.base;

    /* Public API */
    exports com.example.bicyclerentalsystem;
    exports com.example.bicyclerentalsystem.controller;
}
