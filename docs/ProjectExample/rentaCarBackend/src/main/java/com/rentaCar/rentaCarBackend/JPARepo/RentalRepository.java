/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.JPARepo;

import com.rentaCar.rentaCarBackend.model.Client;
import com.rentaCar.rentaCarBackend.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * @author Saki
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByClient(Client client);

    List<Rental> findByStatus(int status);

    @Query(value = "SELECT * FROM adoption ", nativeQuery = true)
    List<Rental> getAllAdoptions();
}
