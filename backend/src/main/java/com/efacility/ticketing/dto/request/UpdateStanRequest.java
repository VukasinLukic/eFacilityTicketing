package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStanRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String number;

    @NotNull
    private Integer floor;

    @NotNull
    private Long buildingId;
}
