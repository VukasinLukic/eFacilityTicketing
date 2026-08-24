package com.rentaCar.rentaCarBackend.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Car implements Serializable, DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int status; // 1 = Доступан, 0 = Изнајмљен

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY је боља перформанса
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

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
    public CarModel getCarModel() { return carModel; }
    public void setCarModel(CarModel carModel) { this.carModel = carModel; }
}