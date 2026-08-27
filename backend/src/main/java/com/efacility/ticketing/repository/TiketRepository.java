package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.StatusTiketa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TiketRepository extends JpaRepository<Tiket, Long>, JpaSpecificationExecutor<Tiket> {

    List<Tiket> findByTenantId(Long tenantId);

    List<Tiket> findByTechnicianId(Long technicianId);

    List<Tiket> findByStatus(StatusTiketa status);

    long countByStatus(StatusTiketa status);
}
