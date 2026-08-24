package com.rentaCar.rentaCarBackend.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Client implements Serializable, DomainEntity {

    @Id
    @Column(name = "jmbg", nullable = false, updatable = false, unique = true)
    private String jmbg;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "surname", nullable = false, updatable = true)
    private String surname;

    @Column(name = "age", nullable = false, updatable = true)
    private int age;

    @ManyToOne
    // IZMENJENO: Ime kolone je sada 'city_id'
    @JoinColumn(name = "city_id", nullable = false, updatable = true)
    private City city;

    @Column(name = "mobile", nullable = false, updatable = true)
    private String mobile;

    @Column(name = "username", nullable = true, updatable = false)
    private String username;

    @Column(name = "password", nullable = true, updatable = false)
    private String password;

    public Client() {
    }

    public Client(String jmbg, String name, String surname, int age, City city, String mobile, String username,
                  String password) {
        this.jmbg = jmbg;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.city = city;
        this.mobile = mobile;
        this.username = username;
        this.password = password;
    }

    // --- Getteri i Setteri (getTown/setTown promenjeni u getCity/setCity) ---

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Client{" +
                "jmbg='" + jmbg + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", city=" + (city != null ? city.getName() : null) +
                '}';
    }
}