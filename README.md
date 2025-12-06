# Rakin_2207082_Bicycle-Rental-Mini-System
# Bicycle Rental Mini System  

A simple JavaFX desktop application to manage bicycle rentals using SQLite.

## Features
- Manage bicycles (CRUD)
- Record rentals and returns
- Track overdue rentals
- Simple, clean JavaFX UI with FXML

## Requirements
- Java 17+
- JavaFX SDK
- SQLite (included as JDBC file `bicyclerental.db` will be created)

## How to run
1. Import the project into your IDE as a Maven/Gradle or plain Java project.
2. Add JavaFX SDK to module path and set `--module-path` and `--add-modules javafx.controls,javafx.fxml` when running.
3. Run `com.example.bikerental.Main`.

## Database
See `schema.sql` to create the `bicycles` and `rentals` tables.

Project Structure:

src/
└── main/
    ├── java/
    │   └── com/example/bicyclerentalsystem/
    │       ├── Main.java
    │       ├── App.java
    │       │
    │       ├── controller/
    │       │   ├── LoginController.java
    │       │   ├── RegisterController.java
    │       │   ├── DashboardController.java
    │       │   ├── BicyclesController.java
    │       │   ├── RentalsController.java
    │       │   ├── OverdueController.java
    │       │
    │       ├── model/
    │       │   ├── User.java
    │       │   ├── Bicycle.java
    │       │   ├── Rental.java
    │       │   ├── Overdue.java
    │       │   ├── UserSession.java
    │       │   ├── DatabaseHelper.java
    │       │
    │       └── util/
    │           └── PasswordUtil.java
    │
    └── resources/
        └── com/example/bicyclerentalsystem/
            │
            ├── css/
            │   └── style.css
            │
            ├── login_view.fxml
            ├── register_view.fxml
            ├── dashboard_view.fxml
            ├── bicycles_view.fxml
            ├── rentals_view.fxml
            ├── overdue_view.fxml
            │
            └── database.db       <-- SQLite DATABASE FILE
