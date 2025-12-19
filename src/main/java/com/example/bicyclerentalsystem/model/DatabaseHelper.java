package com.example.bicyclerentalsystem.model;

import java.sql.*;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:bicycles.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    // Call once in App.java startup
    public static void initializeDatabase() {
        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    hint TEXT
                );
                """;

        String bicyclesTable = """
                CREATE TABLE IF NOT EXISTS bicycles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    model TEXT NOT NULL,
                    type TEXT,
                    isAvailable INTEGER DEFAULT 1,
                    owner_id INTEGER,
                    FOREIGN KEY(owner_id) REFERENCES users(id)
                );
                """;

        String rentalsTable = """
                CREATE TABLE IF NOT EXISTS rentals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    bicycle_id INTEGER NOT NULL,
                    rent_date TEXT NOT NULL,
                    return_date TEXT,
                    rental_days INTEGER DEFAULT 1,
                    due_date TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(bicycle_id) REFERENCES bicycles(id)
                );
                """;

        String messagesTable = """
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    rental_id INTEGER NOT NULL,
                    sender_id INTEGER NOT NULL,
                    receiver_id INTEGER NOT NULL,
                    transaction_id TEXT NOT NULL,
                    message_text TEXT NOT NULL,
                    response TEXT,
                    sent_at TEXT NOT NULL,
                    responded_at TEXT,
                    FOREIGN KEY(rental_id) REFERENCES rentals(id),
                    FOREIGN KEY(sender_id) REFERENCES users(id),
                    FOREIGN KEY(receiver_id) REFERENCES users(id)
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(bicyclesTable);
            stmt.execute(rentalsTable);
            stmt.execute(messagesTable);
            
            // Migration: Add rental_days and due_date columns if they don't exist
            try {
                stmt.execute("ALTER TABLE rentals ADD COLUMN rental_days INTEGER DEFAULT 1");
                System.out.println("Added rental_days column to rentals table");
            } catch (Exception e) {
                // Column already exists, ignore
            }
            
            try {
                stmt.execute("ALTER TABLE rentals ADD COLUMN due_date TEXT");
                System.out.println("Added due_date column to rentals table");
            } catch (Exception e) {
                // Column already exists, ignore
            }
            
            // Migration: Add hint column to users table if it doesn't exist
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN hint TEXT");
                System.out.println("Added hint column to users table");
            } catch (Exception e) {
                // Column already exists, ignore
            }

            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    //  USER QUERIES
    // -------------------------------------------------------------------------

    /**
     * Returns User object if username exists, otherwise null.
     */
    public static User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("hint")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // Not found
    }

    /**
     * Returns User object if hint matches, otherwise null.
     */
    public static User getUserByHint(String hint) {
        String query = "SELECT * FROM users WHERE hint = ? COLLATE NOCASE";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, hint);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("hint")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // Not found
    }

    /**
     * Inserts a new user into database.
     */
    public static void insertUser(String username, String hashedPassword, String hint) {
        String query = "INSERT INTO users(username, password, hint) VALUES(?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, hint);

            ps.executeUpdate();
            System.out.println("User registered: " + username);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Updates user's password.
     */
    public static boolean updatePassword(String username, String newHashedPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, newHashedPassword);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated for user: " + username);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
