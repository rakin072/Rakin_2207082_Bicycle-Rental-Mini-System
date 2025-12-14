package com.example.bicyclerentalsystem.model;

public class UserSession {

    private static int userId = -1;
    private static User loggedUser = null;

    // Store only the ID (used mostly for DB queries)
    public static void setUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }

    public static boolean isLoggedIn() {
        return userId != -1;
    }

    public static void logout() {
        userId = -1;
        loggedUser = null;
    }

    // Store the full user object
    public static void setLoggedUser(User user) {
        loggedUser = user;
        if (user != null) {
            userId = user.getId(); // keep values in sync
        }
    }

    public static User getLoggedUser() {
        return loggedUser;
    }
}
