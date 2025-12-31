package com.example.bicyclerentalsystem.model;

public class Bicycle {

    private int id;
    private String model;
    private String type;
    private boolean available;
    private int ownerId;

    public Bicycle() {}

    public Bicycle(int id, String model, String type, boolean available, int ownerId) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.available = available;
        this.ownerId = ownerId;
    }

    public Bicycle(String model, String type, int ownerId) {
        this.model = model;
        this.type = type;
        this.available = true;
        this.ownerId = ownerId;
    }

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public boolean isAvailable() { return available; }
    public int getOwnerId() { return ownerId; }

    public void setId(int id) { this.id = id; }
    public void setModel(String model) { this.model = model; }
    public void setType(String type) { this.type = type; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
}
