package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.KorisnikDTO;
import com.efacility.ticketing.model.Korisnik;

import java.util.List;

public interface KorisnikService {
    KorisnikDTO getCurrentKorisnik(Korisnik currentKorisnik);

    List<KorisnikDTO> getAllTechnicians();

    List<KorisnikDTO> getAllTenants();
}
