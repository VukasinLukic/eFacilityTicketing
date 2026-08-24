package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    List<Apartment> findByBuildingId(Long buildingId);
}
