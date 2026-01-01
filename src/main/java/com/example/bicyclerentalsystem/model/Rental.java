package com.example.bicyclerentalsystem.model;

import java.time.LocalDate;

public class Rental {

    private int id;
    private int userId;
    private int bicycleId;
    private LocalDate rentDate;
    private LocalDate returnDate;
    private int rentalDays;
    private LocalDate dueDate;

    public Rental() {}

    public Rental(int id, int userId, int bicycleId, LocalDate rentDate, LocalDate returnDate) {
        this.id = id;
        this.userId = userId;
        this.bicycleId = bicycleId;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
    }

    public Rental(int userId, int bicycleId, LocalDate rentDate) {
        this.userId = userId;
        this.bicycleId = bicycleId;
        this.rentDate = rentDate;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getBicycleId() { return bicycleId; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getRentalDays() { return rentalDays; }
    public LocalDate getDueDate() { return dueDate; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setBicycleId(int bicycleId) { this.bicycleId = bicycleId; }
    public void setRentDate(LocalDate rentDate) { this.rentDate = rentDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
