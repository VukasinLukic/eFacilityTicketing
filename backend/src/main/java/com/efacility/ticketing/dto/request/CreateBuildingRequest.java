package com.efacility.ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBuildingRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;
}
