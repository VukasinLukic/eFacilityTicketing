package com.efacility.ticketing.dto;

import com.efacility.ticketing.model.enums.Uloga;
import lombok.Data;

@Data
public class KorisnikDTO implements DomainDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Uloga role;
}
