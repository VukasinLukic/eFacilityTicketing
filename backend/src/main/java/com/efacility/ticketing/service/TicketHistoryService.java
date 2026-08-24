package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.TicketHistoryDTO;
import com.efacility.ticketing.mapper.TicketHistoryMapper;
import com.efacility.ticketing.model.Ticket;
import com.efacility.ticketing.model.TicketHistory;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.model.enums.TicketStatus;
import com.efacility.ticketing.repository.TicketHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketHistoryService {

    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketHistoryMapper ticketHistoryMapper;

    public TicketHistoryService(TicketHistoryRepository ticketHistoryRepository,
                                TicketHistoryMapper ticketHistoryMapper) {
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketHistoryMapper = ticketHistoryMapper;
    }

    public void createHistoryEntry(Ticket ticket, User changedBy, TicketStatus oldStatus, TicketStatus newStatus) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setChangedBy(changedBy);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        ticketHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<TicketHistoryDTO> getHistoryByTicket(Long ticketId) {
        return ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId)
                .stream()
                .map(ticketHistoryMapper::toDomainDTO)
                .collect(Collectors.toList());
    }
}
