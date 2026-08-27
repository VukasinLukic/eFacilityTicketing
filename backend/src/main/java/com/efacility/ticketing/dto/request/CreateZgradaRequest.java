package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateZgradaRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;
}
