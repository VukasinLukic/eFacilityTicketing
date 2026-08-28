package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.Prioritet;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePrioritetRequest {

    @NotNull(message = "Id tiketa je obavezan")
    private Long ticketId;

    @NotNull(message = "Prioritet je obavezan")
    private Prioritet priority;
}
