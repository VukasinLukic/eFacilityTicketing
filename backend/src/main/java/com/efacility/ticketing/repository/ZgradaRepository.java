package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Zgrada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ZgradaRepository extends JpaRepository<Zgrada, Long> {

    List<Zgrada> findByNameContainingIgnoreCase(String name);

    // Server-side paginacija: baza vraća samo jednu stranicu (LIMIT/OFFSET), ne sve zgrade.
    @Query("SELECT z FROM Zgrada z ORDER BY z.id ASC")
    Page<Zgrada> findAllPaged(Pageable pageable);
}
