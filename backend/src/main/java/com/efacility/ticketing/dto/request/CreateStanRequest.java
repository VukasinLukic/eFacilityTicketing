package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStanRequest {

    @NotBlank(message = "Broj stana je obavezan")
    private String number;

    @NotNull(message = "Sprat je obavezan")
    private Integer floor;

    @NotNull(message = "Zgrada je obavezna")
    private Long buildingId;
}
