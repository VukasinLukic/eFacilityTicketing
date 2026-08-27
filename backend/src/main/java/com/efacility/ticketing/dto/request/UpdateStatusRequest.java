package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.StatusTiketa;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Long ticketId;

    @NotNull
    private StatusTiketa newStatus;
}
