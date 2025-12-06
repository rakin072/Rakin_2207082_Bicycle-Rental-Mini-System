package com.example.bicyclerentalsystem.model;

public class Overdue {

    private int rentalId;
    private String customerName;
    private String bicycle;
    private String dueDate;
    private int daysOverdue;
    private double overdueFee;

    public Overdue(int rentalId, String customerName, String bicycle, String dueDate, int daysOverdue, double overdueFee) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.bicycle = bicycle;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.overdueFee = overdueFee;
    }

    public int getRentalId() { return rentalId; }
    public String getCustomerName() { return customerName; }
    public String getBicycle() { return bicycle; }
    public String getDueDate() { return dueDate; }
    public int getDaysOverdue() { return daysOverdue; }
    public double getOverdueFee() { return overdueFee; }

    public void setRentalId(int rentalId) { this.rentalId = rentalId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setBicycle(String bicycle) { this.bicycle = bicycle; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setDaysOverdue(int daysOverdue) { this.daysOverdue = daysOverdue; }
    public void setOverdueFee(double overdueFee) { this.overdueFee = overdueFee; }
}
