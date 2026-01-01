package com.example.bicyclerentalsystem.model;

public class User {

    private int id;
    private String username;
    private String passwordHash; // stored hashed password
    private String hint; // password recovery hint
    private double overdueCharges; // accumulated overdue charges

    public User() {}

    public User(int id, String username, String passwordHash, String hint) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.hint = hint;
        this.overdueCharges = 0.0;
    }
    
    public User(int id, String username, String passwordHash, String hint, double overdueCharges) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.hint = hint;
        this.overdueCharges = overdueCharges;
    }

    public User(String username, String passwordHash, String hint) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.hint = hint;
        this.overdueCharges = 0.0;
    }

    // Keep old constructors for backward compatibility
    public User(int id, String username, String passwordHash) {
        this(id, username, passwordHash, null);
    }

    public User(String username, String passwordHash) {
        this(username, passwordHash, null);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
    
    public double getOverdueCharges() {
        return overdueCharges;
    }
    
    public void setOverdueCharges(double overdueCharges) {
        this.overdueCharges = overdueCharges;
    }

    // Optional alias to match older controllers
    public String getPassword() {
        return passwordHash;
    }
}
