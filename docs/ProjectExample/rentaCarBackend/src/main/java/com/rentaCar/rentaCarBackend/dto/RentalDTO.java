package com.rentaCar.rentaCarBackend.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RentalDTO implements DomainDTO, Serializable {

    private int rentalId;
    private LocalDate startDate;
    private LocalDate endDate; // DODATO POLJE
    private String notes;
    private CarDTO car;
    private ClientDTO client;
    private int status;
    private UserDTO user;

    public RentalDTO() {
    }

    public RentalDTO(int rentalId, LocalDate startDate, LocalDate endDate, String notes, CarDTO car, ClientDTO client, int status, UserDTO user) {
        this.rentalId = rentalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.car = car;
        this.client = client;
        this.status = status;
        this.user = user;
    }

    // --- Ažurirani Getteri i Setteri ---

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RentalDTO{" +
                "rentalId=" + rentalId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", car=" + (car != null ? car.getLicensePlate() : null) +
                ", client=" + (client != null ? client.getName() : null) +
                ", status=" + status +
                '}';
    }
}