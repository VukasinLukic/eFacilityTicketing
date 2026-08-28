package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.Prioritet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTiketRequest {

    @NotBlank(message = "Naslov je obavezan")
    private String title;

    @NotBlank(message = "Opis je obavezan")
    private String description;

    @NotNull(message = "Prioritet je obavezan")
    private Prioritet priority;

    @NotNull(message = "Stan je obavezan")
    private Long apartmentId;
}
