package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTiketRequest {

    @NotNull
    private Long ticketId;

    @NotNull
    private Long technicianId;
}
