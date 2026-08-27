package com.efacility.ticketing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KomentarDTO implements DomainDTO {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private KorisnikDTO user;
}
