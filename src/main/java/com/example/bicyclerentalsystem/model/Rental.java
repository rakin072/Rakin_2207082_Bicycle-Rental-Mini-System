package com.example.bicyclerentalsystem.model;

import java.time.LocalDate;

public class Rental {

    private int id;
    private String customerName;
    private String bicycleModel;
    private LocalDate rentalDate;
    private LocalDate returnDate;

    public Rental(int id, String customerName, String bicycleModel, LocalDate rentalDate, LocalDate returnDate) {
        this.id = id;
        this.customerName = customerName;
        this.bicycleModel = bicycleModel;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public Rental(String customerName, String bicycleModel, LocalDate rentalDate, LocalDate returnDate) {
        this.customerName = customerName;
        this.bicycleModel = bicycleModel;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBicycleModel() {
        return bicycleModel;
    }

    public void setBicycleModel(String bicycleModel) {
        this.bicycleModel = bicycleModel;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
