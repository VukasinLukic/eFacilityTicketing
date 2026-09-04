package com.efacility.ticketing.service.impl;

import com.efacility.ticketing.dto.DashboardStatsDTO;
import com.efacility.ticketing.model.enums.StatusTiketa;
import com.efacility.ticketing.repository.TiketRepository;
import com.efacility.ticketing.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TiketRepository ticketRepository;

    public DashboardServiceImpl(TiketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardStatsDTO getStats() {
        long openCount = ticketRepository.countByStatus(StatusTiketa.OPEN);
        long assignedCount = ticketRepository.countByStatus(StatusTiketa.ASSIGNED);
        long inProgressCount = ticketRepository.countByStatus(StatusTiketa.IN_PROGRESS);
        long completedCount = ticketRepository.countByStatus(StatusTiketa.COMPLETED);
        long closedCount = ticketRepository.countByStatus(StatusTiketa.CLOSED);
        long totalCount = openCount + assignedCount + inProgressCount + completedCount + closedCount;

        return new DashboardStatsDTO(openCount, assignedCount, inProgressCount, completedCount, closedCount, totalCount);
    }
}
