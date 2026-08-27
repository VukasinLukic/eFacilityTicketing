package com.efacility.ticketing.repository;

import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Uloga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {

    Optional<Korisnik> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Korisnik> findByRole(Uloga role);
}
