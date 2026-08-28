package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTiketRequest {

    @NotNull(message = "Id tiketa je obavezan")
    private Long ticketId;

    @NotNull(message = "Tehničar je obavezan")
    private Long technicianId;
}
