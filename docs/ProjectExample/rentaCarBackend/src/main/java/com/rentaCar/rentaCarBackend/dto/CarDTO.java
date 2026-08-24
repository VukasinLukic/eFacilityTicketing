package com.rentaCar.rentaCarBackend.dto;

import java.io.Serializable;

public class CarDTO implements Serializable {

    private int id;
    private String licensePlate;
    private int year;
    private int status;
    private String notes;
    private CarModelDTO carModel; // Sadrži sve ostale detalje

    // --- Getters and Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public CarModelDTO getCarModel() { return carModel; }
    public void setCarModel(CarModelDTO carModel) { this.carModel = carModel; }
}