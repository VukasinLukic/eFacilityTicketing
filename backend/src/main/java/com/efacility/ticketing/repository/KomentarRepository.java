package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Komentar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KomentarRepository extends JpaRepository<Komentar, Long> {

    List<Komentar> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
