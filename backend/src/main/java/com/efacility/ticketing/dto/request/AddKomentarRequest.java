package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddKomentarRequest {

    @NotNull(message = "Id tiketa je obavezan")
    private Long ticketId;

    @NotBlank(message = "Tekst komentara je obavezan")
    private String message;
}
