package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateZgradaRequest {

    @NotNull(message = "Id zgrade je obavezan")
    private Long id;

    @NotBlank(message = "Naziv zgrade je obavezan")
    private String name;

    @NotBlank(message = "Adresa je obavezna")
    private String address;
}
