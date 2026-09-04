package com.efacility.ticketing.dto;

import lombok.Data;

@Data
public class StanDTO implements DomainDTO {
    private Long id;
    private String number;
    private int floor;
    private ZgradaDTO building;
    private KorisnikDTO tenant;
}
