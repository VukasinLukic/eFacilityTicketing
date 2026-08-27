package com.efacility.ticketing.dto;

import lombok.Data;

@Data
public class ZgradaDTO implements DomainDTO {
    private Long id;
    private String name;
    private String address;
}
