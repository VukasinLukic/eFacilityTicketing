package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.IstorijaTiketaDTO;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.StatusTiketa;

import java.util.List;

public interface IstorijaTiketaService {
    void createHistoryEntry(Tiket ticket, Korisnik changedBy,
                            StatusTiketa oldStatus, StatusTiketa newStatus);

    List<IstorijaTiketaDTO> getHistoryByTiket(Long ticketId, Korisnik currentKorisnik);
}
