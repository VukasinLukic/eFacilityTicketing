package com.efacility.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO implements DomainDTO {
    private long openCount;
    private long assignedCount;
    private long inProgressCount;
    private long completedCount;
    private long closedCount;
    private long totalCount;
}
