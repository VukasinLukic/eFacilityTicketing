package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.StatusTiketa;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IstorijaTiketaDTO implements DomainDTO {
    private Long id;
    private StatusTiketa oldStatus;
    private StatusTiketa newStatus;
    private LocalDateTime changedAt;
    private KorisnikDTO changedBy;
}
