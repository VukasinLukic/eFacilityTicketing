package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.IstorijaTiketaDTO;
import com.efacility.ticketing.mapper.IstorijaTiketaMapper;
import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.IstorijaTiketa;
import com.efacility.ticketing.model.Korisnik;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.repository.IstorijaTiketaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IstorijaTiketaService {

    private final IstorijaTiketaRepository ticketHistoryRepository;
    private final IstorijaTiketaMapper ticketHistoryMapper;

    public IstorijaTiketaService(IstorijaTiketaRepository ticketHistoryRepository,
                                IstorijaTiketaMapper ticketHistoryMapper) {
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketHistoryMapper = ticketHistoryMapper;
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
    public List<IstorijaTiketaDTO> getHistoryByTiket(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(ticketHistoryMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}
