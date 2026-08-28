package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.StatusTiketa;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "Id tiketa je obavezan")
    private Long ticketId;

    @NotNull(message = "Novi status je obavezan")
    private StatusTiketa newStatus;
}
