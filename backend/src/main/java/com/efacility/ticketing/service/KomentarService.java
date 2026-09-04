package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.KomentarDTO;
import com.efacility.ticketing.dto.request.AddKomentarRequest;
import com.efacility.ticketing.model.Korisnik;

import java.util.List;

public interface KomentarService {
    List<KomentarDTO> getKomentarsByTiket(Long ticketId, Korisnik currentKorisnik);

    KomentarDTO addKomentar(AddKomentarRequest request, Korisnik currentKorisnik);
}
