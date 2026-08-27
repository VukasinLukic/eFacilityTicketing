package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Stan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StanRepository extends JpaRepository<Stan, Long> {

    List<Stan> findByBuildingId(Long buildingId);
}
