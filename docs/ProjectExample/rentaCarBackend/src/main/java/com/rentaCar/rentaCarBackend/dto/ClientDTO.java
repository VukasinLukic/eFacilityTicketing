package com.rentaCar.rentaCarBackend.dto;

import java.io.Serializable;

public class ClientDTO implements DomainDTO, Serializable {

    private String jmbg;
    private String name;
    private String surname;
    private int age;
    private CityDTO city; // town -> city
    private String mobile;
    private String username;
    private String password;

    public ClientDTO() {
    }

    public ClientDTO(String jmbg, String name, String surname, int age, CityDTO city, String mobile, String username, String password) {
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

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
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
        return "ClientDTO{" +
                "jmbg='" + jmbg + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", city=" + (city != null ? city.getName() : null) +
                '}';
    }
}