package com.rentaCar.rentaCarBackend.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Rental implements Serializable, DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IZMENJENO: Ime kolone i polja
    @Column(name = "rental_id", nullable = false, updatable = false, unique = true)
    private int rentalId;

    // IZMENJENO: 'date' je sada 'startDate'
    @Column(name = "start_date", nullable = false, updatable = true)
    private LocalDate startDate;

    // DODATO: 'endDate' je ključno za rent-a-car
    @Column(name = "end_date", nullable = false, updatable = true)
    private LocalDate endDate;

    @Column(name = "notes", nullable = true, updatable = true)
    private String notes;

    @ManyToOne
    // IZMENJENO: Ime kolone i polja
    @JoinColumn(name = "car_id", nullable = false, updatable = true)
    private Car car;

    @ManyToOne
    // IZMENJENO: Ime kolone i polja (referencira JMBG klijenta)
    @JoinColumn(name = "client_jmbg", nullable = false, updatable = true)
    private Client client;

    @Column(name = "status", nullable = false, updatable = true)
    private int status;

    @ManyToOne
    // IZMENJENO: Ime kolone i polja
    @JoinColumn(name = "user_id", nullable = true, updatable = true)
    private User user;

    public Rental() {
    }

    public Rental(int rentalId, LocalDate startDate, LocalDate endDate, String notes, Car car, Client client, int status, User user) {
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", car=" + (car != null ? car.getLicensePlate() : null) +
                ", client=" + (client != null ? client.getName() : null) +
                ", status=" + status +
                '}';
    }
}