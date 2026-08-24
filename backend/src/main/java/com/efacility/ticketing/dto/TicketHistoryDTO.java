package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketHistoryDTO implements DomainDTO {
    private Long id;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private LocalDateTime changedAt;
    private UserDTO changedBy;
}
