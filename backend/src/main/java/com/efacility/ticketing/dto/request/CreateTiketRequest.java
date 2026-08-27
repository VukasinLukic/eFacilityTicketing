package com.efacility.ticketing.dto.request;

import com.efacility.ticketing.model.enums.Prioritet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTiketRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Prioritet priority;

    @NotNull
    private Long apartmentId;
}
