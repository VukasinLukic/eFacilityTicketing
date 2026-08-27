package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.IstorijaTiketa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IstorijaTiketaRepository extends JpaRepository<IstorijaTiketa, Long> {

    List<IstorijaTiketa> findByTicketIdOrderByChangedAtAsc(Long ticketId);
}
