package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.TiketDTO;
import com.efacility.ticketing.dto.request.AssignTiketRequest;
import com.efacility.ticketing.dto.request.CreateTiketRequest;
import com.efacility.ticketing.dto.request.UpdatePrioritetRequest;
import com.efacility.ticketing.dto.request.UpdateStatusRequest;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TiketService {
    TiketDTO createTiket(CreateTiketRequest request, Korisnik currentKorisnik);

    Page<TiketDTO> getAllTikets(StatusTiketa status, Prioritet priority, Long buildingId,
                                String search, Pageable pageable);

    List<TiketDTO> getMyTikets(Long tenantId);

    List<TiketDTO> getAssignedTikets(Long technicianId);

    TiketDTO getTiket(Long ticketId, Korisnik currentKorisnik);

    TiketDTO assignTechnician(AssignTiketRequest request, Korisnik manager);

    TiketDTO updateStatus(UpdateStatusRequest request, Korisnik currentKorisnik);

    TiketDTO updatePrioritet(UpdatePrioritetRequest request, Korisnik currentKorisnik);
}
