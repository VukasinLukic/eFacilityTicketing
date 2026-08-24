/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.JPARepo;

import com.rentaCar.rentaCarBackend.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Saki
 */
public interface CarModelRepository extends JpaRepository<CarModel, Integer> {

}
