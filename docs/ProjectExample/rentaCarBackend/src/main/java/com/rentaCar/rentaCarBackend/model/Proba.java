package com.rentaCar.rentaCarBackend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Proba implements Serializable, DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false,updatable = false,unique = true)
    private int id;
    @Column(name = "licence_plate", nullable = false, updatable = true)
    private String licencePlate;

    @Column(name="year", nullable = false, updatable = true)
    private int year;

}
