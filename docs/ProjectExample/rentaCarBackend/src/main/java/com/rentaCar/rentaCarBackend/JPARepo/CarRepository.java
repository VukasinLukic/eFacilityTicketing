/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.JPARepo;

import com.rentaCar.rentaCarBackend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author Saki
 */
public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByLicensePlateStartsWith(String licensePlate);

    List<Car> findByStatus(int status);

}
