package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TiketDTO implements DomainDTO {
    private Long id;
    private String title;
    private String description;
    private StatusTiketa status;
    private Prioritet priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private KorisnikDTO tenant;
    private KorisnikDTO manager;
    private KorisnikDTO technician;
    private StanDTO apartment;
}
