package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Ticket;
import com.efacility.ticketing.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByTenantId(Long tenantId);

    List<Ticket> findByTechnicianId(Long technicianId);

    List<Ticket> findByStatus(TicketStatus status);

    long countByStatus(TicketStatus status);
}
