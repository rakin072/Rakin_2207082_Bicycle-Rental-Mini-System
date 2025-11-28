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
