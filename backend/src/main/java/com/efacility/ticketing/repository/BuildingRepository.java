package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByNameContainingIgnoreCase(String name);
}
