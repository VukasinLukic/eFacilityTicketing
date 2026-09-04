package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Stan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StanRepository extends JpaRepository<Stan, Long> {

    List<Stan> findByBuildingId(Long buildingId);

    List<Stan> findByTenantId(Long tenantId);

    Optional<Stan> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByBuildingId(Long buildingId);
}
