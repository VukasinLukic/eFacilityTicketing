package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateZgradaRequest {

    @NotBlank(message = "Naziv zgrade je obavezan")
    private String name;

    @NotBlank(message = "Adresa je obavezna")
    private String address;
}
