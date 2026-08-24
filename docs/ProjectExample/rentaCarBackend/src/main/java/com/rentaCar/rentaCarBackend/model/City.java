package com.rentaCar.rentaCarBackend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class City implements Serializable, DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private int id;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    public City() {
    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // IZMENJENO: 'Town' u 'City'
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}