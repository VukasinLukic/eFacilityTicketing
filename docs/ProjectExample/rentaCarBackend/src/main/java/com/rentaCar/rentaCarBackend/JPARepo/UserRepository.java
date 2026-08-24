/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.JPARepo;

import com.rentaCar.rentaCarBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Saki
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
