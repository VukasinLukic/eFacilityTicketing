package com.efacility.ticketing.dto;

import lombok.Data;

@Data
public class ApartmentDTO implements DomainDTO {
    private Long id;
    private String number;
    private int floor;
    private BuildingDTO building;
}
