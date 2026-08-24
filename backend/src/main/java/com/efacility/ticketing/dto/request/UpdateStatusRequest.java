package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Long ticketId;

    @NotNull
    private TicketStatus newStatus;
}
