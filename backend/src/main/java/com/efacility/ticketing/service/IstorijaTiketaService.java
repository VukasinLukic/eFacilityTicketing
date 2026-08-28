package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.IstorijaTiketaDTO;
import com.efacility.ticketing.exception.ResourceNotFoundException;
import com.efacility.ticketing.mapper.IstorijaTiketaMapper;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.IstorijaTiketa;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.repository.IstorijaTiketaRepository;
import com.efacility.ticketing.repository.TiketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IstorijaTiketaService {

    private final IstorijaTiketaRepository ticketHistoryRepository;
    private final TiketRepository ticketRepository;
    private final IstorijaTiketaMapper ticketHistoryMapper;
    private final TiketPristup tiketPristup;

    public IstorijaTiketaService(IstorijaTiketaRepository ticketHistoryRepository,
                                TiketRepository ticketRepository,
                                IstorijaTiketaMapper ticketHistoryMapper,
                                TiketPristup tiketPristup) {
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketRepository = ticketRepository;
        this.ticketHistoryMapper = ticketHistoryMapper;
        this.tiketPristup = tiketPristup;
    }

    public void createHistoryEntry(Tiket ticket, Korisnik changedBy, StatusTiketa oldStatus, StatusTiketa newStatus) {
        IstorijaTiketa history = new IstorijaTiketa();
        history.setTicket(ticket);
        history.setChangedBy(changedBy);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        ticketHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<IstorijaTiketaDTO> getHistoryByTiket(Long ticketId, Korisnik currentKorisnik) {
        Tiket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Tiket nije pronađen, id: " + ticketId));
        tiketPristup.proveriPristup(ticket, currentKorisnik);

        return ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(ticketHistoryMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}
