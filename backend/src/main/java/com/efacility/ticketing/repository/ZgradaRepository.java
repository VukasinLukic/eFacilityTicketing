package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Zgrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZgradaRepository extends JpaRepository<Zgrada, Long> {

    List<Zgrada> findByNameContainingIgnoreCase(String name);
}
