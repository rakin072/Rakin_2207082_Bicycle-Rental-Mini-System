package com.example.bicyclerentalsystem.model;

import java.time.LocalDate;

public class Overdue {

    private int rentalId;
    private String bicycleModel;
    private String username;
    private LocalDate rentDate;
    private long overdueDays;
    private double overdueAmount;
    private static final double OVERDUE_RATE_PER_DAY = 50.0; // 50 taka per day

    public Overdue(int rentalId, String bicycleModel, String username,
                   LocalDate rentDate, long overdueDays) {
        this.rentalId = rentalId;
        this.bicycleModel = bicycleModel;
        this.username = username;
        this.rentDate = rentDate;
        this.overdueDays = overdueDays;
        this.overdueAmount = overdueDays * OVERDUE_RATE_PER_DAY;
    }

    public int getRentalId() { return rentalId; }
    public String getBicycleModel() { return bicycleModel; }
    public String getUsername() { return username; }
    public LocalDate getRentDate() { return rentDate; }
    public long getOverdueDays() { return overdueDays; }
    public double getOverdueAmount() { return overdueAmount; }
    public static double getOverdueRatePerDay() { return OVERDUE_RATE_PER_DAY; }
}
