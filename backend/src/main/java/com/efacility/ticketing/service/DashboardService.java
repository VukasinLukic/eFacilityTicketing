package com.efacility.ticketing.service;

import com.efacility.ticketing.dto.DashboardStatsDTO;
import com.efacility.ticketing.model.enums.TicketStatus;
import com.efacility.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardStatsDTO getStats() {
        long openCount = ticketRepository.countByStatus(TicketStatus.OPEN);
        long assignedCount = ticketRepository.countByStatus(TicketStatus.ASSIGNED);
        long inProgressCount = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long completedCount = ticketRepository.countByStatus(TicketStatus.COMPLETED);
        long closedCount = ticketRepository.countByStatus(TicketStatus.CLOSED);
        long totalCount = openCount + assignedCount + inProgressCount + completedCount + closedCount;

        return new DashboardStatsDTO(openCount, assignedCount, inProgressCount, completedCount, closedCount, totalCount);
    }
}
