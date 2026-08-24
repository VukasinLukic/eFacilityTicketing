package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Priority;
import com.efacility.ticketing.model.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDTO implements DomainDTO {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO tenant;
    private UserDTO manager;
    private UserDTO technician;
    private ApartmentDTO apartment;
}
