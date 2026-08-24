package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBuildingRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;
}
